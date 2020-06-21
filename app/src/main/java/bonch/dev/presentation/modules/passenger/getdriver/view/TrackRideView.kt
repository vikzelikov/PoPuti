package bonch.dev.presentation.modules.passenger.getdriver.view

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import bonch.dev.MainActivity
import bonch.dev.R
import bonch.dev.domain.entities.common.profile.Profile
import bonch.dev.domain.entities.common.ride.ActiveRide
import bonch.dev.domain.entities.common.ride.RideStatus
import bonch.dev.domain.entities.common.ride.StatusRide
import bonch.dev.domain.entities.passenger.getdriver.ReasonCancel
import bonch.dev.domain.utils.Keyboard
import bonch.dev.domain.utils.Vibration
import bonch.dev.presentation.base.MBottomSheet
import bonch.dev.presentation.interfaces.ParentHandler
import bonch.dev.presentation.interfaces.ParentMapHandler
import bonch.dev.presentation.modules.passenger.getdriver.GetDriverComponent
import bonch.dev.presentation.modules.passenger.getdriver.presenter.ContractPresenter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
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
    lateinit var nextFragment: ParentHandler<FragmentManager>


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
        super.onViewCreated(view, savedInstanceState)

        correctMapView()

        setListeners()

        setBottomSheet()

        val driver = ActiveRide.activeRide?.driver
        if (driver != null) {
            trackRidePresenter.initTracking()

            setInfoDriver(driver)
        }
    }


    override fun setInfoDriver(driver: Profile) {
        driver_name.text = driver.firstName
//        car_number.text = driver.carNumber
//        car_name.text = driver.carName

        var photo: Any? = driver.photos?.lastOrNull()?.imgUrl
        if (photo == null) photo = R.drawable.ic_default_ava
        Glide.with(img_driver.context).load(photo)
            .apply(RequestOptions().centerCrop().circleCrop())
            .error(R.drawable.ic_default_ava)
            .into(img_driver)

        //set default status
        checkoutStatusView(StatusRide.WAIT_FOR_DRIVER)
    }


    override fun checkoutStatusView(idStep: StatusRide) {
        context?.let { Vibration.start(it) }

        when (idStep) {
            StatusRide.WAIT_FOR_DRIVER -> {
                status_driver.text = resources.getString(R.string.driverInWay)
            }

            StatusRide.WAIT_FOR_PASSANGER -> {
                status_driver.text = resources.getString(R.string.driverArrived)
            }

            StatusRide.IN_WAY -> {
                status_driver.text = resources.getString(R.string.inWay)
            }

            StatusRide.GET_PLACE -> {
                nextFragment()
            }

            StatusRide.CANCEL -> {
                //driver has cancelled this ride
                driverCancelRide()
            }

            else -> {
                status_driver.text = resources.getString(R.string.driverInWay)
                showNotification(getString(R.string.errorSystem))
            }
        }
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
        var reasonID = ReasonCancel.MISTAKE

        cancel_ride.setOnClickListener {
            getCancelReason()
        }

        case1.setOnClickListener {
            reasonID = ReasonCancel.DRIVER_CANCEL
            getConfirmCancel()
        }

        case2.setOnClickListener {
            reasonID = ReasonCancel.WAIT_LONG
            getConfirmCancel()
        }

        case3.setOnClickListener {
            reasonID = ReasonCancel.MISTAKE
            getConfirmCancel()
        }

        case4.setOnClickListener {
            reasonID = ReasonCancel.OTHER
            getOtherReasonComment()
        }

        comment_done.setOnClickListener {
            trackRidePresenter.cancelDoneOtherReason(comment_text.text.toString())
        }

        cancel.setOnClickListener {
            trackRidePresenter.cancelDone(reasonID)
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
        comment_text.clearFocus()
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


    private fun driverCancelRide() {
        hideAllBottomSheet()

        (driverCancelledBottomSheet as? MBottomSheet<*>)?.swipeEnabled = false
        main_info_layout.elevation = 0f
        on_map_view.visibility = View.VISIBLE
        on_map_view.alpha = 0.8f
        on_map_view.isClickable = false

        driverCancelledBottomSheet?.state = BottomSheetBehavior.STATE_EXPANDED
    }


    private fun onSlideCancelReason(slideOffset: Float) {
        if (slideOffset > 0) {
            on_map_view.alpha = slideOffset * 0.8f
        }
    }


    private fun onChangedStateCancelReason(newState: Int) {
        if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
            on_map_view.visibility = View.GONE
            main_info_layout.elevation = 30f
            comment_text.clearFocus()
        } else {
            confirmCancelBottomSheet?.state = BottomSheetBehavior.STATE_COLLAPSED
            on_map_view.visibility = View.VISIBLE
            main_info_layout.elevation = 0f
        }
    }


    private fun onChangedStateConfirmCancel(newState: Int) {
        if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
            main_coordinator.elevation = 10f
        } else {
            main_coordinator.elevation = 0f
        }
    }


    private fun onChangedStateComment(newState: Int) {
        if (newState == BottomSheetBehavior.STATE_DRAGGING) {
            hideKeyboard()
            comment_text.clearFocus()
        }

        if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
            comment_text.clearFocus()
        } else {
            confirmCancelBottomSheet?.state = BottomSheetBehavior.STATE_COLLAPSED
            on_map_view.visibility = View.VISIBLE
            main_info_layout.elevation = 0f
        }
    }


    private fun correctMapView() {
//        Thread(Runnable {
//            while (true) {
//                try {
//                    val height = main_info_layout?.height
//                    if (height in 100..1000) {
//                        val mainHandler = Handler(Looper.getMainLooper())
//                        val myRunnable = Runnable {
//                            kotlin.run {
//                                val layoutParams: RelativeLayout.LayoutParams =
//                                    RelativeLayout.LayoutParams(
//                                        LinearLayout.LayoutParams.MATCH_PARENT,
//                                        LinearLayout.LayoutParams.MATCH_PARENT
//                                    )
//                                //"-10" for correct view radius corners
//                                if (height != null) {
//                                    layoutParams.setMargins(0, 0, 0, height - 10)
//                                }
//                                getMap()?.layoutParams = layoutParams
//                            }
//                        }
//
//                        mainHandler.post(myRunnable)
//
//                        break
//                    }
//                } catch (ex: Exception) {
//                    break
//                }
//
//            }
//        }).start()
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
        val status = RideStatus.status
        if (status == StatusRide.WAIT_FOR_PASSANGER) {
            val tax = trackRidePresenter.getTaxMoney()
            val message: String = resources.getString(R.string.messageWarningTakeMoney)

            val rub = resources.getString(R.string.offer_price_average_price)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                message.plus(Html.fromHtml(" <b>$tax $rub</b>", Html.FROM_HTML_MODE_COMPACT))
            } else {
                message.plus(Html.fromHtml(" <b>$tax $rub</b>"))
            }

            text_message.text = message

        } else if (status == StatusRide.WAIT_FOR_DRIVER) {
            text_message.text = resources.getString(R.string.messageWarningDriverIs)
        }

        main_coordinator.elevation = 0f
        confirmCancelBottomSheet?.state = BottomSheetBehavior.STATE_EXPANDED
    }


    override fun getMap(): MapView? {
        return mapView()
    }


    override fun nextFragment() {
        val fm = (activity as? MainActivity)?.supportFragmentManager
        fm?.let { nextFragment(fm) }
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


    override fun onDestroy() {
        trackRidePresenter.onDestroy()
        super.onDestroy()
    }


}