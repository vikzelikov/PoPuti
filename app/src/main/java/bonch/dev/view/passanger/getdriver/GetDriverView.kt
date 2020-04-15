package bonch.dev.view.passanger.getdriver

import android.graphics.Color
import android.graphics.PointF
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import bonch.dev.MainActivity
import bonch.dev.Permissions
import bonch.dev.R
import bonch.dev.model.passanger.getdriver.pojo.DriverObject.driver
import bonch.dev.presenter.passanger.getdriver.GetDriverPresenter
import bonch.dev.utils.Constants
import bonch.dev.utils.Constants.REASON1
import bonch.dev.utils.Constants.REASON2
import bonch.dev.utils.Constants.REASON3
import bonch.dev.utils.Constants.REASON4
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.directions.DirectionsFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.layers.ObjectEvent
import com.yandex.mapkit.logo.Alignment
import com.yandex.mapkit.logo.HorizontalAlignment
import com.yandex.mapkit.logo.VerticalAlignment
import com.yandex.mapkit.map.*
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.search.SearchFactory
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.mapkit.user_location.UserLocationObjectListener
import com.yandex.mapkit.user_location.UserLocationView
import com.yandex.runtime.image.ImageProvider
import kotlinx.android.synthetic.main.driver_info_layout.view.cancel_ride
import kotlinx.android.synthetic.main.driver_info_layout.view.reasons_bottom_sheet
import kotlinx.android.synthetic.main.get_driver_layout.view.*

class GetDriverView : Fragment(), UserLocationObjectListener, CameraListener {

    var mapView: MapView? = null
    var cancelBottomSheetBehavior: BottomSheetBehavior<*>? = null
    var confirmCancelBottomSheetBehavior: BottomSheetBehavior<*>? = null
    var expiredTimeBottomSheetBehavior: BottomSheetBehavior<*>? = null
    var confirmGetBottomSheetBehavior: BottomSheetBehavior<*>? = null
    var driverCancelledBottomSheet: BottomSheetBehavior<*>? = null
    var getDriverPresenter: GetDriverPresenter? = null
    var userLocationLayer: UserLocationLayer? = null


