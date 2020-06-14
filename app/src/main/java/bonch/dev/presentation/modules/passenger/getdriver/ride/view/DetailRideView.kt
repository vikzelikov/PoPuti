package bonch.dev.presentation.modules.passenger.getdriver.ride.view

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.recyclerview.widget.LinearLayoutManager
import bonch.dev.MainActivity
import bonch.dev.R
import bonch.dev.domain.entities.common.ride.Coordinate.fromAdr
import bonch.dev.domain.entities.common.ride.Coordinate.toAdr
import bonch.dev.domain.entities.common.banking.BankCard
import bonch.dev.domain.entities.common.ride.RideInfo
import bonch.dev.domain.utils.Keyboard
import bonch.dev.presentation.interfaces.ParentHandler
import bonch.dev.presentation.interfaces.ParentMapHandler
import bonch.dev.presentation.modules.passenger.getdriver.GetDriverComponent
import bonch.dev.presentation.modules.passenger.getdriver.ride.adapters.PaymentsListAdapter
import bonch.dev.presentation.modules.passenger.getdriver.ride.presenter.ContractPresenter
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.yandex.mapkit.mapview.MapView
import kotlinx.android.synthetic.main.detail_ride_layout.*
import java.lang.Exception
import javax.inject.Inject


class DetailRideView : Fragment(), ContractView.IDetailRideView {

    @Inject
    lateinit var detailRidePresenter: ContractPresenter.IDetailRidePresenter

    @Inject
    lateinit var paymentsListAdapter: PaymentsListAdapter

    private var cardsBottomSheetBehavior: BottomSheetBehavior<*>? = null
    private var commentBottomSheetBehavior: BottomSheetBehavior<*>? = null
    private var infoPriceBottomSheetBehavior: BottomSheetBehavior<*>? = null

    lateinit var backHandler: ParentHandler<FragmentManager>
    lateinit var mapView: ParentMapHandler<MapView>
    var bottomNavView: BottomNavigationView? = null


    init {
        GetDriverComponent.getDriverComponent?.inject(this)

        detailRidePresenter.instance().attachView(this)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.detail_ride_layout, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val from = fromAdr
        val to = toAdr
        if (from != null && to != null) {
            detailRidePresenter.checkAddressPoints(from, to)
        }

        setListeners()

        setBottomSheet()

        initBankingAdapter()

        correctMapView()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == detailRidePresenter.instance().OFFER_PRICE && resultCode == RESULT_OK) {
            detailRidePresenter.offerPriceDone(data)
        }

