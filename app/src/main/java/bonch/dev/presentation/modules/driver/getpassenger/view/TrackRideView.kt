package bonch.dev.presentation.modules.driver.getpassenger.view

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import bonch.dev.R
import bonch.dev.domain.entities.common.ride.ActiveRide
import bonch.dev.domain.entities.common.ride.RideInfo
import bonch.dev.domain.entities.common.ride.RideStatus
import bonch.dev.domain.entities.common.ride.StatusRide
import bonch.dev.domain.entities.driver.getpassenger.ReasonCancel
import bonch.dev.domain.utils.Keyboard
import bonch.dev.domain.utils.Vibration
import bonch.dev.presentation.base.MBottomSheet
import bonch.dev.presentation.interfaces.ParentEmptyHandler
import bonch.dev.presentation.interfaces.ParentHandler
import bonch.dev.presentation.interfaces.ParentMapHandler
import bonch.dev.presentation.modules.driver.getpassenger.GetPassengerComponent
import bonch.dev.presentation.modules.driver.getpassenger.presenter.ContractPresenter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.yandex.mapkit.mapview.MapView
import kotlinx.android.synthetic.main.order_track_ride_layout.*
import java.util.*
import javax.inject.Inject


class TrackRideView : Fragment(), ContractView.ITrackRideView {

    @Inject
    lateinit var trackRidePresenter: ContractPresenter.ITrackRidePresenter

    private var cancelBottomSheet: BottomSheetBehavior<*>? = null
    private var confirmCancelBottomSheet: BottomSheetBehavior<*>? = null
    private var passangerCancelledBottomSheet: BottomSheetBehavior<*>? = null
    private var commentBottomSheet: BottomSheetBehavior<*>? = null

    private var blockHandler: Handler? = null
    private var isBlock = false

    lateinit var mapView: ParentMapHandler<MapView>
    lateinit var nextFragment: ParentHandler<FragmentManager>
    lateinit var finishActivity: ParentEmptyHandler


