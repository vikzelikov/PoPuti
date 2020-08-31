package bonch.dev.poputi.presentation.modules.passenger.getdriver.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import bonch.dev.poputi.MainActivity
import bonch.dev.poputi.R
import bonch.dev.poputi.domain.entities.common.ride.ActiveRide
import bonch.dev.poputi.domain.entities.common.ride.Offer
import bonch.dev.poputi.domain.entities.common.ride.RideInfo
import bonch.dev.poputi.domain.entities.common.ride.StatusRide
import bonch.dev.poputi.domain.entities.passenger.getdriver.ReasonCancel
import bonch.dev.poputi.domain.utils.Keyboard
import bonch.dev.poputi.presentation.base.MBottomSheet
import bonch.dev.poputi.presentation.interfaces.ParentEmptyHandler
import bonch.dev.poputi.presentation.interfaces.ParentHandler
import bonch.dev.poputi.presentation.interfaces.ParentMapHandler
import bonch.dev.poputi.presentation.modules.passenger.getdriver.GetDriverComponent
import bonch.dev.poputi.presentation.modules.passenger.getdriver.adapters.OffersAdapter
import bonch.dev.poputi.presentation.modules.passenger.getdriver.adapters.WrapContentLinearLayoutManager
import bonch.dev.poputi.presentation.modules.passenger.getdriver.presenter.ContractPresenter
import bonch.dev.poputi.service.passenger.PassengerRideService
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.mapview.MapView
import kotlinx.android.synthetic.main.get_offers_layout.*
import javax.inject.Inject


class GetOffersView : Fragment(), ContractView.IGetOffersView {

    @Inject
    lateinit var getOffersPresenter: ContractPresenter.IGetOffersPresenter

    @Inject
    lateinit var offersAdapter: OffersAdapter

    private var cancelBottomSheet: BottomSheetBehavior<*>? = null
    private var confirmCancelBottomSheet: BottomSheetBehavior<*>? = null
    private var expiredTimeBottomSheet: BottomSheetBehavior<*>? = null
    private var confirmGetBottomSheet: BottomSheetBehavior<*>? = null
    private var commentBottomSheet: BottomSheetBehavior<*>? = null

    lateinit var nextFragment: ParentEmptyHandler
    lateinit var onGetOfferFail: ParentEmptyHandler
    lateinit var mapView: ParentMapHandler<MapView>
    lateinit var cancelRide: ParentHandler<ReasonCancel>
    lateinit var notification: ParentHandler<String>
    lateinit var offerText: TextView
    var userPoint: ParentMapHandler<Point?>? = null

    private var isAllowSlide = true


    init {
        GetDriverComponent.getDriverComponent?.inject(this)

        getOffersPresenter.instance().attachView(this)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.get_offers_layout, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, null)

        //change status
        ActiveRide.activeRide?.statusId = StatusRide.SEARCH.status

        //check offers from server
        if (!PassengerRideService.isRunning) getOffersPresenter.checkOnOffers()

        onViewCreatedAnimation()

        getOffersPresenter.startSearchAnimation()

        correctMapView()

        receiveUserData(ActiveRide.activeRide)

        initializeAdapter()

        setBottomSheet()

