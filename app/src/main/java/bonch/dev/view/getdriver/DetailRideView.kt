package bonch.dev.view.getdriver

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import bonch.dev.Coordinator
import bonch.dev.MainActivity
import bonch.dev.MainActivity.Companion.hideKeyboard
import bonch.dev.MainActivity.Companion.showKeyboard
import bonch.dev.R
import bonch.dev.model.getdriver.pojo.PaymentCard
import bonch.dev.presenter.getdriver.adapters.PaymentsListAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.directions.DirectionsFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.search.SearchFactory


class DetailRideView : Fragment() {

    private val API_KEY = "6e2e73e8-4a73-42f5-9bf1-35259708af3c"
    private val OFFER_PRICE = "OFFER_PRICE"
    private val CARD_NUMBER = "CARD_NUMBER"
    private val VALID_UNTIL = "VALID_UNTIL"
    private val BANK_IMG = "BANK_IMG"
    private val CVC = "CVC"
    private val FROM = "FROM"
    private val TO = "TO"


    private var mapView: MapView? = null
    private var cardsBottomSheetBehavior: BottomSheetBehavior<*>? = null
    private var commentBottomSheetBehavior: BottomSheetBehavior<*>? = null
    private var paymentsListAdapter: PaymentsListAdapter? = null
    private var coordinator: Coordinator? = null


    private lateinit var getDriverBtn: Button
    private lateinit var backBtn: ImageButton
    private lateinit var offerPrice: TextView
    private lateinit var addCard: TextView
    private lateinit var commentBtn: TextView
    private lateinit var commentDoneBtn: TextView
    private lateinit var commentBackBtn: ImageView
    private lateinit var paymentMethod: TextView
    private lateinit var cardsBottomSheet: LinearLayout
    private lateinit var commentBottomSheet: LinearLayout
    private lateinit var recyclerPayments: RecyclerView
    private lateinit var commentText: EditText
    private lateinit var commentMinText: TextView
    private lateinit var toAddress: TextView
    private lateinit var fromAddress: TextView
    private lateinit var selectedPaymentMethod: LinearLayout
    private lateinit var paymentMethodImg: ImageView
    private lateinit var cardNumberText: TextView
    private lateinit var priceLabelColor: TextView
    private lateinit var onMapView: View
    private lateinit var mainInfoLayout: LinearLayout


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        MapKitFactory.setApiKey(API_KEY)
        MapKitFactory.initialize(context)
        SearchFactory.initialize(context)
        DirectionsFactory.initialize(context)

        //val mapKit = MapKitFactory.getInstance()
        val root = inflater.inflate(R.layout.detail_ride_fragment, container, false)
        initViews(root)

        val bundle = this.arguments
        if (bundle != null) {
            setAddresses(bundle)
        }

        setListener(root)

        setBottomSheet(root)

        if (coordinator == null) {
            coordinator = (activity as MainActivity).coordinator
        }

        initBankCardRecycler()

        moveCamera(Point(60.066971, 30.334))

