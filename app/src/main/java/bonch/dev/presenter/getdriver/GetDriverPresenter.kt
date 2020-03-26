package bonch.dev.presenter.getdriver

import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.LinearLayoutManager
import bonch.dev.MainActivity
import bonch.dev.R
import bonch.dev.model.getdriver.GetDriverModel
import bonch.dev.model.getdriver.pojo.*
import bonch.dev.model.getdriver.pojo.Coordinate.fromAdr
import bonch.dev.presenter.getdriver.adapters.DriversListAdapter
import bonch.dev.utils.Constants
import bonch.dev.utils.Constants.CREATE_RIDE_VIEW
import bonch.dev.utils.Constants.FROM
import bonch.dev.utils.Constants.GET_DRIVER_VIEW
import bonch.dev.utils.Constants.MAIN_FRAGMENT
import bonch.dev.utils.Constants.REASON1
import bonch.dev.utils.Constants.REASON2
import bonch.dev.utils.Constants.REASON3
import bonch.dev.utils.Constants.REASON4
import bonch.dev.utils.Constants.RIDE_DETAIL_INFO
import bonch.dev.utils.Constants.TO
import bonch.dev.utils.Coordinator.replaceFragment
import bonch.dev.view.getdriver.DetailRideView
import bonch.dev.view.getdriver.DriverInfoView
import bonch.dev.view.getdriver.GetDriverView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.yandex.mapkit.Animation
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import kotlinx.android.synthetic.main.get_driver_fragment.*
import kotlinx.android.synthetic.main.get_driver_fragment.view.*
import kotlinx.android.synthetic.main.get_driver_fragment.view.on_map_view_main
import kotlinx.android.synthetic.main.get_driver_layout.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class GetDriverPresenter(val getDriverView: GetDriverView) {

    private var driverInfoView: DriverInfoView? = null
    private var driversListAdapter: DriversListAdapter? = null
    private var getDriverModel: GetDriverModel? = null
    private var handler: Handler? = null
    private var isAnimaionSearching = false
    var root: View? = null

    init {
        if (getDriverModel == null) {
            getDriverModel = GetDriverModel(this)
        }
    }


    fun receiveUserData(bundle: Bundle?, root: View) {
        bundle?.let {
            val from: Ride = it.getParcelable(FROM)!!
            val to: Ride = it.getParcelable(TO)!!
            val rideDetailInfo: RideDetailInfo? = it.getParcelable(RIDE_DETAIL_INFO)

            root.from_address.text = from.address!!
            root.to_address.text = to.address!!

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


    fun onSlideCancelReason(slideOffset: Float, root: View) {
        val onMapView = root.on_map_view

        if (slideOffset > 0) {
            onMapView.alpha = slideOffset * 0.8f
        }
    }


    fun onSlideConfirmCancel(slideOffset: Float, root: View) {
        val onView = root.on_view_cancel_reason

        if (slideOffset > 0) {
            onView.alpha = slideOffset * 0.5f
        }
    }


    fun onChangedStateCancelReason(newState: Int, root: View) {
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


    fun onChangedStateConfirmCancel(newState: Int, root: View) {
        val onMapView = root.on_view_cancel_reason

        if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
            onMapView.visibility = View.GONE
        } else {
            onMapView.visibility = View.VISIBLE
        }
    }


    fun selectDriver(driver: Driver) {
        root?.on_map_view_main?.visibility = View.GONE
        root?.get_driver_center_text?.visibility = View.GONE

        //dynamic replace layout
        var view =
            root!!.findViewById<CoordinatorLayout>(R.id.get_driver_layout) as View
        val parent = view.parent as ViewGroup
        val index = parent.indexOfChild(view)
        view.visibility = View.GONE
        view = getDriverView.layoutInflater.inflate(R.layout.driver_info_layout, parent, false)
        parent.addView(view, index)

        //stop getting new driver
        handler?.removeCallbacksAndMessages(null)

        //create next screen
        driverInfoView = DriverInfoView(getDriverView)
        driverInfoView!!.onCreateView(driver)
    }


    fun startSearchDrivers(root: View) {
        //start animation searching
        val arguments = getDriverView.arguments
        val userPoint: RidePoint? = arguments?.getParcelable(Constants.USER_POINT)
        if(userPoint != null){
            getDriverView.startAnimSearch(Point(userPoint.latitude, userPoint.longitude))
        }
        isAnimaionSearching = true

        //init recycler drivers item
        initializeListDrivers(root)

        //TODO
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
            val arguments = getDriverView.arguments
            val userPoint: RidePoint? = arguments?.getParcelable(Constants.USER_POINT)
            val point = Point(userPoint!!.latitude, userPoint.longitude)
            val zoom = getDriverView.mapView!!.map.cameraPosition.zoom

            getDriverView.moveCamera(zoom, point)

            isAnimaionSearching = false
        }
    }


    private fun initializeListDrivers(root: View) {
        val context = getDriverView.context!!

        driversListAdapter = DriversListAdapter(ArrayList(), context, this)

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


    fun cancelDone(reasonID: Int) {
        //TODO send reason to server
        //stop getting new driver
        handler?.removeCallbacksAndMessages(null)
        DriverObject.driver = null
        fromAdr = null
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
                ReasonCancel.reasonID = REASON3
            }

            REASON4 -> {
                //другая причина
                ReasonCancel.reasonID = REASON4
            }
        }

        replaceFragment(MAIN_FRAGMENT, null, fm)
    }


    fun notCancel() {
        getDriverView.view?.on_map_view?.visibility = View.GONE
        getDriverView.view?.on_view_cancel_reason?.visibility = View.GONE

        getDriverView.cancelBottomSheetBehavior!!.state = BottomSheetBehavior.STATE_COLLAPSED
        getDriverView.confirmCancelBottomSheetBehavior!!.state = BottomSheetBehavior.STATE_COLLAPSED
    }


    fun checkBackground(isShow: Boolean) {
        val onMapView = getDriverView.on_map_view_main

        if (isShow) {
            onMapView.visibility = View.VISIBLE
            onMapView.alpha = 0.8f
        } else {
            onMapView.visibility = View.GONE
            onMapView.alpha = 0f
        }
    }

}