        setListeners()
    }


    private fun receiveUserData(ride: RideInfo?) {
        ride?.let {
            from_address?.text = ride.position
            to_address?.text = ride.destination

            if (ride.price != null) {
                offer_price?.text = ride.price.toString()
            }

            val comment = ride.comment
            if (comment.isNullOrEmpty()) {
                comment_btn?.visibility = View.GONE
            } else {
                comment_min_text?.text = ride.comment
            }
        }
    }


    override fun setListeners() {
        //set deafult reason
        var reasonID = ReasonCancel.MISTAKE_ORDER
        var textReason: String? = null

        cancel_ride.setOnClickListener {
            getCancelReason()
        }

        change_mind.setOnClickListener {
            reasonID = ReasonCancel.CHANGE_MIND
            textReason = change_mind?.text?.toString()
            getConfirmCancel()
        }

        wait_long.setOnClickListener {
            reasonID = ReasonCancel.WAIT_LONG
            textReason = wait_long?.text?.toString()
            getConfirmCancel()
        }

        mistake_order.setOnClickListener {
            reasonID = ReasonCancel.MISTAKE_ORDER
            textReason = mistake_order?.text?.toString()
            getConfirmCancel()
        }

        other_reason.setOnClickListener {
            reasonID = ReasonCancel.OTHER_REASON
            getOtherReasonComment()
        }

        comment_done.setOnClickListener {
            getOffersPresenter.cancelDoneOtherReason(comment_text?.text?.toString())
        }

        cancel.setOnClickListener {
            textReason?.let {
                getOffersPresenter.cancelDone(reasonID, it)
                getOffersPresenter.backFragment(reasonID)
            }
        }

        expired_time_ok_btn.setOnClickListener {
            getOffersPresenter.backFragment(ReasonCancel.MISTAKE_ORDER)
        }

        confirm_accept_driver.setOnClickListener {
            getOffersPresenter.confirmAccept()
        }

        not_cancel.setOnClickListener {
            hideAllBottomSheet()
        }

        on_view_cancel.setOnClickListener {
            hideAllBottomSheet()
        }


        comment_back_btn.setOnClickListener {
            hideAllBottomSheet()
            comment_text.clearFocus()
            hideKeyboard()
        }
    }


    private fun setBottomSheet() {
        cancelBottomSheet = BottomSheetBehavior.from<View>(reasons_bottom_sheet)
        confirmCancelBottomSheet = BottomSheetBehavior.from<View>(confirm_cancel_bottom_sheet)
        expiredTimeBottomSheet = BottomSheetBehavior.from<View>(time_expired_bottom_sheet)
        confirmGetBottomSheet = BottomSheetBehavior.from<View>(confirm_get_bottom_sheet)
        commentBottomSheet = BottomSheetBehavior.from<View>(comment_bottom_sheet)

        cancelBottomSheet?.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                onSlideCancelReason(slideOffset)
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                onChangedStateCancelReason(newState)
            }
        })

        confirmCancelBottomSheet?.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                onChangedStateConfirmCancel(newState)
            }
        })

        confirmGetBottomSheet?.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                onSlideCancelReason(slideOffset)
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                onChangedStateCancelReason(newState)
            }
        })

        commentBottomSheet?.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                onChangedStateComment(newState)
            }
        })
    }


    private fun setConfirmAcceptData(offerPrice: Offer?) {
        //set data in BottomSheet for confirm or cancel
        offerPrice?.let { offer ->
            bs_driver_name?.text = offer.driver?.firstName
            bs_car_name?.text = offer.driver?.car?.name?.plus(" ")?.plus(offer.driver?.car?.model)
            bs_price?.text = offer.price.toString().plus(" ₽")

            bs_driver_rating?.text = if (offer.driver?.rating == null) {
                "0.0"
            } else {
                offer.driver?.rating.toString()
            }

            offer.driver?.photos?.sortBy { it.id }
            var photo: Any? = offer.driver?.photos?.lastOrNull()?.imgUrl
            if (photo == null) {
                photo = R.drawable.ic_default_ava
            }
            bs_img_driver?.let{
                Glide.with(bs_img_driver.context).load(photo)
                    .apply(RequestOptions().centerCrop().circleCrop())
                    .error(R.drawable.ic_default_ava)
                    .into(bs_img_driver)
            }
        }
    }


    private fun onSlideCancelReason(slideOffset: Float) {
        if (slideOffset > 0 && isAllowSlide) {
            on_view_cancel?.alpha = slideOffset * 0.8f
        }
    }


    private fun onChangedStateCancelReason(newState: Int) {
        if (isAllowSlide) {
            if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                on_view_cancel?.visibility = View.GONE
                comment_text?.clearFocus()
            } else {
                confirmCancelBottomSheet?.state = BottomSheetBehavior.STATE_COLLAPSED
                on_view_cancel?.visibility = View.VISIBLE
            }
        }
    }


    private fun onChangedStateConfirmCancel(newState: Int) {
        if (isAllowSlide) {
            if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                get_driver_coordinator?.elevation = 100f
            } else {
                get_driver_coordinator?.elevation = 65f
            }
        }
    }


    private fun onChangedStateComment(newState: Int) {
        if (isAllowSlide) {
            if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                hideKeyboard()
                comment_text?.clearFocus()
            }

            if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                comment_text?.clearFocus()
            } else {
                confirmCancelBottomSheet?.state = BottomSheetBehavior.STATE_COLLAPSED
                on_view_cancel?.visibility = View.VISIBLE
            }
        }
    }


    private fun getCancelReason() {
        cancelBottomSheet?.state = BottomSheetBehavior.STATE_EXPANDED
    }


    private fun getConfirmCancel() {
        confirmCancelBottomSheet?.state = BottomSheetBehavior.STATE_EXPANDED
    }


    override fun getConfirmAccept(offer: Offer) {
        setConfirmAcceptData(offer)
        confirmGetBottomSheet?.state = BottomSheetBehavior.STATE_EXPANDED
    }


    private fun getOtherReasonComment() {
        commentBottomSheet?.state = BottomSheetBehavior.STATE_EXPANDED

        comment_text?.let {
            if (!comment_text.isFocused) {
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


    override fun getExpiredTimeConfirm() {
        hideAllBottomSheet()

        isAllowSlide = false
        (expiredTimeBottomSheet as? MBottomSheet<*>)?.swipeEnabled = false
        on_view_cancel?.visibility = View.VISIBLE
        on_view_cancel?.isClickable = false
        on_view_cancel?.alpha = 0.8f
        get_driver_coordinator?.elevation = 100f


        expiredTimeBottomSheet?.state = BottomSheetBehavior.STATE_EXPANDED
    }


    private fun hideAllBottomSheet() {
        if (expiredTimeBottomSheet?.state == BottomSheetBehavior.STATE_COLLAPSED) {
            on_view_cancel?.visibility = View.GONE

            cancelBottomSheet?.state = BottomSheetBehavior.STATE_COLLAPSED
            confirmGetBottomSheet?.state = BottomSheetBehavior.STATE_COLLAPSED
            confirmCancelBottomSheet?.state = BottomSheetBehavior.STATE_COLLAPSED
            commentBottomSheet?.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        comment_text?.clearFocus()
        hideKeyboard()
    }


    override fun hideConfirmAccept() {
        confirmGetBottomSheet?.state = BottomSheetBehavior.STATE_COLLAPSED
    }


    override fun hideKeyboard() {
        val activity = activity as? MainActivity
        activity?.let {
            Keyboard.hideKeyboard(it, view)
        }
    }


    override fun getNavHost(): NavController? {
        return (activity as? MainActivity)?.navController
    }


    private fun initializeAdapter() {
        offersAdapter.list = getOffersPresenter.getOffers()

        if (!offersAdapter.list.isNullOrEmpty()) checkoutBackground(false)

        driver_list?.apply {
            layoutManager =
                WrapContentLinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = offersAdapter
        }

        driver_list?.visibility = View.VISIBLE
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
                    layoutParams.setMargins(0, 0, 0, 0)
                }
                getMap()?.layoutParams = layoutParams
            }
        } catch (ex: java.lang.Exception) {

        }
    }


    override fun getAdapter(): OffersAdapter {
        return offersAdapter
    }


    override fun checkoutBackground(isShow: Boolean) {
        //unsupported exception
        try {
            if (on_view_main != null) {
                if (isShow) {
                    offerText.alpha = 1.0f
                    on_view_main?.alpha = 0f
                } else {
                    offerText.alpha = 0f
                    on_view_main?.alpha = 0.8f
                }
            }
        } catch (ex: Exception) {
        }
    }


    override fun removeBackground() {
        offerText.alpha = 0f
        on_view_main?.visibility = View.GONE
    }


    override fun onBackPressed(): Boolean {
        var isBackPressed = true

        if (cancelBottomSheet?.state != BottomSheetBehavior.STATE_COLLAPSED
            || confirmCancelBottomSheet?.state != BottomSheetBehavior.STATE_COLLAPSED
            || expiredTimeBottomSheet?.state != BottomSheetBehavior.STATE_COLLAPSED
            || confirmGetBottomSheet?.state != BottomSheetBehavior.STATE_COLLAPSED
        ) {

            //hide all bottom sheets
            hideAllBottomSheet()

            isBackPressed = false
        }

        return isBackPressed
    }


    override fun showNotification(text: String) {
        notification(text)
    }


    override fun showLoading() {
        val mainHandler = Handler(Looper.getMainLooper())
        val myRunnable = Runnable {
            kotlin.run {
                confirm_accept_driver?.text = ""
                confirm_accept_driver?.isClickable = false
                confirm_accept_driver?.isFocusable = false
                progress_bar_btn?.visibility = View.VISIBLE
            }
        }

        mainHandler.post(myRunnable)
    }


    override fun hideLoading() {
        val mainHandler = Handler(Looper.getMainLooper())
        val myRunnable = Runnable {
            kotlin.run {
                confirm_accept_driver?.text = getString(R.string.letsGo)
                confirm_accept_driver?.isClickable = true
                confirm_accept_driver?.isFocusable = true
                progress_bar_btn?.visibility = View.GONE
            }
        }

        mainHandler.post(myRunnable)
    }


    private fun onViewCreatedAnimation() {
        get_offers_layout?.alpha = 0.0f
        get_offers_layout?.animate()?.alpha(1f)?.duration = 300

        offerText.alpha = 0.0f
        offerText.animate()?.alpha(1f)?.duration = 200

        val a = AnimationUtils.loadAnimation(context, R.anim.scale)
        a.reset()

        offerText.clearAnimation()
        offerText.startAnimation(a)
    }


    override fun getUserLocation(): Point? {
        return userPoint?.let { it() }
    }


    override fun getMap(): MapView? {
        return mapView()
    }


    override fun registerReceivers() {
        getOffersPresenter.registerReceivers()
    }


    override fun attachTrackRide() {
        get_offers_layout?.alpha = 1.0f
        get_offers_layout?.animate()
            ?.alpha(0f)
            ?.setDuration(50)
            ?.setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    nextFragment()
                }
            })
    }


    override fun getOfferFail() {
        onGetOfferFail()
    }


    override fun onCancelRide(reason: ReasonCancel) {
        cancelRide(reason)
    }


    override fun getRecyclerView(): RecyclerView? {
        return driver_list
    }


    override fun onResume() {
        super.onResume()
        PassengerRideService.isAppClose = false
    }


    override fun onPause() {
        super.onPause()
        PassengerRideService.isAppClose = true
    }
}