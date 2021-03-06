package bonch.dev.poputi.presentation.modules.passenger.getdriver.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
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
import androidx.navigation.NavController
import androidx.recyclerview.widget.LinearLayoutManager
import bonch.dev.poputi.MainActivity
import bonch.dev.poputi.R
import bonch.dev.poputi.domain.entities.common.banking.BankCard
import bonch.dev.poputi.domain.entities.common.ride.Coordinate
import bonch.dev.poputi.domain.entities.common.ride.Coordinate.fromAdr
import bonch.dev.poputi.domain.entities.common.ride.Coordinate.toAdr
import bonch.dev.poputi.domain.entities.common.ride.RideInfo
import bonch.dev.poputi.domain.utils.Keyboard
import bonch.dev.poputi.presentation.interfaces.ParentEmptyHandler
import bonch.dev.poputi.presentation.interfaces.ParentHandler
import bonch.dev.poputi.presentation.interfaces.ParentMapHandler
import bonch.dev.poputi.presentation.modules.passenger.getdriver.GetDriverComponent
import bonch.dev.poputi.presentation.modules.passenger.getdriver.adapters.PaymentsAdapter
import bonch.dev.poputi.presentation.modules.passenger.getdriver.presenter.ContractPresenter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.yandex.mapkit.mapview.MapView
import kotlinx.android.synthetic.main.detail_ride_layout.*
import javax.inject.Inject


class DetailRideView : Fragment(), ContractView.IDetailRideView {

    @Inject
    lateinit var detailRidePresenter: ContractPresenter.IDetailRidePresenter

    @Inject
    lateinit var paymentAdapter: PaymentsAdapter


    private var cardsBottomSheetBehavior: BottomSheetBehavior<*>? = null
    private var commentBottomSheetBehavior: BottomSheetBehavior<*>? = null
    private var infoPriceBottomSheetBehavior: BottomSheetBehavior<*>? = null


    lateinit var backHandler: ParentEmptyHandler
    lateinit var nextFragment: ParentEmptyHandler
    lateinit var onCreateRideFail: ParentEmptyHandler
    lateinit var mapView: ParentMapHandler<MapView>
    lateinit var notification: ParentHandler<String>

    private val VISA = 4
    private val MC = 5
    private val RUS_WORLD = 2

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
        super.onViewCreated(view, null)

