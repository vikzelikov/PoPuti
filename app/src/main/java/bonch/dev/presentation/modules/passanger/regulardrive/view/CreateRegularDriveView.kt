package bonch.dev.presentation.modules.passanger.regulardrive.view

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
import bonch.dev.Permissions
import bonch.dev.R
import bonch.dev.domain.utils.Constants
import bonch.dev.domain.utils.Keyboard
import bonch.dev.presentation.modules.passanger.regulardrive.RegularDriveComponent
import bonch.dev.presentation.modules.passanger.regulardrive.presenter.ContractPresenter
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
import kotlinx.android.synthetic.main.regular_create_ride.*
import javax.inject.Inject

class CreateRegularDriveView : AppCompatActivity(), UserLocationObjectListener, CameraListener,
    ContractView.ICreateRegularDriveView {

    @Inject
    lateinit var createRegularDrivePresenter: ContractPresenter.ICreateRegularDrivePresenter

    private lateinit var mapView: MapView
    private var userLocationLayer: UserLocationLayer? = null
    private var handlerAnimation: Handler? = null
    private var selectDateBottomSheet: BottomSheetBehavior<*>? = null



    init {
        RegularDriveComponent.regularDriveComponent?.inject(this)

        createRegularDrivePresenter.instance().attachView(this)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //init map
        MapKitFactory.setApiKey(Constants.API_KEY)
        MapKitFactory.initialize(this)
        SearchFactory.initialize(this)
        DirectionsFactory.initialize(this)

        setContentView(R.layout.regular_create_ride)

        mapView = map

        mapView.map?.addCameraListener(this)
        mapView.map?.isRotateGesturesEnabled = false

        mapView.map?.apply {
            val alignment = Alignment(HorizontalAlignment.RIGHT, VerticalAlignment.TOP)
            logo.setAlignment(alignment)
        }

        setBottomSheet()

        setListeners()

        onPermission()

    }


    private fun getSelectBottomSheet(){
        selectDateBottomSheet?.state = BottomSheetBehavior.STATE_EXPANDED
    }


    private fun setBottomSheet() {
        selectDateBottomSheet = BottomSheetBehavior.from<View>(select_time_bottom_sheet)

        selectDateBottomSheet?.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                onSlideBottomSheet(slideOffset)
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                onStateChangedBottomSheet(newState)
            }
        })
    }


    private fun onSlideBottomSheet(slideOffset: Float) {
        if (slideOffset > 0 && slideOffset < 1) {
            on_view.alpha = slideOffset * 0.8f
            back_btn.alpha = 1 - slideOffset
            show_route.alpha = 1 - slideOffset
        }
    }


    private fun onStateChangedBottomSheet(newState: Int) {
        if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
            on_view.visibility = View.GONE
            main_info_layout.elevation = 30f
        } else {
            on_view.visibility = View.VISIBLE
            main_info_layout.elevation = 0f
        }
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


    override fun setListeners() {
        select_date.setOnClickListener {
            getSelectBottomSheet()
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
        val userMark = createRegularDrivePresenter.getBitmap(R.drawable.ic_user_mark)
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


    override fun onObjectUpdated(view: UserLocationView, event: ObjectEvent) {}


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


    fun showNotification(text: String) {
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
        createRegularDrivePresenter.instance().detachView()
        super.onDestroy()
    }


    override fun getNavHost(): NavController? {
        return null
    }


    override fun hideKeyboard() {
        Keyboard.hideKeyboard(this, create_regular_ride_container)
    }

}