        return root
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK) {
            val strPrice = data!!.getStringExtra(OFFER_PRICE)

            offerPrice.textSize = 22f
            offerPrice.setTextColor(Color.parseColor("#000000"))
            offerPrice.typeface = Typeface.DEFAULT_BOLD

            if (isPositiveOfferPrice(strPrice.toInt())) {
                priceLabelColor.text = getString(R.string.positive)
                priceLabelColor.background =
                    ContextCompat.getDrawable(context!!, R.drawable.offer_price_positive)
            } else {
                priceLabelColor.text = getString(R.string.negative)
                priceLabelColor.background =
                    ContextCompat.getDrawable(context!!, R.drawable.offer_price_negative)
            }

            priceLabelColor.visibility = View.VISIBLE
            offerPrice.text = strPrice
        }

        if (requestCode == 2 && resultCode == RESULT_OK) {
            val cardNumber = data!!.getStringExtra(CARD_NUMBER)
            val validUntil = data.getStringExtra(VALID_UNTIL)
            val cvc = data.getStringExtra(CVC)
            val img = data.getIntExtra(BANK_IMG, 4)

            val paymentCard = PaymentCard(cardNumber, validUntil, cvc, img)

            paymentsListAdapter!!.list.add(paymentCard)
            paymentsListAdapter!!.notifyDataSetChanged()

        }
    }


    private fun isPositiveOfferPrice(price: Int): Boolean {
        var isPositive = false
        //TODO

        if (price > 300) {
            isPositive = true
        }

        return isPositive
    }


    fun setSelectedBankCard(paymentCard: PaymentCard) {
        selectedPaymentMethod.visibility = View.VISIBLE
        paymentMethod.visibility = View.GONE

        cardNumberText.text = paymentCard.numberCard
        if (paymentCard.img != null) {
            paymentMethodImg.setImageResource(paymentCard.img!!)
        }

        cardsBottomSheetBehavior!!.state = BottomSheetBehavior.STATE_COLLAPSED
    }


    private fun setListener(root: View) {
        offerPrice.setOnClickListener {
            //TODO() to do via coordianator
            val intent = Intent(context, OfferPriceView::class.java)
            startActivityForResult(intent, 1)
        }

        addCard.setOnClickListener {
            //TODO() to do via coordianator
            val intent = Intent(context, AddBankCardView::class.java)
            startActivityForResult(intent, 2)
        }


        commentBtn.setOnClickListener {
            commentBottomSheetBehavior!!.state = BottomSheetBehavior.STATE_EXPANDED

            if (!commentText.isFocused) {
                commentText.requestFocus()
                showKeyboard(activity!!)
            }
        }

        commentBackBtn.setOnClickListener {
            hideKeyboard(activity!!, root)
            commentText.clearFocus()
            commentBottomSheetBehavior!!.state = BottomSheetBehavior.STATE_COLLAPSED

            commentText.setText("")
            commentMinText.text = ""
        }

        commentDoneBtn.setOnClickListener {
            val comment = commentText.text.toString()

            if (comment.trim().isNotEmpty()) {
                commentMinText.text = comment
            }

            hideKeyboard(activity!!, root)
            commentText.clearFocus()
            commentBottomSheetBehavior!!.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        paymentMethod.setOnClickListener {
            cardsBottomSheetBehavior!!.state = BottomSheetBehavior.STATE_EXPANDED
        }

        selectedPaymentMethod.setOnClickListener {
            cardsBottomSheetBehavior!!.state = BottomSheetBehavior.STATE_EXPANDED
        }

        onMapView.setOnClickListener {
            hideKeyboard(activity!!, root)
            commentText.clearFocus()

            commentBottomSheetBehavior!!.state = BottomSheetBehavior.STATE_COLLAPSED
            cardsBottomSheetBehavior!!.state = BottomSheetBehavior.STATE_COLLAPSED
        }

    }


    private fun setAddresses(bundle: Bundle) {
        fromAddress.text = bundle.getString(FROM, "")
        toAddress.text = bundle.getString(TO, "")
    }


    private fun setBottomSheet(root: View) {
        commentBottomSheetBehavior!!.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                if (slideOffset > 0) {
                    onMapView.alpha = slideOffset * 0.8f
                }
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                    hideKeyboard(activity!!, root)
                    commentText.clearFocus()
                }

                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    onMapView.visibility = View.GONE
                    mainInfoLayout.elevation = 30f
                } else {
                    onMapView.visibility = View.VISIBLE
                    mainInfoLayout.elevation = 0f
                }
            }

        })

        cardsBottomSheetBehavior!!.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                if (slideOffset > 0) {
                    onMapView.alpha = slideOffset * 0.8f
                }
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                    hideKeyboard(activity!!, root)
                    commentText.clearFocus()
                }

                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    onMapView.visibility = View.GONE
                    mainInfoLayout.elevation = 30f
                } else {
                    onMapView.visibility = View.VISIBLE
                    mainInfoLayout.elevation = 0f
                }
            }

        })
    }


    private fun initViews(root: View) {
        mapView = root.findViewById(R.id.map) as MapView
        backBtn = root.findViewById(R.id.back_btn)
        getDriverBtn = root.findViewById(R.id.get_driver_btn)
        offerPrice = root.findViewById(R.id.offer_price)
        commentBtn = root.findViewById(R.id.comment_btn)
        commentDoneBtn = root.findViewById(R.id.comment_done)
        commentBackBtn = root.findViewById(R.id.comment_back_btn)
        commentText = root.findViewById(R.id.comment_text)
        commentMinText = root.findViewById(R.id.comment_min_text)
        paymentMethod = root.findViewById(R.id.payment_method)
        cardsBottomSheet = root.findViewById(R.id.cards_bottom_sheet)
        commentBottomSheet = root.findViewById(R.id.comment_bottom_sheet)
        recyclerPayments = root.findViewById(R.id.payments_list)
        addCard = root.findViewById(R.id.add_card)
        fromAddress = root.findViewById(R.id.fromAddress)
        toAddress = root.findViewById(R.id.toAddress)
        selectedPaymentMethod = root.findViewById(R.id.selected_payment_method)
        paymentMethodImg = root.findViewById(R.id.payment_method_img)
        cardNumberText = root.findViewById(R.id.number_card)
        priceLabelColor = root.findViewById(R.id.price_label_color)
        onMapView = root.findViewById(R.id.on_map_view)
        mainInfoLayout = root.findViewById(R.id.main_info_layout)

        cardsBottomSheetBehavior = BottomSheetBehavior.from<View>(cardsBottomSheet)
        commentBottomSheetBehavior = BottomSheetBehavior.from<View>(commentBottomSheet)
    }


    private fun initBankCardRecycler() {
        val list = arrayListOf<PaymentCard>()

        list.add(PaymentCard("", "", "", R.drawable.google_pay))
        paymentsListAdapter =
            PaymentsListAdapter(
                recyclerPayments,
                list,
                context!!,
                this
            )

        recyclerPayments.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerPayments.adapter = paymentsListAdapter
    }


    private fun moveCamera(point: Point) {
        mapView!!.map.move(
            CameraPosition(point, 35.0f, 0.0f, 0.0f),
            Animation(Animation.Type.SMOOTH, 1f),
            null
        )
    }

}