        onViewCreatedAnimation()

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
            detailRidePresenter.createRide()
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
            val comment = comment_text?.text?.toString()?.trim()
            comment_min_text?.text = comment

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
            (activity as? MainActivity)?.onBackPressed()
        }
    }


    override fun setAddresses(fromAddress: String, toAddress: String) {
        from_address?.text = fromAddress
        to_address?.text = toAddress

        Coordinate.price?.let {
            offerPriceDone(it)
        }
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
        payments_list?.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = paymentAdapter
        }

        val cards = detailRidePresenter.getBankCards()
        if (cards.isNotEmpty()) {
            cards.sortBy {
                it.id
            }

            paymentAdapter.list.addAll(cards)
            paymentAdapter.notifyDataSetChanged()

            payments_list?.visibility = View.VISIBLE
        }
    }


    override fun getRideInfo(): RideInfo {
        val rideInfo = RideInfo()
        try {
            val comment = comment_min_text?.text?.let {
                if (it.isEmpty()) {
                    null
                } else {
                    comment_text?.text?.toString()?.trim()
                }
            }
            val price = offer_price?.text?.toString()?.toInt()

            rideInfo.comment = comment
            rideInfo.price = price

            paymentAdapter.list.forEach {
                if (it.isSelect) rideInfo.paymentMethod = it
            }
        } catch (ex: NumberFormatException) {
            Log.e("Convert", "Converting price ride to Int " + this::class.java.simpleName)
        }

        return rideInfo
    }


    override fun isDataComplete(): Boolean {
        var isComplete = false

        if (selected_payment_method?.visibility == View.VISIBLE && checkPrice()) {
            isComplete = true
        }

        changeBtnEnable(isComplete)

        return isComplete
    }


    private fun checkPrice(): Boolean {
        var result = false

        try {
            val price = offer_price?.text?.toString()?.trim()?.toInt()
            if (price != null && price > 0) {
                result = true
            }
        } catch (ex: NumberFormatException) {

        }

        return result
    }


    private fun changeBtnEnable(isEnable: Boolean) {
        if (isEnable) {
            get_driver_btn?.isClickable = true
            get_driver_btn?.setBackgroundResource(R.drawable.bg_btn_blue)
        } else {
            get_driver_btn?.isClickable = false
            get_driver_btn?.setBackgroundResource(R.drawable.bg_btn_gray)
        }
    }


    override fun offerPriceDone(price: Int) {
        offer_price?.textSize = 22f
        getDP(15)?.let { offer_price?.setPadding(0, 5, 0, it) }
        offer_price?.setTextColor(Color.parseColor("#000000"))
        offer_price?.typeface = Typeface.DEFAULT_BOLD

        val averagePrice = Coordinate.averagePrice
        if (averagePrice != null) {
            if (averagePrice <= price) {
                info_price?.visibility = View.GONE
            } else {
                info_price?.visibility = View.VISIBLE
            }
        }

        try {
            offer_price?.text = price.toString()
        } catch (ex: NumberFormatException) {
            offer_price?.text = "0"
        }

        //change btn enabled in case completed detail info
        isDataComplete()
    }


    override fun setSelectedBankCard(bankCard: BankCard) {
        val hideChars = "•••• "
        var imgCard: Int? = null
        var numberCard = bankCard.numberCard

        if (numberCard != null) {
            val firstDigit = numberCard.first().minus('0')

            try {
                numberCard = when (firstDigit) {
                    VISA -> {
                        imgCard = R.drawable.ic_visa
                        hideChars + numberCard.substring(15, 19)
                    }

                    MC -> {
                        imgCard = R.drawable.ic_mastercard
                        hideChars + numberCard.substring(15, 19)
                    }

                    RUS_WORLD -> {
                        imgCard = R.drawable.ic_pay_world
                        hideChars + numberCard.substring(15, 19)
                    }

                    else -> {
                        imgCard = null
                        hideChars + numberCard.substring(15, 19)
                    }
                }

            } catch (ex: Exception) {

            }

            imgCard?.let {
                payment_method_img?.setImageResource(it)
            }

            number_card?.text = numberCard

            if (firstDigit == 'g'.minus('0')) {
                payment_method_img?.setImageResource(R.drawable.ic_google_pay)
                number_card?.text = ""
            }
        }

        selected_payment_method?.visibility = View.VISIBLE
        payment_method?.visibility = View.GONE

        cardsBottomSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED

        //change btn enabled in case completed detail info
        isDataComplete()
    }


    override fun commentEditStart() {
        commentBottomSheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED

        comment_text?.let {
            if (!it.isFocused) {
                comment_text?.requestFocus()
                //set a little timer to open keyboard
                Handler().postDelayed({
                    val activity = activity as? MainActivity
                    activity?.let {
                        Keyboard.showKeyboard(activity)
                    }
                }, 200)
            }
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
            on_map_view?.alpha = slideOffset * 0.8f
        }
    }


    override fun onStateChangedBottomSheet(newState: Int) {
        if (newState == BottomSheetBehavior.STATE_DRAGGING) {
            hideKeyboard()
            comment_text?.clearFocus()
        }

        if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
            on_map_view?.visibility = View.GONE
            comment_text?.clearFocus()
        } else {
            on_map_view?.visibility = View.VISIBLE
        }
    }


    override fun removeTickSelected() {
        val childCount = payments_list.childCount

        for (i in 0 until childCount) {
            payments_list?.let {
                val holder = payments_list?.getChildViewHolder(it.getChildAt(i))
                val tick = holder?.itemView?.findViewById<ImageView>(R.id.tick)
                tick?.setImageResource(R.drawable.ic_tick)
            }
        }
    }


    override fun hideAllBottomSheet() {
        hideKeyboard()
        comment_text?.clearFocus()

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
            detail_ride_container?.alpha = 0.0f
            detailRidePresenter.removeRoute()
            detailRidePresenter.onDestroy()

            backHandler()

            true
        }
    }


    override fun attachGetOffers() {
        detail_ride_container?.alpha = 1.0f
        detail_ride_container?.animate()
            ?.alpha(0f)
            ?.setDuration(200)
            ?.setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    //go to the next screen
                }
            })

        nextFragment()
    }


    override fun createRideFail() {
        onCreateRideFail()
    }


    private fun correctMapView() {
        try {
            main_info_layout?.post {
                val height = main_info_layout?.height
                val layoutParams: RelativeLayout.LayoutParams =
                    RelativeLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT
                    )
                //"-10" for correct view radius corners
                if (height != null) {
                    layoutParams.setMargins(0, 0, 0, 250)
                }
                getMap()?.layoutParams = layoutParams
            }
        } catch (ex: Exception) {

        }
    }


    private fun commentDone() {
        comment_text?.clearFocus()

        hideAllBottomSheet()
    }


    override fun getMap(): MapView? {
        return mapView()
    }


    override fun getPaymentsAdapter(): PaymentsAdapter {
        return paymentAdapter
    }


    override fun hideKeyboard() {
        val activity = activity as? MainActivity
        activity?.let {
            Keyboard.hideKeyboard(activity, view)
        }
    }


    override fun showNotification(text: String) {
        notification(text)
    }


    private fun onViewCreatedAnimation() {
        Handler().postDelayed({
            back_btn?.animate()?.alpha(1f)?.duration = 200
        }, 700)

        detail_ride_container?.alpha = 0.0f
        detail_ride_container?.animate()?.alpha(1f)?.duration = 200
    }


    override fun showLoading() {
        val mainHandler = Handler(Looper.getMainLooper())
        val myRunnable = Runnable {
            kotlin.run {
                get_driver_btn?.isClickable = false
                get_driver_btn?.isFocusable = false
            }
        }

        mainHandler.post(myRunnable)
    }


    override fun hideLoading() {
        val mainHandler = Handler(Looper.getMainLooper())
        val myRunnable = Runnable {
            kotlin.run {
                get_driver_btn?.isClickable = true
                get_driver_btn?.isFocusable = true
            }
        }

        mainHandler.post(myRunnable)
    }


    override fun removeRoute() {
        detailRidePresenter.removeRoute()
    }


    private fun getDP(dp: Int): Int? {
        var returnDP: Int? = null
        context?.resources?.displayMetrics?.density?.let {
            returnDP = ((dp * it).toInt())
        }

        return returnDP
    }


    override fun getNavHost(): NavController? {
        return (activity as? MainActivity)?.navController
    }


    override fun onDestroy() {
        detailRidePresenter.onDestroy()
        super.onDestroy()
    }

}