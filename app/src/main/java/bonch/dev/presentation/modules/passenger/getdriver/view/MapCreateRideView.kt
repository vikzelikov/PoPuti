package bonch.dev.presentation.modules.passenger.getdriver.view


import android.graphics.Color
import android.graphics.PointF
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import bonch.dev.App
import bonch.dev.MainActivity
import bonch.dev.Permissions
import bonch.dev.R
import bonch.dev.di.component.passenger.DaggerGetDriverComponent
import bonch.dev.di.module.passenger.GetDriverModule
import bonch.dev.domain.entities.common.ride.Coordinate.fromAdr
import bonch.dev.domain.utils.Constants.API_KEY
import bonch.dev.domain.utils.Keyboard
import bonch.dev.presentation.modules.passenger.getdriver.GetDriverComponent
import bonch.dev.presentation.modules.passenger.getdriver.presenter.ContractPresenter
import com.google.android.material.bottomnavigation.BottomNavigationView
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
import kotlinx.android.synthetic.main.map_create_ride_fragment.*
import javax.inject.Inject


class MapCreateRideView : Fragment(), UserLocationObjectListener, CameraListener,
    ContractView.IMapCreateRideView {

    @Inject
    lateinit var mapPresenter: ContractPresenter.IMapCreateRidePresenter

    private lateinit var mapView: MapView
    var bottomNavView: BottomNavigationView? = null
    private var userLocationLayer: UserLocationLayer? = null

    init {
        initDI()

        GetDriverComponent.getDriverComponent?.inject(this)

        mapPresenter.instance().attachView(this)
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
        MapKitFactory.setApiKey(API_KEY)
        MapKitFactory.initialize(context)
        SearchFactory.initialize(context)
        DirectionsFactory.initialize(context)

        val root = inflater.inflate(R.layout.map_create_ride_fragment, container, false)
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

            mapPresenter.isUserCoordinate(arguments)
        }

        super.onViewCreated(view, savedInstanceState)
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        val activity = activity as? MainActivity

        activity?.let {
            if (Permissions.isAccess(Permissions.GEO_PERMISSION, activity)) {
                setUserLocation()
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    //center screen position
    override fun onCameraPositionChanged(
        p0: Map,
        p1: CameraPosition,
        p2: CameraUpdateSource,
        p3: Boolean
    ) {
        if (p3) {
            userLocationLayer?.resetAnchor()
        }

        if ((p2 == CameraUpdateSource.GESTURES && p3) || fromAdr == null) {
            mapPresenter.requestGeocoder(Point(p1.target.latitude, p1.target.longitude))
        }
    }


    override fun onObjectRemoved(view: UserLocationView) {}

    //add user location icon
    override fun onObjectAdded(userLocationView: UserLocationView) {
        val pinIcon = userLocationView.pin.useCompositeIcon()

        //for moving
        val userMark = mapPresenter.getBitmap(R.drawable.ic_user_mark)
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
        mapPresenter.onObjectUpdate()
    }


    private fun setUserLocation() {
        val mapKit = MapKitFactory.getInstance()
        //set correct zoom
        mapView.map?.move(CameraPosition(Point(0.0, 0.0), 16f, 0f, 0f))

        //init user location service
        userLocationLayer = mapKit.createUserLocationLayer(mapView.mapWindow)
        userLocationLayer?.let {
            it.isHeadingEnabled = false
            it.isVisible = true
            it.setObjectListener(this)
        }
    }


    override fun getUserLocation(): UserLocationLayer? {
        return userLocationLayer
    }


    override fun correctMapView() {
        val layoutParams: RelativeLayout.LayoutParams = RelativeLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        map.layoutParams = layoutParams
    }


    override fun setListeners() {}


    override fun getMap(): MapView {
        return mapView
    }


    override fun moveCamera(point: Point) {
        mapView.map?.move(
            CameraPosition(point, 35.0f, 0.0f, 0.0f),
            Animation(Animation.Type.SMOOTH, 1f),
            null
        )
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
            Keyboard.hideKeyboard(activity, view)
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


    override fun getFM(): FragmentManager? {
        return (activity as? MainActivity)?.supportFragmentManager
    }


    override fun getNavView(): BottomNavigationView? {
        return bottomNavView
    }


    override fun onDestroy() {
        mapPresenter.instance().detachView()
        super.onDestroy()
    }


    override fun getNavHost(): NavController? {
        return (activity as? MainActivity)?.navController
    }
}