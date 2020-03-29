package bonch.dev.view.passanger.getdriver

import android.graphics.Color
import android.graphics.PointF
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
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
    var getDriverPresenter: GetDriverPresenter? = null
    private var userLocationLayer: UserLocationLayer? = null


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

        setUserLocation()

        mapView!!.map.addCameraListener(this)
        mapView!!.map.isRotateGesturesEnabled = false

        getDriverPresenter?.receiveUserData(arguments, root)

        setBottomSheet(root)

        setListeners(root)

        getDriverPresenter?.startSearchDrivers(root)

        getDriverPresenter?.root = root

        if(driver != null){
            //ride already created
            //TODO update driver status
            getDriverPresenter?.selectDriver(driver!!)
        }

        return root
    }


    override fun onObjectRemoved(view: UserLocationView) {}

    //add user location icon
    override fun onObjectAdded(userLocationView: UserLocationView) {
        val pinIcon = userLocationView.pin.useCompositeIcon()
        //TODO change to svg
        userLocationView.arrow.setIcon(
            ImageProvider.fromResource(
                context,
                R.drawable.ic_user_mark,
                true
            )
        )

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

        pinIcon.setIcon(
            "pin",
            ImageProvider.fromResource(context, R.drawable.ic_user_mark),
            IconStyle().setAnchor(PointF(0.5f, 0.5f))
                .setRotationType(RotationType.ROTATE)
                .setZIndex(1f)
        )

        userLocationView.accuracyCircle.fillColor = Color.TRANSPARENT
    }


    override fun onObjectUpdated(view: UserLocationView, event: ObjectEvent) {}


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


    private fun setListeners(root: View) {
        var reasonID: Int = REASON4
        val cancelRideBtn = root.cancel_ride
        val cancelBtn = root.cancel
        val notCancelBtn = root.not_cancel
        val timeExpiredBtn = root.expired_time_ok_btn
        val onMapView = root.on_map_view
        val case1 = root.case1
        val case2 = root.case2
        val case3 = root.case3
        val case4 = root.case4

        cancelRideBtn.setOnClickListener {
            getDriverPresenter?.getCancelReason()
        }

        case1.setOnClickListener {
            reasonID = REASON1
            getDriverPresenter?.getConfirmCancel()
        }

        case2.setOnClickListener {
            reasonID = REASON2
            getDriverPresenter?.getConfirmCancel()
        }

        case3.setOnClickListener {
            reasonID = REASON3
            getDriverPresenter?.getConfirmCancel()
        }

        case4.setOnClickListener {
            reasonID = REASON4
            getDriverPresenter?.getConfirmCancel()
        }

        cancelBtn.setOnClickListener {
            getDriverPresenter?.cancelDone(reasonID)
        }

        notCancelBtn.setOnClickListener {
            getDriverPresenter?.notCancel()
        }

        onMapView.setOnClickListener {
            getDriverPresenter?.notCancel()
        }

        timeExpiredBtn.setOnClickListener {
            getDriverPresenter?.timeExpiredOk()
        }
    }


    private fun setBottomSheet(root: View) {
        cancelBottomSheetBehavior = BottomSheetBehavior.from<View>(root.reasons_bottom_sheet)
        confirmCancelBottomSheetBehavior =
            BottomSheetBehavior.from<View>(root.confirm_cancel_bottom_sheet)
        expiredTimeBottomSheetBehavior = BottomSheetBehavior.from<View>(root.time_expired_bottom_sheet)


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


        expiredTimeBottomSheetBehavior!!.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                getDriverPresenter?.onSlideCancelReason(slideOffset,  root)
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                getDriverPresenter?.onChangedStateCancelReason(newState, root)
            }
        })
    }


    fun startAnimSearch(point: Point) {
        //TODO
        val zoom = mapView!!.map.cameraPosition.zoom - 2
        Handler().postDelayed({
            moveCamera(zoom, point)
        }, 1500)

    }


    fun moveCamera(zoom: Float, point: Point) {
        mapView!!.map.move(
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
