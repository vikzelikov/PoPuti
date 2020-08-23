package bonch.dev.poputi.presentation.modules.passenger.regular.ride.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.graphics.Color
import android.graphics.PointF
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import bonch.dev.poputi.Permissions
import bonch.dev.poputi.R
import bonch.dev.domain.utils.Constants
import bonch.dev.poputi.domain.entities.common.ride.Coordinate
import bonch.dev.poputi.presentation.modules.passenger.regular.RegularDriveComponent
import bonch.dev.poputi.presentation.modules.passenger.regular.ride.presenter.ContractPresenter
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
import kotlinx.android.synthetic.main.map_create_regular_drive.*
import javax.inject.Inject

class MapCreateRegularRide : AppCompatActivity(), UserLocationObjectListener, CameraListener,
    ContractView.IMapCreateRegularDrive {

    @Inject
    lateinit var mapCreateDrivePresenter: ContractPresenter.IMapCreateRegDrivePresenter

    private lateinit var mapView: MapView
    private var userLocationLayer: UserLocationLayer? = null
    private var handlerAnimation: Handler? = null


    init {
        RegularDriveComponent.regularDriveComponent?.inject(this)

        mapCreateDrivePresenter.instance().attachView(this)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //init map
        MapKitFactory.setApiKey(Constants.API_KEY)
        MapKitFactory.initialize(this)
        SearchFactory.initialize(this)
        DirectionsFactory.initialize(this)

        setContentView(R.layout.map_create_regular_drive)

        mapView = map

        mapView.map?.addCameraListener(this)
        mapView.map?.isRotateGesturesEnabled = false

        mapView.map?.apply {
            val alignment = Alignment(HorizontalAlignment.RIGHT, VerticalAlignment.TOP)
            logo.setAlignment(alignment)
        }

        setListeners()

        onPermission()

        mapCreateDrivePresenter.attachCreateRegularDrive(supportFragmentManager)

        Coordinate.fromAdr = null
        Coordinate.toAdr = null
    }


    private fun onPermission() {
        if (Permissions.isAccess(Permissions.GEO_PERMISSION, this)) {
            setUserLocation()
        } else {
            Permissions.access(Permissions.GEO_PERMISSION_REQUEST, this)
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (Permissions.isAccess(Permissions.GEO_PERMISSION, this)) {
            setUserLocation()
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    override fun setListeners() {}


    override fun onCameraPositionChanged(
        p0: Map,
        p1: CameraPosition,
        p2: CameraUpdateSource,
        p3: Boolean
    ) {
        if (p3) {
            userLocationLayer?.resetAnchor()
        }

        if ((p2 == CameraUpdateSource.GESTURES && p3)) {
            mapCreateDrivePresenter.requestGeocoder(Point(p1.target.latitude, p1.target.longitude))
        }
    }


    override fun getUserLocation(): UserLocationLayer? {
        return userLocationLayer
    }


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


    override fun onObjectRemoved(view: UserLocationView) {}

    //add user location icon
    override fun onObjectAdded(userLocationView: UserLocationView) {
        val pinIcon = userLocationView.pin.useCompositeIcon()

        //for moving
        val userMark = mapCreateDrivePresenter.getBitmap(R.drawable.ic_user_mark)
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
        mapCreateDrivePresenter.onObjectUpdate()
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


    override fun showNotification(text: String) {
        val mainHandler = Handler(Looper.getMainLooper())
        val myRunnable = Runnable {
            kotlin.run {
                val view = general_notification

                view.text = text
                handlerAnimation?.removeCallbacksAndMessages(null)
                handlerAnimation = Handler()
                view.translationY = 0.0f
                view.alpha = 0.0f

                view.animate()
                    .setDuration(500L)
                    .translationY(100f)
                    .alpha(1.0f)
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator?) {
                            super.onAnimationEnd(animation)
                            handlerAnimation?.postDelayed({ hideNotifications() }, 2000)
                        }
                    })
            }
        }

        mainHandler.post(myRunnable)
    }

    override fun showLoading() {
        val mainHandler = Handler(Looper.getMainLooper())
        val myRunnable = Runnable {
            kotlin.run {
                on_view.alpha = 0.7f
                progress_bar_btn.visibility = View.VISIBLE
                on_view.visibility = View.VISIBLE
            }
        }

        mainHandler.post(myRunnable)
    }


    override fun hideLoading() {
        val mainHandler = Handler(Looper.getMainLooper())
        val myRunnable = Runnable {
            kotlin.run {
                progress_bar_btn.visibility = View.GONE
                on_view.alpha = 0.7f
                on_view.animate()
                    .alpha(0f)
                    .setDuration(500)
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator?) {
                            //go to the next screen
                            on_view.visibility = View.GONE
                        }
                    })
            }
        }

        mainHandler.post(myRunnable)
    }


    private fun hideNotifications() {
        val view = general_notification

        view.animate()
            .setDuration(500L)
            .translationY(-100f)
            .alpha(0.0f)
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


    override fun onDestroy() {
        mapCreateDrivePresenter.instance().detachView()
        super.onDestroy()
    }


    override fun getNavHost(): NavController? {
        return null
    }


    override fun hideKeyboard() {}


    override fun pressBack() {
        super.onBackPressed()
    }


    override fun onBackPressed() {
        mapCreateDrivePresenter.onBackPressed()
    }

}