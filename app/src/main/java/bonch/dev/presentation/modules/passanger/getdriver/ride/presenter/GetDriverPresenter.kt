package bonch.dev.presentation.modules.passanger.getdriver.ride.presenter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import bonch.dev.MainActivity
import bonch.dev.R
import bonch.dev.data.repository.passanger.getdriver.GetDriverModel
import bonch.dev.data.repository.passanger.getdriver.pojo.*
import bonch.dev.data.repository.passanger.getdriver.pojo.Coordinate.toAdr
import bonch.dev.presentation.modules.passanger.getdriver.ride.adapters.DriversListAdapter
import bonch.dev.domain.utils.Constants.FROM
import bonch.dev.domain.utils.Constants.MAIN_FRAGMENT
import bonch.dev.domain.utils.Constants.REASON1
import bonch.dev.domain.utils.Constants.REASON2
import bonch.dev.domain.utils.Constants.REASON3
import bonch.dev.domain.utils.Constants.REASON4
import bonch.dev.domain.utils.Constants.RIDE_DETAIL_INFO
import bonch.dev.domain.utils.Constants.TO
import bonch.dev.route.Coordinator.replaceFragment
import bonch.dev.presentation.modules.passanger.getdriver.ride.view.DriverInfoView
import bonch.dev.presentation.modules.passanger.getdriver.ride.view.GetDriverView
import bonch.dev.presentation.base.MBottomSheet
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.yandex.mapkit.geometry.Point
import kotlinx.android.synthetic.main.get_driver_fragment.*
import kotlinx.android.synthetic.main.get_driver_fragment.view.*
import kotlinx.android.synthetic.main.get_driver_layout.*
import kotlinx.android.synthetic.main.get_driver_layout.view.*
import java.lang.Exception


class GetDriverPresenter(val getDriverView: GetDriverView) {

    private var driverInfoView: DriverInfoView? = null
    private var driversListAdapter: DriversListAdapter? = null
    private var getDriverModel: GetDriverModel? = null
    private var handler: Handler? = null
    private var isAnimaionSearching = false
    lateinit var root: View

    init {
        if (getDriverModel == null) {
            getDriverModel = GetDriverModel(this)
        }
    }


    fun receiveUserData(bundle: Bundle?) {
        bundle?.let {
            val from: Ride = it.getParcelable(FROM)!!
            val to: Ride = it.getParcelable(TO)!!
            val rideDetailInfo: RideDetailInfo? = it.getParcelable(RIDE_DETAIL_INFO)

            root.from_address.text = from.address
            root.to_address.text = to.address

            if (rideDetailInfo?.priceRide != null) {
                root.offer_price.text = rideDetailInfo.priceRide
            }

            if (rideDetailInfo?.comment!!.isNotEmpty()) {
                root.comment_min_text.text = rideDetailInfo.comment
            } else {
                root.comment_btn.visibility = View.GONE
            }
        }
    }


    fun confirmAccept() {
        //stop main timer
        val driver = DriverObject.driver

        if (driver != null) {
            DriverMainTimer.getInstance()
                ?.cancel()
            selectDriver(driver)

        }
    }


    fun onSlideCancelReason(slideOffset: Float) {
        val onMapView = root.on_map_view

        if (slideOffset > 0) {
            onMapView.alpha = slideOffset * 0.8f
        }
    }


    fun onSlideConfirmCancel(slideOffset: Float) {
        val onView = root.on_view_cancel_reason

        if (slideOffset > 0) {
            onView.alpha = slideOffset * 0.5f
        }
    }