    init {
        if (getDriverPresenter == null) {
            getDriverPresenter = GetDriverPresenter(this)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //init map
        MapKitFactory.setApiKey(Constants.API_KEY)
        MapKitFactory.initialize(context)
        SearchFactory.initialize(context)
        DirectionsFactory.initialize(context)

        val root = inflater.inflate(R.layout.get_driver_fragment, container, false)
        mapView = root.findViewById(R.id.map) as MapView

        mapView?.map?.addCameraListener(this)

        mapView?.map?.apply {
            val alignment = Alignment(HorizontalAlignment.RIGHT, VerticalAlignment.TOP)
            logo.setAlignment(alignment)
        }

        getDriverPresenter?.receiveUserData(arguments, root)

        setBottomSheet(root)

        setListeners(root)

        getDriverPresenter?.startSearchDrivers(root)

        getDriverPresenter?.root = root

        if (driver != null) {
            //ride already created
            //TODO update driver status net
            getDriverPresenter?.selectDriver(driver!!)
        }

        return root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //access geo permission
        if (Permissions.isAccess(Constants.GEO_PERMISSION, activity as MainActivity)) {
            setUserLocation()
        } else {
            Permissions.access(Constants.GEO_PERMISSION_REQUEST, this)
        }
        super.onViewCreated(view, savedInstanceState)
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (Permissions.isAccess(Constants.GEO_PERMISSION, activity as MainActivity)) {
            setUserLocation()
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    override fun onObjectRemoved(view: UserLocationView) {}

    //add user location icon
    override fun onObjectAdded(userLocationView: UserLocationView) {
        val pinIcon = userLocationView.pin.useCompositeIcon()

        //for moving
        val userMark = getDriverPresenter?.getBitmap(R.drawable.ic_user_mark)
        userLocationView.arrow.setIcon(ImageProvider.fromBitmap(userMark))

        userLocationLayer!!.setAnchor(
            PointF(
                (mapView!!.width * 0.5).toFloat(),
                (mapView!!.height * 0.5).toFloat()
            ),
            PointF(
                (mapView!!.width * 0.5).toFloat(),
                (mapView!!.height * 0.83).toFloat()
            )
        )

        //for staying
        pinIcon.setIcon(
            "pin",
            ImageProvider.fromBitmap(userMark),
            IconStyle().setAnchor(PointF(0.5f, 0.5f))
                .setRotationType(RotationType.ROTATE)
                .setZIndex(1f)
        )

        userLocationView.accuracyCircle.fillColor = Color.TRANSPARENT
    }


    override fun onObjectUpdated(view: UserLocationView, event: ObjectEvent) {
        getDriverPresenter?.onUserLocationAttach()
    }


    private fun setUserLocation() {
        val mapKit = MapKitFactory.getInstance()
        mapView!!.map.move(CameraPosition(Point(0.0, 0.0), 16f, 0f, 0f))

        //init user location service
        userLocationLayer = mapKit.createUserLocationLayer(mapView!!.mapWindow)
        userLocationLayer?.let {
            it.isHeadingEnabled = false
            it.isVisible = true
            it.setObjectListener(this)
        }
    }


    override fun onCameraPositionChanged(
        p0: Map,
        p1: CameraPosition,
        p2: CameraUpdateSource,
        p3: Boolean
    ) {
        if (p3) {
            userLocationLayer?.resetAnchor()
        }
    }


    private fun setListeners(r: View) {
        //set deafult reason
        var reasonID: Int = REASON4

        r.cancel_ride.setOnClickListener {
            getDriverPresenter?.getCancelReason()
        }

        r.case1.setOnClickListener {
            reasonID = REASON1
            getDriverPresenter?.getConfirmCancel()
        }

        r.case2.setOnClickListener {
            reasonID = REASON2
            getDriverPresenter?.getConfirmCancel()
        }

        r.case3.setOnClickListener {
            reasonID = REASON3
            getDriverPresenter?.getConfirmCancel()
        }

        r.case4.setOnClickListener {
            reasonID = REASON4
            getDriverPresenter?.getConfirmCancel()
        }

        r.cancel.setOnClickListener {
            getDriverPresenter?.cancelDone(reasonID)
        }

        r.not_cancel.setOnClickListener {
            getDriverPresenter?.notCancel()
        }

        r.on_map_view.setOnClickListener {
            getDriverPresenter?.notCancel()
        }

        r.expired_time_ok_btn.setOnClickListener {
            getDriverPresenter?.timeExpiredOk()
        }

        r.confirm_accept_driver.setOnClickListener {
            getDriverPresenter?.confirmAccept()
        }
    }


    private fun setBottomSheet(root: View) {
        cancelBottomSheetBehavior =
            BottomSheetBehavior.from<View>(root.reasons_bottom_sheet)
        confirmCancelBottomSheetBehavior =
            BottomSheetBehavior.from<View>(root.confirm_cancel_bottom_sheet)
        expiredTimeBottomSheetBehavior =
            BottomSheetBehavior.from<View>(root.time_expired_bottom_sheet)
        confirmGetBottomSheetBehavior =
            BottomSheetBehavior.from<View>(root.confirm_get_bottom_sheet)
        driverCancelledBottomSheet =
            BottomSheetBehavior.from<View>(root.driver_cancelled_bottom_sheet)


        cancelBottomSheetBehavior!!.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                getDriverPresenter?.onSlideCancelReason(slideOffset, root)
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                getDriverPresenter?.onChangedStateCancelReason(newState, root)
            }
        })


        confirmCancelBottomSheetBehavior!!.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                getDriverPresenter?.onSlideConfirmCancel(slideOffset, root)
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                getDriverPresenter?.onChangedStateConfirmCancel(newState, root)
            }
        })


        confirmGetBottomSheetBehavior!!.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                getDriverPresenter?.onSlideCancelReason(slideOffset, root)
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                getDriverPresenter?.onChangedStateCancelReason(newState, root)
            }
        })


        driverCancelledBottomSheet!!.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                getDriverPresenter?.onSlideCancelReason(slideOffset, root)
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                getDriverPresenter?.onChangedStateCancelReason(newState, root)
            }
        })
    }


    fun startAnimSearch(point: Point) {
        //TODO delete stop timer
        Handler().postDelayed({
            val zoom = mapView!!.map.cameraPosition.zoom - 1
            moveCamera(zoom, point)
        }, 2000)
    }


    fun moveCamera(zoom: Float, point: Point) {
        mapView?.map?.move(
            CameraPosition(point, zoom, 0.0f, 0.0f),
            Animation(Animation.Type.SMOOTH, 30f),
            null
        )
    }


    override fun onStop() {
        super.onStop()
        MapKitFactory.getInstance().onStop()
        mapView!!.onStop()
    }


    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        mapView!!.onStart()
    }


    fun backPressed(): Boolean {
        return true
    }

}
