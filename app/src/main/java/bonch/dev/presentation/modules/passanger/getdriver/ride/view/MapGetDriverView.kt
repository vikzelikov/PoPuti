package bonch.dev.presentation.modules.passanger.getdriver.ride.view

import android.graphics.Color
import android.graphics.PointF
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import bonch.dev.App
import bonch.dev.MainActivity
import bonch.dev.Permissions
import bonch.dev.R
import bonch.dev.di.component.passanger.DaggerGetDriverComponent
import bonch.dev.di.module.passanger.GetDriverModule
import bonch.dev.domain.entities.passanger.getdriver.DriverObject
import bonch.dev.domain.utils.Constants
import bonch.dev.domain.utils.Keyboard
import bonch.dev.presentation.modules.passanger.getdriver.GetDriverComponent
import bonch.dev.presentation.modules.passanger.getdriver.ride.presenter.ContractPresenter
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
import javax.inject.Inject

class MapGetDriverView : Fragment(), UserLocationObjectListener, CameraListener,
    ContractView.IMapGetDriverView {

    @Inject
    lateinit var mapGetDriverPresenter: ContractPresenter.IMapGetDriverPresenter

    private lateinit var mapView: MapView
    private var userLocationLayer: UserLocationLayer? = null


    init {
        initDI()

        GetDriverComponent.getDriverComponent?.inject(this)

        mapGetDriverPresenter.instance().attachView(this)
    }


    //first build DI component
    private fun initDI() {
        if (GetDriverComponent.getDriverComponent == null) {
            GetDriverComponent.getDriverComponent = DaggerGetDriverComponent
                .builder()
                .getDriverModule(GetDriverModule())
                .appComponent(App.appComponent)
                .build()
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

        val root = inflater.inflate(R.layout.map_get_driver_fragment, container, false)
        mapView = root.findViewById(R.id.map) as MapView

        mapView.map?.addCameraListener(this)
        mapView.map?.isRotateGesturesEnabled = false

        mapView.map?.apply {
            val alignment = Alignment(HorizontalAlignment.RIGHT, VerticalAlignment.TOP)
            logo.setAlignment(alignment)
        }

        return root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val activity = activity as? MainActivity
        //access geo permission
        activity?.let {
            if (Permissions.isAccess(Permissions.GEO_PERMISSION, activity)) {
                setUserLocation()
            } else {
                Permissions.access(Permissions.GEO_PERMISSION_REQUEST, this)
            }

            val fm = it.supportFragmentManager
            val driver = DriverObject.driver

            if (driver != null) {
                //ride already created
                //TODO update driver status net
                mapGetDriverPresenter.attachTrackRide(fm)
            } else {
                mapGetDriverPresenter.attachGetDriver(fm)
            }
        }

        super.onViewCreated(view, savedInstanceState)
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (Permissions.isAccess(Permissions.GEO_PERMISSION, activity as MainActivity)) {
            setUserLocation()
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    override fun onObjectRemoved(view: UserLocationView) {}

    //add user location icon
    override fun onObjectAdded(userLocationView: UserLocationView) {
        val pinIcon = userLocationView.pin.useCompositeIcon()

        //for moving
        val userMark = mapGetDriverPresenter.getBitmap(R.drawable.ic_user_mark)
        userLocationView.arrow.setIcon(ImageProvider.fromBitmap(userMark))

        userLocationLayer?.setAnchor(
            PointF(
                (mapView.width * 0.5).toFloat(),
                (mapView.height * 0.5).toFloat()
            ),
            PointF(
                (mapView.width * 0.5).toFloat(),
                (mapView.height * 0.83).toFloat()
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
        mapGetDriverPresenter.onObjectUpdated()
    }


    override fun setListeners() {}


    private fun setUserLocation() {
        val mapKit = MapKitFactory.getInstance()
        mapView.map.move(CameraPosition(Point(0.0, 0.0), 16f, 0f, 0f))

        //init user location service
        userLocationLayer = mapKit.createUserLocationLayer(mapView.mapWindow)
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


    override fun getMap(): MapView {
        return mapView
    }


    override fun getUserLocation(): UserLocationLayer? {
        return userLocationLayer
    }


    override fun onStop() {
        super.onStop()
        MapKitFactory.getInstance().onStop()
        mapView.onStop()
    }


    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        mapView.onStart()
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


    override fun getArgs(): Bundle? {
        return arguments
    }


    fun onBackPressed(): Boolean {
        return mapGetDriverPresenter.onBackPressed()
    }


    override fun onDestroy() {
        mapGetDriverPresenter.instance().detachView()
        super.onDestroy()
    }

}
