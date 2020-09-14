package bonch.dev.poputi.presentation.modules.passenger.getdriver.view


import android.graphics.Color
import android.graphics.PointF
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import bonch.dev.poputi.App
import bonch.dev.poputi.MainActivity
import bonch.dev.poputi.Permissions
import bonch.dev.poputi.R
import bonch.dev.poputi.di.component.passenger.DaggerGetDriverComponent
import bonch.dev.poputi.di.module.passenger.GetDriverModule
import bonch.dev.poputi.domain.entities.common.ride.ActiveRide
import bonch.dev.poputi.domain.entities.common.ride.Address
import bonch.dev.poputi.domain.utils.Constants.API_KEY_YANDEX
import bonch.dev.poputi.domain.utils.Geo
import bonch.dev.poputi.domain.utils.Keyboard
import bonch.dev.poputi.presentation.interfaces.ParentEmptyHandler
import bonch.dev.poputi.presentation.interfaces.ParentHandler
import bonch.dev.poputi.presentation.modules.passenger.getdriver.GetDriverComponent
import bonch.dev.poputi.presentation.modules.passenger.getdriver.presenter.ContractPresenter
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
    lateinit var nextFragment: ParentEmptyHandler
    lateinit var navigateUser: ParentEmptyHandler
    lateinit var onboarding: ParentEmptyHandler
    private var userLocationLayer: UserLocationLayer? = null

    var myCityCallbackMain: ParentHandler<Address>? = null
    private var myCityCallback: ParentHandler<Address>? = null
    private var myCityCallbackDetect: ParentHandler<Address>? = null

    private var userIcon: PlacemarkMapObject? = null
    private var driverIcon: PlacemarkMapObject? = null
    private var mapObjects: MapObjectCollection? = null

    private var isAllowFirstZoom = false
    private var isAllowZoom = false
    private var isDetectCity = true


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
        MapKitFactory.setApiKey(API_KEY_YANDEX)
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

        (activity as? MainActivity)?.let {
            Geo.showAlertEnable(it)
        }

        return root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val activity = activity as? MainActivity
        //access geo permission
        activity?.let {
            activity.applicationContext?.let {
                val isEnabled = Geo.isEnabled(it)
                if (isEnabled == null || !isEnabled) {
                    Geo.isPreferCityGeo = true
                    getUserLocation()?.let { point ->
                        map?.post {
                            Handler().postDelayed({
                                mapView.map?.move(CameraPosition(point, 16f, 1f, 1f))
                            }, 1000)
                        }
                    }
                }
            }

            Permissions.access(Permissions.GEO_PERMISSION_REQUEST, this)

            navigateUser()
        }

        Geo.isEnabled(App.appComponent.getContext())?.let {
            if (!it) {
                Geo.selectedCity?.point?.let { point ->
                    moveCamera(Point(point.latitude, point.longitude))

                    Geo.isPreferCityGeo = true
                }
            }
        }

        super.onViewCreated(view, null)
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

            map?.post {
                Handler().postDelayed({
                    isAllowFirstZoom = true

                    onboarding()
                }, 2000)
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
        if (p2 == CameraUpdateSource.GESTURES && p3) {
            isAllowZoom = true
            mapPresenter.requestGeocoder(p1, false)

        } else if (p2 == CameraUpdateSource.GESTURES) {
            isAllowZoom = false
            mapPresenter.requestGeocoder(p1, true)
        }


        if (Geo.isRequestUpdateCity || p3) {

            if (p3) mapPresenter.requestGeocoder(p1, false)

            //update city
            myCityCallback = { city ->
                myCityCallbackMain?.let { it(city) }
                myCityCallbackDetect?.let { it(city) }

                if (Geo.isRequestUpdateCity)
                    mapPresenter.saveMyCity(city)

                Geo.isRequestUpdateCity = false

                myCityCallback = null
            }
        }

        mapPresenter.onObjectUpdate()

    }


    override fun onObjectRemoved(view: UserLocationView) {}

    //add user location icon
    override fun onObjectAdded(userLocationView: UserLocationView) {
        val pinIcon = userLocationView.pin.useCompositeIcon()

        //for moving
        val userMark = mapPresenter.getBitmap(R.drawable.ic_user_mark)
        userLocationView.arrow.setIcon(ImageProvider.fromBitmap(userMark))

        //saving for handle
        userIcon = userLocationView.arrow

        ActiveRide.activeRide?.let {
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
        }

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
        if (isDetectCity) {
            getUserLocation()?.let {
                mapPresenter.requestGeocoder(CameraPosition(it, 16f, 1f, 1f), false)
            }

            myCityCallbackDetect = { city ->
                if (city.address != Geo.selectedCity?.address) {
                    Geo.selectedCity?.point?.let {
                        Geo.isPreferCityGeo = true
                    }
                }

                getUserLocation()?.let {
                    mapView.map?.move(CameraPosition(it, 16f, 1f, 1f))

                    Handler().postDelayed({
                        mapPresenter.requestGeocoder(
                            CameraPosition(it, 16f, 1f, 1f),
                            false
                        )
                    }, 1000)
                }

                myCityCallbackDetect = null
            }

            isDetectCity = false
        }
    }


    private fun setUserLocation() {
        val mapKit = MapKitFactory.getInstance()
        //set correct zoom
        mapView.map?.move(CameraPosition(Point(), 16f, 0f, 0f))

        //init user location service
        userLocationLayer = mapKit.createUserLocationLayer(mapView.mapWindow)
        userLocationLayer?.let {
            it.isHeadingEnabled = false
            it.isVisible = true
            it.setObjectListener(this)
        }
    }


    override fun addDriverIcon(point: Point): PlacemarkMapObject? {
        if (mapObjects == null) {
            val mark = mapPresenter.getBitmap(R.drawable.ic_car)
            mapObjects = mapView.map.mapObjects.addCollection()
            driverIcon = mapObjects?.addPlacemark(point)
            driverIcon?.setIcon(ImageProvider.fromBitmap(mark))
            driverIcon?.setIconStyle(IconStyle().setRotationType(RotationType.ROTATE))
        }

        return driverIcon
    }


    override fun removeDriverIcon() {
        mapObjects?.let {
            try {
                mapView.map?.mapObjects?.remove(it)
                mapObjects = null
            } catch (ex: java.lang.Exception) {
            }
        }
    }


    override fun getUserLocation(): Point? {
        return if (Geo.isPreferCityGeo || userLocationLayer?.cameraPosition()?.target == null) {
            val p = Geo.selectedCity?.point

            if (p != null) Point(p.latitude, p.longitude)
            else null

        } else userLocationLayer?.cameraPosition()?.target
    }


    override fun correctMapView() {
        val layoutParams: RelativeLayout.LayoutParams = RelativeLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        map.layoutParams = layoutParams
    }


    override fun setListeners() {}


    override fun getMap() = mapView


    override fun zoomMap(cameraPosition: CameraPosition) {
        val point = cameraPosition.target
        val zoom = cameraPosition.zoom + 0.05

        if (isAllowFirstZoom && isAllowZoom) {
            map?.post {
                mapView.map?.move(
                    CameraPosition(
                        point,
                        zoom.toFloat(),
                        cameraPosition.azimuth,
                        cameraPosition.tilt
                    ),
                    Animation(Animation.Type.SMOOTH, 0.3f),
                    null
                )
            }

            isAllowZoom = false
        }
    }


    override fun zoomMapDistance(cameraPosition: CameraPosition) {
        val point = cameraPosition.target
        val zoom = cameraPosition.zoom - 0.5f

        mapView.map?.move(
            CameraPosition(point, zoom, cameraPosition.azimuth, cameraPosition.tilt),
            Animation(Animation.Type.SMOOTH, 0.9f),
            null
        )
    }


    override fun moveCamera(point: Point) {
        mapView.map?.move(
            CameraPosition(point, 17.0f, 0.0f, 0.0f),
            Animation(Animation.Type.SMOOTH, 1f),
            null
        )
    }


    override fun attachCreateRide() {
        (activity as? MainActivity)?.supportFragmentManager?.let {
            mapPresenter.instance().attachView(this)

            mapPresenter.attachCreateRide(it)

            frame_container?.visibility = View.VISIBLE

            fadeMap()
        }
    }


    override fun attachDetailRide() {
        isAllowZoom = false

        nextFragment()

        fadeMap()

        frame_container?.visibility = View.GONE
    }


    override fun fadeMap() {
        on_map_view?.alpha = 1.0f
        on_map_view?.animate()?.alpha(0f)?.duration = 800
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


    override fun showUserIcon() {
        userIcon?.opacity = 1f
    }


    override fun hideUserIcon() {
        userIcon?.opacity = 0f
    }


    override fun onDestroy() {
        mapPresenter.instance().detachView()
        super.onDestroy()
    }


    override fun getMyCityCall(): ParentHandler<Address>? = myCityCallback


    override fun getNavHost(): NavController? {
        return (activity as? MainActivity)?.navController
    }
}