    fun onChangedStateCancelReason(newState: Int) {
        val onMapView = root.on_map_view
        val mainInfoLayout = root.main_info_layout

        if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
            onMapView.visibility = View.GONE
            mainInfoLayout.elevation = 30f
        } else {
            getDriverView.confirmCancelBottomSheetBehavior!!.state =
                BottomSheetBehavior.STATE_COLLAPSED
            onMapView.visibility = View.VISIBLE
            mainInfoLayout.elevation = 0f
        }
    }


    fun onChangedStateConfirmCancel(newState: Int) {
        val onMapView = root.on_view_cancel_reason

        if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
            onMapView.visibility = View.GONE
        } else {
            onMapView.visibility = View.VISIBLE
        }
    }


    fun selectDriver(driver: Driver) {
        root.on_map_view_main?.visibility = View.GONE
        root.get_driver_center_text?.visibility = View.GONE

        //dynamic replace layout
        replaceDynamic()

        //stop getting new driver
        handler?.removeCallbacksAndMessages(null)

        //create next screen
        driverInfoView =
            DriverInfoView(
                getDriverView
            )
        driverInfoView!!.onCreateView(driver)
    }

    fun startSearchDrivers(root: View) {
        //init recycler drivers item
        initializeListDrivers(root)

        //TODO replace to searching with server
        handler = Handler()
        handler!!.postDelayed(object : Runnable {
            override fun run() {
                getDriverModel?.getNewDriver()
                handler?.postDelayed(this, 3000)
            }
        }, 10000)
    }


    fun setNewDriverOffer(driver: Driver) {
        driversListAdapter?.setNewDriver(driver)

        if (isAnimaionSearching) {
            //animation off
            val zoom = getDriverView.mapView?.map?.cameraPosition?.zoom
            val userPoint = getUserPoint()

            if (zoom != null && userPoint != null) {
                getDriverView.moveCamera(zoom, userPoint)
            }
        }
    }


    private fun setConfirmAcceptData(driver: Driver?) {
        //set data in BottomSheet for confirm or cancel
        val r = getDriverView

        driver?.let {
            r.bs_driver_name.text = it.nameDriver
            r.bs_car_name.text = it.carName
            r.bs_driver_rating.text = it.rating.toString()
            r.bs_price.text = it.price.toString().plus(" ₽")

            Glide.with(r.bs_img_driver.context).load(it.imgDriver)
                .apply(RequestOptions().centerCrop().circleCrop())
                .into(r.bs_img_driver)
        }
    }


    private fun initializeListDrivers(root: View) {
        val context = root.context

        driversListAdapter =
            DriversListAdapter(
                ArrayList(),
                context,
                this
            )

        root.driver_list.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        root.driver_list.adapter = driversListAdapter

        root.driver_list.visibility = View.VISIBLE
    }


    fun getCancelReason() {
        getDriverView.cancelBottomSheetBehavior!!.state = BottomSheetBehavior.STATE_EXPANDED
    }


    fun getConfirmCancel() {
        getDriverView.confirmCancelBottomSheetBehavior!!.state = BottomSheetBehavior.STATE_EXPANDED
    }


    fun getConfirmAccept() {
        setConfirmAcceptData(DriverObject.driver)

        getDriverView.confirmGetBottomSheetBehavior!!.state = BottomSheetBehavior.STATE_EXPANDED
    }


    fun getExpiredTimeConfirm() {
        val bottomSheet = getDriverView.expiredTimeBottomSheetBehavior
        (bottomSheet as MBottomSheet<*>).swipeEnabled = false

        bottomSheet.state = BottomSheetBehavior.STATE_EXPANDED
    }


    fun cancelDone(reasonID: Int) {
        //TODO send reason to server
        //stop getting new driver
        handler?.removeCallbacksAndMessages(null)
        DriverObject.driver = null
        DriverMainTimer.getInstance()
            ?.cancel()
        DriverMainTimer.deleteInstance()
        val fm = (getDriverView.activity as MainActivity).supportFragmentManager

        when (reasonID) {
            REASON1 -> {
                //Водитель попросил отменить
                ReasonCancel.reasonID = REASON1
            }

            REASON2 -> {
                //Долго ждать
                ReasonCancel.reasonID = REASON2
            }

            REASON3 -> {
                //по ошибке
                toAdr = null
                ReasonCancel.reasonID = REASON3
            }

            REASON4 -> {
                //другая причина
                toAdr = null
                ReasonCancel.reasonID = REASON4
            }
        }

        (getDriverView.activity as MainActivity).showNotification(
            getDriverView.resources.getString(
                R.string.rideCancel
            )
        )

        replaceFragment(MAIN_FRAGMENT, null, fm)
    }


    fun timeExpiredOk() {
        //clear data and redirect
        handler?.removeCallbacksAndMessages(null)
        DriverObject.driver = null
        toAdr = null

        val fm = (getDriverView.activity as MainActivity).supportFragmentManager
        replaceFragment(MAIN_FRAGMENT, null, fm)
    }


    fun hideAllBottomSheet() {
        if (getDriverView.expiredTimeBottomSheetBehavior!!.state == BottomSheetBehavior.STATE_COLLAPSED) {
            getDriverView.view?.on_map_view?.visibility = View.GONE
            getDriverView.view?.on_view_cancel_reason?.visibility = View.GONE

            getDriverView.cancelBottomSheetBehavior!!.state = BottomSheetBehavior.STATE_COLLAPSED
            getDriverView.confirmGetBottomSheetBehavior!!.state =
                BottomSheetBehavior.STATE_COLLAPSED
            getDriverView.confirmCancelBottomSheetBehavior!!.state =
                BottomSheetBehavior.STATE_COLLAPSED
        }
    }


    fun hideConfirmAccept() {
        getDriverView.confirmGetBottomSheetBehavior!!.state = BottomSheetBehavior.STATE_COLLAPSED
    }


    private fun getUserPoint(): Point? {
        val userLocation = getDriverView.userLocationLayer
        return userLocation?.cameraPosition()?.target
    }


    fun onUserLocationAttach() {
        //start animation searching
        val userPoint = getUserPoint()

        if (userPoint != null && !isAnimaionSearching) {
            getDriverView.startAnimSearch(
                Point(
                    userPoint.latitude,
                    userPoint.longitude
                )
            )

            isAnimaionSearching = true
        }
    }


    private fun replaceDynamic() {
        var view = root.findViewById<CoordinatorLayout>(R.id.get_driver_layout) as View
        val parent = view.parent as ViewGroup
        val index = parent.indexOfChild(view)
        view.visibility = View.GONE
        view = getDriverView.layoutInflater.inflate(R.layout.driver_info_layout, parent, false)
        parent.addView(view, index)
    }


    fun correctMapView() {
        Thread(Runnable {
            while (true) {
                val height = root.main_info_layout.height
                if (height > 0) {
                    val layoutParams: RelativeLayout.LayoutParams = RelativeLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT
                    )
                    //"-10" for correct view radius corners
                    layoutParams.setMargins(0, 0, 0, height - 10)
                    root.map.layoutParams = layoutParams
                    break
                }
            }
        }).start()
    }


    fun checkBackground(isShow: Boolean) {
        //unsupported exception
        try {
            val onMapView = getDriverView.on_map_view_main

            if (onMapView != null) {
                if (isShow) {
                    onMapView.visibility = View.VISIBLE
                    onMapView.alpha = 0.8f
                } else {
                    onMapView.visibility = View.GONE
                    onMapView.alpha = 0f
                }
            }
        }catch (ex: Exception){}

    }


    fun getBitmap(drawableId: Int): Bitmap? {
        return getDriverView.context?.getBitmapFromVectorDrawable(drawableId)
    }


    private fun Context.getBitmapFromVectorDrawable(drawableId: Int): Bitmap? {
        val drawable = ContextCompat.getDrawable(this, drawableId) ?: return null

        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        ) ?: return null
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)

        return bitmap
    }


    fun onBackPressed(): Boolean {
        var isBackPressed = true

        val cancelBottomShee = getDriverView.cancelBottomSheetBehavior
        val confirmCancelBottomSheet = getDriverView.confirmCancelBottomSheetBehavior
        val expiredTimeBottomSheet = getDriverView.expiredTimeBottomSheetBehavior
        val confirmGetBottomSheet = getDriverView.confirmGetBottomSheetBehavior

        if (cancelBottomShee!!.state != BottomSheetBehavior.STATE_COLLAPSED
            || confirmCancelBottomSheet!!.state != BottomSheetBehavior.STATE_COLLAPSED
            || expiredTimeBottomSheet!!.state != BottomSheetBehavior.STATE_COLLAPSED
            || confirmGetBottomSheet!!.state != BottomSheetBehavior.STATE_COLLAPSED) {

            //hide all bottom sheets
            hideAllBottomSheet()

            isBackPressed = false
        }

        if(driverInfoView != null){
            isBackPressed = driverInfoView?.onBackPressed()!!
        }

        return isBackPressed
    }

}
