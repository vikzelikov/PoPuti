package bonch.dev.poputi.presentation.modules.passenger.getdriver.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import bonch.dev.poputi.MainActivity
import bonch.dev.poputi.R
import bonch.dev.poputi.domain.entities.common.media.Photo
import bonch.dev.poputi.domain.entities.common.ride.ActiveRide
import bonch.dev.poputi.domain.entities.common.ride.Driver
import bonch.dev.poputi.domain.entities.common.ride.StatusRide
import bonch.dev.poputi.domain.entities.passenger.getdriver.ReasonCancel
import bonch.dev.poputi.domain.utils.Keyboard
import bonch.dev.poputi.domain.utils.Vibration
import bonch.dev.poputi.presentation.base.MBottomSheet
import bonch.dev.poputi.presentation.interfaces.IconHandler
import bonch.dev.poputi.presentation.interfaces.ParentEmptyHandler
import bonch.dev.poputi.presentation.interfaces.ParentHandler
import bonch.dev.poputi.presentation.interfaces.ParentMapHandler
import bonch.dev.poputi.presentation.modules.passenger.getdriver.GetDriverComponent
import bonch.dev.poputi.presentation.modules.passenger.getdriver.presenter.ContractPresenter
import bonch.dev.poputi.service.passenger.PassengerRideService
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.ethanhua.skeleton.Skeleton
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.mapkit.mapview.MapView
import kotlinx.android.synthetic.main.track_ride_layout.*
import javax.inject.Inject


class TrackRideView : Fragment(), ContractView.ITrackRideView {

    @Inject
    lateinit var trackRidePresenter: ContractPresenter.ITrackRidePresenter

    private var cancelBottomSheet: BottomSheetBehavior<*>? = null
    private var confirmCancelBottomSheet: BottomSheetBehavior<*>? = null
    private var driverCancelledBottomSheet: BottomSheetBehavior<*>? = null
    private var commentBottomSheet: BottomSheetBehavior<*>? = null

    lateinit var mapView: ParentMapHandler<MapView>
    lateinit var nextFragment: ParentEmptyHandler
    lateinit var cancelRide: ParentHandler<ReasonCancel>
    lateinit var addDriverIcon: IconHandler
    lateinit var removeDriverIcon: ParentEmptyHandler

    private var isAllowSlide = true


    init {
        GetDriverComponent.getDriverComponent?.inject(this)

        trackRidePresenter.instance().attachView(this)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.track_ride_layout, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, null)

        showSkeletonAnim()

        onViewCreatedAnimation()

        correctMapView()

        setListeners()

        setBottomSheet()

