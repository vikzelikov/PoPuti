package bonch.dev.view.getdriver

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import bonch.dev.Constant.Companion.ADD_BANK_CARD_VIEW
import bonch.dev.Constant.Companion.OFFER_PRICE_VIEW
import bonch.dev.Coordinator
import bonch.dev.MainActivity
import bonch.dev.R
import bonch.dev.model.getdriver.pojo.PaymentCard
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
    private lateinit var paymentMethod: TextView
    private lateinit var cardsBottomSheet: LinearLayout
    private lateinit var commentBottomSheet: LinearLayout
    private lateinit var recyclerPayments: RecyclerView
    private lateinit var commentText: EditText

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        MapKitFactory.setApiKey(API_KEY)
        MapKitFactory.initialize(context)
        SearchFactory.initialize(context)
        DirectionsFactory.initialize(context)

        val mapKit = MapKitFactory.getInstance()
        val root = inflater.inflate(R.layout.detail_ride_fragment, container, false)


        initViews(root)

        setListener()

        setBottomSheet(root)

        if(coordinator == null){
            coordinator = (activity as MainActivity).coordinator
        }


        moveCamera(Point(60.066971, 30.334))


        return root
    }


    private fun setListener() {
        offerPrice.setOnClickListener{
            coordinator!!.replaceFragment(OFFER_PRICE_VIEW)
        }

        addCard.setOnClickListener {
            coordinator!!.replaceFragment(ADD_BANK_CARD_VIEW)
        }

        commentBtn.setOnClickListener {
            commentBottomSheetBehavior!!.state = BottomSheetBehavior.STATE_EXPANDED

            if (!commentText.isFocused) {
                commentText.requestFocus()
                MainActivity.showKeyboard(activity!!)
            }
        }


        paymentMethod.setOnClickListener {
            cardsBottomSheetBehavior!!.state = BottomSheetBehavior.STATE_EXPANDED
        }


    }


    private fun setBottomSheet(root: View) {
        commentBottomSheetBehavior!!.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                if (slideOffset > 0) {


                    if (slideOffset < 0.8) {
                        //onMapView.alpha = slideOffset
                    }
                }
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                    MainActivity.hideKeyboard(activity!!, root)
                    commentText.clearFocus()
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
        commentText = root.findViewById(R.id.comment_text)
        paymentMethod = root.findViewById(R.id.payment_method)
        cardsBottomSheet = root.findViewById(R.id.cards_bottom_sheet)
        commentBottomSheet = root.findViewById(R.id.comment_bottom_sheet)
        recyclerPayments = root.findViewById(R.id.payments_list)
        addCard = root.findViewById(R.id.add_card)

        cardsBottomSheetBehavior = BottomSheetBehavior.from<View>(cardsBottomSheet)
        commentBottomSheetBehavior = BottomSheetBehavior.from<View>(commentBottomSheet)

        val list: ArrayList<PaymentCard> = arrayListOf()
        for( i in 0..3){
            list.add(PaymentCard("VISA $i"))
        }
        paymentsListAdapter = PaymentsListAdapter(this, list, context!!, root)
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