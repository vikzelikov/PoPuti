package bonch.dev.presentation.modules.passenger.getdriver.view

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import bonch.dev.MainActivity
import bonch.dev.R
import bonch.dev.domain.entities.passenger.getdriver.Driver
import bonch.dev.domain.entities.passenger.getdriver.DriverObject
import bonch.dev.domain.entities.passenger.getdriver.ReasonCancel
import bonch.dev.domain.entities.common.ride.RideInfo
import bonch.dev.domain.utils.Keyboard
import bonch.dev.presentation.base.MBottomSheet
import bonch.dev.presentation.interfaces.ParentHandler
import bonch.dev.presentation.interfaces.ParentMapHandler
import bonch.dev.presentation.modules.passenger.getdriver.GetDriverComponent
import bonch.dev.presentation.modules.passenger.getdriver.adapters.DriversListAdapter
import bonch.dev.presentation.modules.passenger.getdriver.presenter.ContractPresenter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.user_location.UserLocationLayer
import kotlinx.android.synthetic.main.get_driver_layout.*
import javax.inject.Inject


class GetDriverView : Fragment(), ContractView.IGetDriverView {

    @Inject
    lateinit var getDriverPresenter: ContractPresenter.IGetDriverPresenter

    @Inject
    lateinit var driversListAdapter: DriversListAdapter

    private val RIDE_DETAIL_INFO = "RIDE_DETAIL_INFO"

    private var cancelBottomSheet: BottomSheetBehavior<*>? = null
    private var confirmCancelBottomSheet: BottomSheetBehavior<*>? = null
    private var expiredTimeBottomSheet: BottomSheetBehavior<*>? = null
    private var confirmGetBottomSheet: BottomSheetBehavior<*>? = null
    private var commentBottomSheet: BottomSheetBehavior<*>? = null

    lateinit var locationLayer: ParentMapHandler<UserLocationLayer>
    lateinit var nextFragment: ParentHandler<FragmentManager>
    lateinit var mapView: ParentMapHandler<MapView>


    init {
        GetDriverComponent.getDriverComponent?.inject(this)

        getDriverPresenter.instance().attachView(this)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.get_driver_layout, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        correctMapView()

        receiveUserData(arguments)

        initializeAdapter()

        getDriverPresenter.startSearchDrivers()

        setBottomSheet()

        setListeners()
    }


    private fun receiveUserData(bundle: Bundle?) {
        bundle?.let {
            val rideInfo: RideInfo? = it.getParcelable(RIDE_DETAIL_INFO)

            rideInfo?.let {
                from_address.text = rideInfo.fromAdr?.address
                to_address.text = rideInfo.toAdr?.address

                if (rideInfo.price != null) {
                    offer_price.text = rideInfo.price.toString()
                }

                val comment = rideInfo.comment
                comment?.let {
                    if (comment.isNotEmpty()) {
                        comment_min_text.text = rideInfo.comment
                    } else {
                        comment_btn.visibility = View.GONE
                    }
                }
            }
        }
    }


    override fun setListeners() {
        //set deafult reason
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
            getDriverPresenter.cancelDoneOtherReason(comment_text.text.toString())
        }

        cancel.setOnClickListener {
            getDriverPresenter.cancelDone(reasonID)
        }

        expired_time_ok_btn.setOnClickListener {
            getDriverPresenter.timeExpired()
        }

        confirm_accept_driver.setOnClickListener {
            getDriverPresenter.confirmAccept()
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


    override fun startAnimSearch(point: Point) {
        val zoom = getMap()?.map?.cameraPosition?.zoom?.minus(1)
        zoom?.let {
            Handler().postDelayed({
                getDriverPresenter.moveCamera(zoom, point)
            }, 1000)
        }
    }


    private fun setConfirmAcceptData(driver: Driver?) {
        //set data in BottomSheet for confirm or cancel

        driver?.let {
            bs_driver_name.text = it.nameDriver
            bs_car_name.text = it.carName
            bs_driver_rating.text = it.rating.toString()
            bs_price.text = it.price.toString().plus(" ₽")

            Glide.with(bs_img_driver.context).load(it.imgDriver)
                .apply(RequestOptions().centerCrop().circleCrop())
                .into(bs_img_driver)
        }
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
            on_view_cancel.visibility = View.VISIBLE
            main_info_layout.elevation = 0f
        }
    }


    private fun onChangedStateConfirmCancel(newState: Int) {
        if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
            get_driver_coordinator.elevation = 10f
        } else {
            get_driver_coordinator.elevation = 0f
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


    private fun getCancelReason() {
        cancelBottomSheet?.state = BottomSheetBehavior.STATE_EXPANDED
    }


    private fun getConfirmCancel() {
        confirmCancelBottomSheet?.state = BottomSheetBehavior.STATE_EXPANDED
        get_driver_coordinator.elevation = 0f
    }


    override fun getConfirmAccept() {
        setConfirmAcceptData(DriverObject.driver)
        confirmGetBottomSheet?.state = BottomSheetBehavior.STATE_EXPANDED
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


    override fun getExpiredTimeConfirm() {
        hideAllBottomSheet()

        (expiredTimeBottomSheet as? MBottomSheet<*>)?.swipeEnabled = false
        main_info_layout.elevation = 0f
        on_view_cancel.visibility = View.VISIBLE
        on_view_cancel.alpha = 0.8f

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

        comment_text.clearFocus()
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
        driversListAdapter.list = arrayListOf()

        driver_list.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = driversListAdapter
        }

        driver_list.visibility = View.VISIBLE
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
                            }
                        }

                        mainHandler.post(myRunnable)

                        break
                    }
                } catch (ex: java.lang.Exception) {
                    break
                }

            }
        }).start()
    }


    override fun getAdapter(): DriversListAdapter {
        return driversListAdapter
    }


    override fun checkoutBackground(isShow: Boolean) {
        //unsupported exception
        try {
            if (on_view_main != null) {
                if (isShow) {
                    get_driver_center_text?.visibility = View.VISIBLE
                    on_view_main?.alpha = 0f
                } else {
                    get_driver_center_text?.visibility = View.GONE
                    on_view_main?.alpha = 0.8f
                }
            }
        } catch (ex: Exception) {
        }
    }


    override fun removeBackground() {
        try {
            if (on_view_main != null) {
                get_driver_center_text?.visibility = View.GONE
                on_view_main?.visibility = View.GONE
            }
        } catch (ex: Exception) {
        }
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
        (activity as? MainActivity)?.showNotification(text)
    }


    override fun showLoading() {
        (activity as? MainActivity)?.showLoading()
    }


    override fun hideLoading() {
        (activity as? MainActivity)?.hideLoading()
    }


    override fun getUserLocationLayer(): UserLocationLayer? {
        return locationLayer()
    }


    override fun getMap(): MapView? {
        return mapView()
    }


    override fun nextFragment() {
        val fm = (activity as? MainActivity)?.supportFragmentManager
        fm?.let {
            nextFragment(it)
        }
    }


    override fun getRecyclerView(): RecyclerView {
        return driver_list
    }


    override fun onObjectUpdated() {
        getDriverPresenter.onUserLocationAttach()
    }

}