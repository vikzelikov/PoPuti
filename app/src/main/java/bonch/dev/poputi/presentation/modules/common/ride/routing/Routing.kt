package bonch.dev.poputi.presentation.modules.common.ride.routing

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import androidx.core.content.ContextCompat
import bonch.dev.poputi.App
import bonch.dev.poputi.R
import com.yandex.mapkit.Animation
import com.yandex.mapkit.RequestPoint
import com.yandex.mapkit.RequestPointType
import com.yandex.mapkit.directions.DirectionsFactory
import com.yandex.mapkit.directions.driving.DrivingOptions
import com.yandex.mapkit.directions.driving.DrivingRoute
import com.yandex.mapkit.directions.driving.DrivingRouter
import com.yandex.mapkit.directions.driving.DrivingSession
import com.yandex.mapkit.geometry.BoundingBox
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.Error
import com.yandex.runtime.image.ImageProvider
import java.util.*
import javax.inject.Inject

class Routing @Inject constructor() : DrivingSession.DrivingRouteListener {

    private var mapView: MapView? = null
    private var drivingRouter: DrivingRouter? = null
    private var drivingSession: DrivingSession? = null
    private var isWaypoint = true

    companion object {
        var mapObjects: MapObjectCollection? = null
        var mapObjectsDriver: MapObjectCollection? = null
        private var boundingBox: BoundingBox? = null
    }

    init {
        drivingRouter = DirectionsFactory.getInstance().createDrivingRouter()
    }


    override fun onDrivingRoutesError(p0: Error) {}


    override fun onDrivingRoutes(routes: MutableList<DrivingRoute>) {
        //unsupported Yandex Map exception
        try {
            for (route in routes) {
//                val t1 = PolylinePosition(0, 50.3)
//                val t2 = PolylinePosition(100, 50.3)
//                val x = Subpolyline(t1, t2)

//                mapObjects?.hide(route.geometry as? Subpolyline)
//                Log.e("TEST", "${mapObjects?.geometry?.points?.size}")
                val pol = mapObjects?.addPolyline(route.geometry)
//                Routing.mapObjects?.co
//                //set color for direction
                pol?.strokeColor = if (isWaypoint) {
                    Color.parseColor("#1152FD")
                } else {
                    Color.parseColor("#00C72C")
                }
            }

            if (isWaypoint) {
                //adding start and end placemarks
                val context = App.appComponent.getContext()
                val fromMark = context.getBitmapFromVectorDrawable(R.drawable.ic_input_marker_from)
                val toMark = context.getBitmapFromVectorDrawable(R.drawable.ic_input_marker_to)
                mapObjects?.addPlacemark(
                    routes.first().geometry.points.first(),
                    ImageProvider.fromBitmap(fromMark)
                )
                mapObjects?.addPlacemark(
                    routes.last().geometry.points.last(),
                    ImageProvider.fromBitmap(toMark)
                )
            }

        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }


    fun submitRequest(
        startLocation: Point,
        endLocation: Point,
        isWaypoint: Boolean,
        mapView: MapView
    ) {

        this.mapView = mapView
        this.isWaypoint = isWaypoint

        if (isWaypoint && mapObjects == null) {
            mapObjects = mapView.map?.mapObjects?.addCollection()
            boundingBox = BoundingBox(
                Point(startLocation.latitude, startLocation.longitude),
                Point(endLocation.latitude, endLocation.longitude)
            )

            route(startLocation, endLocation)

            enterAnimation()

        } else if (!isWaypoint && mapObjectsDriver == null) {
            mapObjectsDriver = mapView.map?.mapObjects?.addCollection()

            route(startLocation, endLocation)
        }

        if (boundingBox != null) {
            enterAnimation()
        }
    }


    private fun route(startLocation: Point, endLocation: Point) {
        val drivingOptions = DrivingOptions().apply {
            alternativeCount = 1
        }
        val requestPoints = ArrayList<RequestPoint>()

        requestPoints.add(
            RequestPoint(
                startLocation,
                RequestPointType.WAYPOINT,
                null
            )
        )

        requestPoints.add(
            RequestPoint(
                endLocation,
                RequestPointType.WAYPOINT,
                null
            )
        )

        drivingSession = drivingRouter?.requestRoutes(requestPoints, drivingOptions, this)
    }


    fun removeRoute() {
        mapObjects?.let {
            try {
                mapView?.map?.mapObjects?.remove(it)
                mapObjects = null
            } catch (ex: java.lang.Exception) {
            }
        }
    }


    private fun enterAnimation() {
        val box = boundingBox
        box?.let {
            var cameraPosition = mapView?.map?.cameraPosition(box)

            if (cameraPosition != null) {
                cameraPosition = CameraPosition(
                    cameraPosition.target,
                    cameraPosition.zoom - 0.5f,
                    cameraPosition.azimuth,
                    cameraPosition.tilt
                )

                mapView?.map?.move(
                    cameraPosition,
                    Animation(Animation.Type.SMOOTH, 0f),
                    null
                )

                cameraPosition = CameraPosition(
                    cameraPosition.target,
                    cameraPosition.zoom - 0.5f,
                    cameraPosition.azimuth,
                    cameraPosition.tilt
                )
                mapView?.map?.move(
                    cameraPosition,
                    Animation(Animation.Type.SMOOTH, 1.1f),
                    null
                )
            }
        }
    }


    fun showRoute() {
        val box = boundingBox
        box?.let {
            var cameraPosition = mapView?.map?.cameraPosition(box)

            if (cameraPosition != null) {
                cameraPosition = CameraPosition(
                    cameraPosition.target,
                    cameraPosition.zoom - 0.3f,
                    cameraPosition.azimuth,
                    cameraPosition.tilt
                )

                mapView?.map?.move(
                    cameraPosition,
                    Animation(Animation.Type.SMOOTH, 1f),
                    null
                )
            }
        }
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
}