        //set info about ride in view
        ActiveRide.activeRide?.driver?.let { setInfoDriver(it) }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == trackRidePresenter.instance().CHAT_REQUEST) {
            checkoutIconChat(false)
        }
    }


    override fun setInfoDriver(driver: Driver) {
        driver_name?.text = driver.firstName
        car_number?.text = driver.car?.number
        car_name?.text = driver.car?.name?.plus(" ")?.plus(driver.car?.model)

        val listPhotos = arrayListOf<Photo>()
        driver.photos?.forEach {
            if (it.imgName == "photo") listPhotos.add(it)
        }
        val img = when {
            listPhotos.lastOrNull()?.imgUrl != null -> {
                listPhotos.sortBy { it.id }
                listPhotos.lastOrNull()?.imgUrl
            }

            else -> null
        }
        img_driver?.let {
            Glide.with(it.context).load(img)
                .apply(RequestOptions().centerCrop().circleCrop())
                .error(R.drawable.ic_default_ava)
                .into(it)
        }

        //set status
        var status = trackRidePresenter.getByValue(ActiveRide.activeRide?.statusId)
        if (status == null) status = StatusRide.SEARCH
        checkoutStatusView(status)
    }


    override fun checkoutStatusView(idStep: StatusRide) {
        when (idStep) {
            StatusRide.WAIT_FOR_DRIVER -> {
                status_driver?.text = resources.getString(R.string.driverInWay)
            }

            StatusRide.WAIT_FOR_PASSANGER -> {
                context?.let { Vibration.start(it) }
                status_driver?.text = resources.getString(R.string.driverArrived)
            }

            StatusRide.IN_WAY -> {
                trackRidePresenter.route()
                phone_call_layout?.visibility = View.GONE
                chat_layout?.visibility = View.GONE
                show_route?.visibility = View.VISIBLE
                status_driver?.text = resources.getString(R.string.inWay)
            }

            StatusRide.GET_PLACE -> {
                context?.let { Vibration.start(it) }
                trackRidePresenter.clearData()

                attachRateView()
            }

            StatusRide.CANCEL -> {
                context?.let { Vibration.start(it) }
                trackRidePresenter.clearData()

                //driver has cancelled this ride
                driverCancelRide()
            }

            else -> {
                status_driver?.text = resources.getString(R.string.driverInWay)
                showNotification(getString(R.string.errorSystem))
            }
        }
    }


    override fun addDriverIconF(point: Point): PlacemarkMapObject? {
        return addDriverIcon(point)
    }


    override fun rotateDriverIconF(deg: Float) {
//        rotateDriverIcon(deg)
    }


    override fun moveDriverIconF(point: Point) {
//        moveDriverIcon(point)
    }


    override fun removeDriverIconF() {
        removeDriverIcon()
    }


    private fun setBottomSheet() {
        cancelBottomSheet = BottomSheetBehavior.from<View>(reasons_bottom_sheet)
        confirmCancelBottomSheet = BottomSheetBehavior.from<View>(confirm_cancel_bottom_sheet)
        driverCancelledBottomSheet = BottomSheetBehavior.from<View>(driver_cancelled_bottom_sheet)
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


        driverCancelledBottomSheet?.addBottomSheetCallback(object :
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


    override fun setListeners() {
        //set default reason
        var reasonID = ReasonCancel.MISTAKE_ORDER
        var textReason: String? = null

        cancel_ride.setOnClickListener {
            getCancelReason()
        }

        driver_say_to_cancel.setOnClickListener {
            reasonID = ReasonCancel.DRIVER_CANCEL
            textReason = driver_say_to_cancel?.text?.toString()
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
            trackRidePresenter.cancelDoneOtherReason(comment_text?.text?.toString())
        }

        show_route.setOnClickListener {
            trackRidePresenter.showRoute()
        }

        cancel.setOnClickListener {
            textReason?.let {
                trackRidePresenter.cancelDone(reasonID, it)
            }
        }

        not_cancel.setOnClickListener {
            hideAllBottomSheet()
        }

        on_map_view.setOnClickListener {
            hideAllBottomSheet()
        }

        comment_back_btn.setOnClickListener {
            hideAllBottomSheet()
            comment_text.clearFocus()
            hideKeyboard()
        }

        ok_btn.setOnClickListener {
            trackRidePresenter.backFragment(ReasonCancel.DRIVER_CANCEL)
        }

        message_driver.setOnClickListener {
            context?.let {
                trackRidePresenter.showChat(it, this)
            }
        }


        phone_call_driver.setOnClickListener {
            ActiveRide.activeRide?.driver?.phone?.let { phone ->
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:$phone")
                startActivity(intent)
            }
        }
    }


    override fun getNavHost(): NavController? {
        return (activity as? MainActivity)?.navController
    }


    override fun hideKeyboard() {
        val activity = activity as? MainActivity
        activity?.let {
            Keyboard.hideKeyboard(it, view)
        }
    }


    private fun hideAllBottomSheet() {
        on_map_view?.visibility = View.GONE
        comment_text?.clearFocus()
        hideKeyboard()

        cancelBottomSheet?.state = BottomSheetBehavior.STATE_COLLAPSED
        confirmCancelBottomSheet?.state = BottomSheetBehavior.STATE_COLLAPSED
        commentBottomSheet?.state = BottomSheetBehavior.STATE_COLLAPSED
    }


    private fun getCancelReason() {
        cancelBottomSheet?.state = BottomSheetBehavior.STATE_EXPANDED
    }


    private fun getOtherReasonComment() {
        commentBottomSheet?.state = BottomSheetBehavior.STATE_EXPANDED

        comment_text?.let {
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
    }


    private fun driverCancelRide() {
        hideAllBottomSheet()

        isAllowSlide = false
        (driverCancelledBottomSheet as? MBottomSheet<*>)?.swipeEnabled = false
        on_map_view?.visibility = View.VISIBLE
        on_map_view?.isClickable = false
        on_map_view?.alpha = 0.8f
        on_map_view?.elevation = 20f
        main_info_layout?.elevation = 0f
        main_coordinator?.elevation = 100f

        driverCancelledBottomSheet?.state = BottomSheetBehavior.STATE_EXPANDED
    }


    private fun onSlideCancelReason(slideOffset: Float) {
        if (slideOffset > 0 && isAllowSlide) {
            on_map_view?.alpha = slideOffset * 0.8f
        }
    }


    private fun onChangedStateCancelReason(newState: Int) {
        if (isAllowSlide) {
            if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                on_map_view?.visibility = View.GONE
                comment_text?.clearFocus()
            } else {
                confirmCancelBottomSheet?.state = BottomSheetBehavior.STATE_COLLAPSED
                on_map_view?.visibility = View.VISIBLE
            }
        }
    }


    private fun onChangedStateConfirmCancel(newState: Int) {
        if (isAllowSlide) {
            if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                main_coordinator?.elevation = 100f
                main_info_layout?.elevation = 30f
                on_map_view?.elevation = 40f
            } else {
                main_coordinator?.elevation = 65f
                main_info_layout?.elevation = 0f
                on_map_view?.elevation = 75f
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
                on_map_view?.visibility = View.VISIBLE
            }
        }
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
                    layoutParams.setMargins(0, 0, 0, height - 300)
                }
                getMap()?.layoutParams = layoutParams
            }
        } catch (ex: java.lang.Exception) {

        }
    }


    override fun showNotification(text: String) {
        (activity as? MainActivity)?.showNotification(text)
    }


    override fun showLoading() {
        (activity as? MainActivity)?.showLoading()
    }


    override fun hideLoading() {
        (activity as? MainActivity)?.hideLoading()
    }


    private fun getConfirmCancel() {
        var status = trackRidePresenter.getByValue(ActiveRide.activeRide?.statusId)
        if (status == null) status = StatusRide.SEARCH

        if (status == StatusRide.WAIT_FOR_PASSANGER || status == StatusRide.IN_WAY) {
            //get payment for cancel
            val tax = trackRidePresenter.getTaxMoney()

            var message = if (status == StatusRide.IN_WAY) {
                resources.getString(R.string.messageWarningTakeMoneyInWay)
            } else resources.getString(R.string.messageWarningTakeMoney)
            val rub = resources.getString(R.string.rub)

            message = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                message.plus(" ")
                    .plus(Html.fromHtml("<b>$tax $rub</b>", Html.FROM_HTML_MODE_COMPACT))
            } else {
                message.plus(" ").plus(Html.fromHtml("<b>$tax $rub</b>"))
            }

            text_message?.text = message

        } else if (status == StatusRide.WAIT_FOR_DRIVER) {
            text_message?.text = resources.getString(R.string.messageWarningDriverIs)
        }

        confirmCancelBottomSheet?.state = BottomSheetBehavior.STATE_EXPANDED
    }


    override fun checkoutIconChat(isShow: Boolean) {
        message_notification?.visibility = if (isShow) View.VISIBLE
        else View.GONE
    }


    override fun getMap(): MapView? {
        return mapView()
    }


    override fun attachRateView() {
        track_ride_layout?.alpha = 1.0f
        track_ride_layout?.animate()
            ?.alpha(0f)
            ?.setDuration(150)
            ?.setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                }
            })

        nextFragment()
    }


    override fun onBackPressed(): Boolean {
        var isBackPressed = true

        if (cancelBottomSheet?.state != BottomSheetBehavior.STATE_COLLAPSED
            || confirmCancelBottomSheet?.state != BottomSheetBehavior.STATE_COLLAPSED
            || driverCancelledBottomSheet?.state != BottomSheetBehavior.STATE_COLLAPSED
        ) {

            //hide all bottom sheets
            hideAllBottomSheet()

            isBackPressed = false
        }

        return isBackPressed
    }


    override fun registerReceivers() {
        trackRidePresenter.registerReceivers()
    }


    private fun onViewCreatedAnimation() {
        track_ride_layout?.alpha = 0.0f
        track_ride_layout?.animate()?.alpha(1f)?.duration = 200
    }


    override fun onCancelRide(reason: ReasonCancel) {
        cancelRide(reason)
    }


    private fun showSkeletonAnim() {
        val imgDriver = Skeleton.bind(img_driver)
            .load(R.layout.skeleton_layout)
            .show()

        val driverName = Skeleton.bind(driver_name)
            .load(R.layout.skeleton_layout)
            .show()

        val carNumber = Skeleton.bind(car_number)
            .load(R.layout.skeleton_layout)
            .show()

        val carName = Skeleton.bind(car_name)
            .load(R.layout.skeleton_layout)
            .show()

        var handler: Handler? = Handler()
        handler?.postDelayed(object : Runnable {
            override fun run() {
                val driver = ActiveRide.activeRide?.driver
                if (driver != null) {

                    resetLayouts()

                    setInfoDriver(driver)

                    imgDriver?.hide()
                    driverName?.hide()
                    carNumber?.hide()
                    carName?.hide()

                    handler?.removeCallbacksAndMessages(null)
                    handler = null
                }

                handler?.postDelayed(this, 1500)
            }
        }, 0)
    }


    private fun resetLayouts() {
        val layoutDriverName = driver_name?.layoutParams
        layoutDriverName?.height = LinearLayout.LayoutParams.WRAP_CONTENT
        layoutDriverName?.width = LinearLayout.LayoutParams.WRAP_CONTENT
        driver_name?.layoutParams = layoutDriverName

        val layoutCarNumber = car_number?.layoutParams
        layoutCarNumber?.height = LinearLayout.LayoutParams.WRAP_CONTENT
        layoutCarNumber?.width = LinearLayout.LayoutParams.WRAP_CONTENT
        car_number?.layoutParams = layoutCarNumber

        val layoutCarName = car_name?.layoutParams
        layoutCarName?.height = LinearLayout.LayoutParams.WRAP_CONTENT
        layoutCarName?.width = LinearLayout.LayoutParams.WRAP_CONTENT
        car_name?.layoutParams = layoutCarName
    }


    override fun onResume() {
        checkoutIconChat(false)

        PassengerRideService.isAppClose = false
        super.onResume()
    }


    override fun onPause() {
        PassengerRideService.isAppClose = true
        super.onPause()
    }
}