        if (requestCode == detailRidePresenter.instance().ADD_BANK_CARD && resultCode == RESULT_OK) {
            detailRidePresenter.addBankCardDone(data)
        }
    }


    override fun setListeners() {
        get_driver_btn.setOnClickListener {
            detailRidePresenter.getDriver()
        }

        offer_price.setOnClickListener {
            val context = context
            if (context != null) {
                detailRidePresenter.offerPrice(context, this)
            }
        }

        add_card.setOnClickListener {
            val context = context
            if (context != null) {
                detailRidePresenter.addBankCard(context, this)
            }
        }


        comment_btn.setOnClickListener {
            commentEditStart()
        }

        comment_back_btn.setOnClickListener {
            commentDone()
        }

        comment_done.setOnClickListener {
            val comment = comment_text.text.toString().trim()
            comment_min_text.text = comment

            commentDone()
        }

        payment_method.setOnClickListener {
            getPaymentMethods()
        }

        selected_payment_method.setOnClickListener {
            getPaymentMethods()
        }

        on_map_view.setOnClickListener {
            hideAllBottomSheet()
        }

        info_price.setOnClickListener {
            getInfoPrice()
        }

        show_route.setOnClickListener {
            detailRidePresenter.showRoute()
        }

        back_btn.setOnClickListener {
            onBackPressed()
        }
    }


    override fun setAddresses(fromAddress: String, toAddress: String) {
        from_address.text = fromAddress
        to_address.text = toAddress
    }


    private fun setBottomSheet() {
        cardsBottomSheetBehavior = BottomSheetBehavior.from<View>(cards_bottom_sheet)
        commentBottomSheetBehavior = BottomSheetBehavior.from<View>(comment_bottom_sheet)
        infoPriceBottomSheetBehavior = BottomSheetBehavior.from<View>(info_price_bottom_sheet)

        commentBottomSheetBehavior?.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                onSlideBottomSheet(slideOffset)
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                onStateChangedBottomSheet(newState)
            }
        })

        cardsBottomSheetBehavior?.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                onSlideBottomSheet(slideOffset)
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                onStateChangedBottomSheet(newState)
            }
        })

        infoPriceBottomSheetBehavior?.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                onSlideBottomSheet(slideOffset)
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                onStateChangedBottomSheet(newState)
            }
        })
    }


    private fun initBankingAdapter() {
        val list = arrayListOf<BankCard>()

        //add google pay
        list.add(
            BankCard(
                null,
                null,
                null,
                R.drawable.ic_google_pay
            )
        )
        paymentsListAdapter.list = list

        payments_list.apply {
            layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = paymentsListAdapter
        }
    }


    override fun getRideInfo(): RideInfo {
        val rideInfo = RideInfo()
        try {
            val comment = comment_text.text.toString().trim()
            val price = offer_price.text.toString().toInt()

            rideInfo.comment = comment
            rideInfo.price = price
        } catch (ex: NumberFormatException) {
            Log.e("Convert", "Converting price ride to Int " + this::class.java.simpleName)
        }

        return rideInfo
    }


    override fun isDataComplete(): Boolean {
        val isComplete: Boolean
        val offerPrice = offer_price.text.toString().trim()

        if (selected_payment_method.visibility == View.VISIBLE && offerPrice.length < 5) {
            isComplete = true
            changeBtnEnable(true)
        } else {
            isComplete = false
            changeBtnEnable(false)
        }

        return isComplete
    }


    private fun changeBtnEnable(isEnable: Boolean) {
        if (isEnable) {
            get_driver_btn.setBackgroundResource(R.drawable.bg_btn_blue)
        } else {
            get_driver_btn.setBackgroundResource(R.drawable.bg_btn_gray)
        }
    }


    override fun offerPriceDone(price: Int, averagePrice: Int) {
        offer_price.textSize = 22f
        offer_price.setPadding(0, 5, 0, 25)
        offer_price.setTextColor(Color.parseColor("#000000"))
        offer_price.typeface = Typeface.DEFAULT_BOLD

        if (averagePrice <= price) {
            info_price.visibility = View.GONE
        } else {
            info_price.visibility = View.VISIBLE
        }

        try {
            offer_price.text = price.toString()
        } catch (ex: NumberFormatException) {
            offer_price.text = "0"
        }

        //change btn enabled in case completed detail info
        isDataComplete()
    }


    override fun setSelectedBankCard(bankCard: BankCard) {
        selected_payment_method.visibility = View.VISIBLE
        payment_method.visibility = View.GONE

        number_card.text = bankCard.numberCard
        val img = bankCard.img
        if (img != null) {
            payment_method_img.setImageResource(img)
        }

        cardsBottomSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED

        //change btn enabled in case completed detail info
        isDataComplete()
    }


    override fun commentEditStart() {
        commentBottomSheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED

        if (!comment_text.isFocused) {
            comment_text.requestFocus()
            //set a little timer to open keyboard
            Handler().postDelayed({
                val activity = activity as? MainActivity
                activity?.let {
                    Keyboard.showKeyboard(activity)
                }
            }, 200)
        }
    }


    override fun getPaymentMethods() {
        cardsBottomSheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
    }


    override fun getInfoPrice() {
        infoPriceBottomSheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
    }


    override fun onSlideBottomSheet(slideOffset: Float) {
        if (slideOffset > 0 && slideOffset < 1) {
            on_map_view.alpha = slideOffset * 0.8f
            back_btn.alpha = 1 - slideOffset
            show_route.alpha = 1 - slideOffset
        }
    }


    override fun onStateChangedBottomSheet(newState: Int) {
        if (newState == BottomSheetBehavior.STATE_DRAGGING) {
            hideKeyboard()
            comment_text.clearFocus()
        }

        if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
            on_map_view.visibility = View.GONE
            main_info_layout.elevation = 30f
            comment_text.clearFocus()
        } else {
            on_map_view.visibility = View.VISIBLE
            main_info_layout.elevation = 0f
        }
    }


    override fun removeTickSelected() {
        val childCount = payments_list.childCount

        for (i in 0 until childCount) {
            val holder = payments_list.getChildViewHolder(payments_list.getChildAt(i))
            val tick = holder.itemView.findViewById<ImageView>(R.id.tick)
            tick.setImageResource(R.drawable.ic_tick)
        }
    }


    override fun hideAllBottomSheet() {
        hideKeyboard()
        comment_text.clearFocus()

        commentBottomSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
        cardsBottomSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
        infoPriceBottomSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
    }


    override fun onBackPressed(): Boolean {
        return if (cardsBottomSheetBehavior?.state != BottomSheetBehavior.STATE_COLLAPSED
            || commentBottomSheetBehavior?.state != BottomSheetBehavior.STATE_COLLAPSED
        ) {
            //hide all bottom sheet
            hideAllBottomSheet()

            false
        } else {
            detail_ride_container.alpha = 0.0f
            detailRidePresenter.removeRoute()

            val fm = (activity as? MainActivity)?.supportFragmentManager
            fm?.let {
                backHandler(fm)
            }

            true
        }
    }


    private fun correctMapView() {
        Thread(Runnable {
            while (true) {
                try {
                    val height = main_info_layout?.height
                    if (height in 100..1000) {
                        val mainHandler = Handler(Looper.getMainLooper())
                        val myRunnable = Runnable {
                            kotlin.run {
                                val layoutParams: RelativeLayout.LayoutParams =
                                    RelativeLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        LinearLayout.LayoutParams.MATCH_PARENT
                                    )
                                //"-10" for correct view radius corners
                                if (height != null) {
                                    layoutParams.setMargins(0, 0, 0, height - 10)
                                }
                                getMap()?.layoutParams = layoutParams
                                bottomNavView?.visibility = View.GONE
                            }
                        }

                        mainHandler.post(myRunnable)

                        break
                    }
                } catch (ex: Exception) {
                    break
                }

            }
        }).start()
    }


    private fun commentDone() {
        comment_text?.clearFocus()

        hideAllBottomSheet()
    }


    override fun getMap(): MapView? {
        return mapView()
    }


    override fun getPaymentsAdapter(): PaymentsListAdapter {
        return paymentsListAdapter
    }


    override fun hideKeyboard() {
        val activity = activity as? MainActivity
        activity?.let {
            Keyboard.hideKeyboard(activity, view)
        }
    }


    override fun getBottomNav(): BottomNavigationView? {
        return bottomNavView
    }


    override fun getNavHost(): NavController? {
        return (activity as? MainActivity)?.navController
    }

}