    init {
        GetPassengerComponent.getPassengerComponent?.inject(this)

        trackRidePresenter.instance().attachView(this)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        context?.let { Vibration.start(it) }
        showNotification(getString(R.string.rideCreated))

        startProcessBlock()

        return inflater.inflate(R.layout.order_track_ride_layout, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        trackRidePresenter.subscribeOnChangeRide()

        trackRidePresenter.receiveOrder(ActiveRide.activeRide)

        correctMapView()

        setListeners()

        setBottomSheet()

        onSlideToNextStep()
    }


    override fun setOrder(order: RideInfo) {
        passanger_name.text = order.passenger?.firstName
        from_address.text = order.position
        to_address.text = order.destination

        order.passenger?.photos?.sortBy { it.id }
        var photo: Any? = order.passenger?.photos?.lastOrNull()?.imgUrl
        if (photo == null) photo = R.drawable.ic_default_ava
        Glide.with(img_passanger.context).load(photo)
            .apply(RequestOptions().centerCrop().circleCrop())
            .error(R.drawable.ic_default_ava)
            .into(img_passanger)
    }


    override fun tickTimerWaitPassanger(sec: Int, isPaidWaiting: Boolean) {
        val mainHandler = Handler(Looper.getMainLooper())
        val myRunnable = Runnable {
            kotlin.run {
                var timeString = sec.toString()
                var textStatus = getString(R.string.waitPassanger)

                if (isPaidWaiting) {
                    status_order.setTextColor(Color.parseColor("#F73F3F"))
                    textStatus = getString(R.string.waitingTime)

                    if (sec > 60) {
                        val minutes = (sec % 3600) / 60
                        val seconds = sec.rem(60)

                        timeString =
                            String.format(
                                "%2d ${getString(R.string.min)} %2d",
                                minutes,
                                seconds
                            )
                    }

                } else {
                    status_order.setTextColor(Color.parseColor("#000000"))
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    status_order.text = Html.fromHtml(
                        textStatus.plus("<br>").plus("<b>$timeString ${getString(R.string.sec)}</b>"),
                        Html.FROM_HTML_MODE_COMPACT
                    )
                } else {
                    status_order.text = Html.fromHtml(
                        textStatus.plus("<br>").plus("<b>$timeString ${getString(R.string.sec)}</b>")
                    )
                }
            }
        }

        mainHandler.post(myRunnable)
    }


    override fun stepWaitPassanger() {
        order_addresses_layout.visibility = View.GONE
        status_order_layout.visibility = View.VISIBLE

        text_step_seekbar.text = getString(R.string.letsGo)

        correctMapView()
    }


    override fun stepInWay() {
        status_order_layout.visibility = View.GONE
        phone_call_layout.visibility = View.GONE
        message_layout.visibility = View.GONE
        navigator_layout.visibility = View.VISIBLE
        order_addresses_layout.visibility = View.VISIBLE

        text_step_seekbar.text = getString(R.string.toEndRide)

        correctMapView()
    }


    override fun stepDoneRide() {
        val activity = activity as? MapOrderView
        activity?.let { nextFragment(it.supportFragmentManager) }
    }


    private fun onSlideToNextStep() {
        seekBar.setSlideButtonListener(object : SlideButtonListener {
            override fun handleSlide() {
                if (!isBlock) {
                    trackRidePresenter.nextStep()
                    isBlock = true
                }
            }
        })

        seekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                try {
                    text_step_seekbar?.alpha = 1 - (progress / 100.0f) * 2f
                } catch (ex: IllegalStateException) {
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}

        })
    }


    override fun setListeners() {
        //set deafult reason
        var reasonID = ReasonCancel.CHANGE_MIND
        var textReason: String? = null

        cancel_order.setOnClickListener {
            getCancelReason()
        }

        change_mind.setOnClickListener {
            reasonID = ReasonCancel.CHANGE_MIND
            textReason = change_mind.text.toString()
            getConfirmCancel(reasonID)
        }

        force_majeure.setOnClickListener {
            reasonID = ReasonCancel.FORCE_MAJEURE
            textReason = force_majeure.text.toString()
            getConfirmCancel(reasonID)
        }

        passenger_with_child.setOnClickListener {
            reasonID = ReasonCancel.PASSENGER_WITH_CHILD
            textReason = passenger_with_child.text.toString()
            getConfirmCancel(reasonID)
        }

        passenger_error.setOnClickListener {
            reasonID = ReasonCancel.PASSENGER_ERROR
            textReason = passenger_error.text.toString()
            getConfirmCancel(reasonID)
        }

        passenger_go_out.setOnClickListener {
            reasonID = ReasonCancel.PASSENGER_GO_OUT
            textReason = passenger_go_out.text.toString()
            getConfirmCancel(reasonID)
        }

        other_reason.setOnClickListener {
            reasonID = ReasonCancel.OTHER_REASON
            getConfirmCancel(reasonID)
        }

        comment_done.setOnClickListener {
            trackRidePresenter.cancelDoneOtherReason(comment_text.text.toString())
        }

        cancel.setOnClickListener {
            textReason?.let {
                trackRidePresenter.cancelDone(reasonID, it)
            }
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

        ok_btn.setOnClickListener {
            finishActivity()
        }

        message.setOnClickListener {
            context?.let {
                trackRidePresenter.showChat(it, this)
            }
        }

        phone_call.setOnClickListener {
            ActiveRide.activeRide?.passenger?.phone?.let { phone ->
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:$phone")
                startActivity(intent)
            }
        }

        navigator.setOnClickListener {
            val lat = ActiveRide.activeRide?.toLat
            val lng = ActiveRide.activeRide?.toLng

            if (lat != null && lng != null)
                openNavigator(lat, lng)
        }
    }


    override fun getNavHost(): NavController? {
        return (activity as? MapOrderView)?.getNavHost()
    }


    override fun hideKeyboard() {
        val activity = activity as? MapOrderView
        activity?.let {
            Keyboard.hideKeyboard(it, view)
        }
    }


    override fun showNotification(text: String) {
        (activity as? MapOrderView)?.showNotification(text)
    }


    override fun showLoading() {
        (activity as? MapOrderView)?.showLoading()
    }


    override fun hideLoading() {
        (activity as? MapOrderView)?.hideLoading()
    }


    private fun hideAllBottomSheet() {
        if (passangerCancelledBottomSheet?.state == BottomSheetBehavior.STATE_COLLAPSED) {
            on_view_cancel?.visibility = View.GONE

            cancelBottomSheet?.state = BottomSheetBehavior.STATE_COLLAPSED
            confirmCancelBottomSheet?.state = BottomSheetBehavior.STATE_COLLAPSED
            commentBottomSheet?.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        comment_text?.clearFocus()
        hideKeyboard()
    }


    private fun getCancelReason() {
        cancelBottomSheet?.state = BottomSheetBehavior.STATE_EXPANDED
    }


    private fun getConfirmCancel(reasonId: ReasonCancel) {
        when (reasonId) {
            ReasonCancel.CHANGE_MIND -> {
                title_confirm_cancel.visibility = View.GONE
                subtitle_cancel.visibility = View.GONE
                subtitle_confirm_cancel.visibility = View.VISIBLE
                subtitle_confirm_cancel.text = getString(R.string.change_mind_text)
                cancel.text = getString(R.string.cancel)
            }

            ReasonCancel.FORCE_MAJEURE -> {
                title_confirm_cancel.visibility = View.GONE
                subtitle_cancel.visibility = View.GONE
                subtitle_confirm_cancel.visibility = View.VISIBLE
                subtitle_confirm_cancel.text = getString(R.string.force_majeure_text)
                cancel.text = getString(R.string.YesCancel)
            }

            ReasonCancel.PASSENGER_WITH_CHILD -> {
                title_confirm_cancel.visibility = View.VISIBLE
                subtitle_cancel.visibility = View.GONE
                subtitle_confirm_cancel.visibility = View.GONE
                cancel.text = getString(R.string.cancel)
            }

            ReasonCancel.PASSENGER_ERROR -> {
                //todo
            }

            ReasonCancel.PASSENGER_GO_OUT -> {
                title_confirm_cancel.visibility = View.GONE
                subtitle_cancel.visibility = View.VISIBLE
                subtitle_confirm_cancel.visibility = View.VISIBLE
                subtitle_confirm_cancel.text = getString(R.string.passenger_go_out_text)
                cancel.text = getString(R.string.cancel)
            }

            ReasonCancel.OTHER_REASON -> {
                getOtherReasonComment()
            }
        }

        confirmCancelBottomSheet?.state = BottomSheetBehavior.STATE_EXPANDED
        main_coordinator.elevation = 0f
    }


    private fun getOtherReasonComment() {
        commentBottomSheet?.state = BottomSheetBehavior.STATE_EXPANDED

        if (!comment_text.isFocused) {
            comment_text.requestFocus()
            //set a little timer to open keyboard
            Handler().postDelayed({
                val activity = activity as? MapOrderView
                activity?.let {
                    Keyboard.showKeyboard(activity)
                }
            }, 200)
        }
    }


    //passanger cancel this ride
    override fun passengerCancelRide(payment: Int) {
        hideAllBottomSheet()

        (passangerCancelledBottomSheet as? MBottomSheet<*>)?.swipeEnabled = false
        main_info_layout.elevation = 0f
        on_view_cancel.visibility = View.VISIBLE
        on_view_cancel.isClickable = false
        on_view_cancel.alpha = 0.8f

        if (RideStatus.status.status > StatusRide.WAIT_FOR_DRIVER.status) {
            payment_sum.text = payment.toString().plus(" ₽")
            payment_sum.visibility = View.VISIBLE
            text_payment_for_cancel.visibility = View.VISIBLE
        }

        passangerCancelledBottomSheet?.state = BottomSheetBehavior.STATE_EXPANDED
    }


    private fun setBottomSheet() {
        cancelBottomSheet = BottomSheetBehavior.from<View>(reasons_bottom_sheet)
        confirmCancelBottomSheet = BottomSheetBehavior.from<View>(confirm_cancel_bottom_sheet)
        passangerCancelledBottomSheet =
            BottomSheetBehavior.from<View>(passanger_cancelled_bottom_sheet)
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


        commentBottomSheet?.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                onChangedStateComment(newState)
            }
        })
    }


    private fun onSlideCancelReason(slideOffset: Float) {
        if (slideOffset > 0) {
            on_view_cancel.alpha = slideOffset * 0.8f
        }
    }


    private fun onChangedStateCancelReason(newState: Int) {
        if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
            on_view_cancel.visibility = View.GONE
            main_info_layout.elevation = 30f
            comment_text.clearFocus()
        } else {
            confirmCancelBottomSheet?.state = BottomSheetBehavior.STATE_COLLAPSED
            on_view_cancel?.visibility = View.VISIBLE
            main_info_layout?.elevation = 0f
        }
    }


    private fun onChangedStateConfirmCancel(newState: Int) {
        try {
            if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                main_coordinator.elevation = 10f
            } else {
                main_coordinator.elevation = 0f
            }
        } catch (ex: java.lang.Exception) {
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
            on_view_cancel.visibility = View.VISIBLE
            main_info_layout.elevation = 0f
        }
    }


    private fun openNavigator(latitude: Double, longitude: Double) {
//        val uri = Uri.parse("yandexnavi://")
//        val intent = Intent(Intent.ACTION_VIEW, uri)
//        intent.setPackage("ru.yandex.yandexnavi")
//
//// Проверяет, установлено ли приложение.
//        val packageManager = App.appComponent.getContext().packageManager
//        val activities = packageManager.queryIntentActivities(intent, 0)
//        val isIntentSafe = activities.size > 0
//        if (isIntentSafe) {
//
////Запускает Яндекс.Навигатор.
//            startActivity(intent)
//        }
//todo переделать тут все
        val gmmIntentUri = String.format(
            Locale.ENGLISH, "geo:%f,%f?q=%f,%f",
            latitude, longitude, latitude, longitude
        )
        val mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse(gmmIntentUri))
        startActivity(mapIntent)
    }


    override fun getMap(): MapView? {
        return mapView()
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
                                if (height != null) {
                                    layoutParams.setMargins(0, 0, 0, height - 100)
                                }
                                getMap()?.layoutParams = layoutParams
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


    private fun startProcessBlock() {
        if (blockHandler == null) {
            blockHandler = Handler()
        }

        blockHandler?.postDelayed(object : Runnable {
            override fun run() {
                isBlock = false
                blockHandler?.postDelayed(this, 1500)
            }
        }, 0)
    }


    override fun onBackPressed(): Boolean {
        hideAllBottomSheet()
        return false
    }


    override fun finish() {
        finishActivity()
    }


    override fun onDestroy() {
        trackRidePresenter.onDestroy()
        trackRidePresenter.instance().detachView()
        super.onDestroy()
    }


}