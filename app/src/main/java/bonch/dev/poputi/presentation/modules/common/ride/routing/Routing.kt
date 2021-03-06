package bonch.dev.poputi.presentation.modules.common.ride.routing

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import androidx.core.content.ContextCompat
import bonch.dev.poputi.App
import bonch.dev.poputi.R
import bonch.dev.poputi.domain.entities.common.ride.Coordinate
import bonch.dev.poputi.domain.utils.Constants
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

    private var drivingRouter: DrivingRouter? = null
    private var drivingSession: DrivingSession? = null
    private var isWaypoint = true

    companion object {
        private var mapView: MapView? = null
        private var boundingBox: BoundingBox? = null

        var mapObjects: MapObjectCollection? = null
        var mapObjectsDriver: MapObjectCollection? = null


        fun removeRoute() {
            mapObjects?.let {
                try {
                    mapView?.map?.mapObjects?.remove(it)
                    mapObjects = null
                } catch (ex: java.lang.Exception) {
                }
            }
        }


        fun removeRouteDriver() {
            mapObjectsDriver?.let {
                try {
                    mapView?.map?.mapObjects?.remove(it)
                    mapObjectsDriver = null
                } catch (ex: java.lang.Exception) {
                }
            }
        }
    }


    override fun onDrivingRoutesError(p0: Error) {}


    override fun onDrivingRoutes(routes: MutableList<DrivingRoute>) {
        //unsupported Yandex Map exception
        try {
            for (route in routes) {
                if (isWaypoint) {
                    //calc average price
                    var sec = 0.0
                    route.sections.forEach {
                        sec += it.metadata.weight.timeWithTraffic.value
                    }

                    Coordinate.averagePrice = ((sec / 60) * Constants.AVERAGE_PRICE_K).toInt()
                }

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
        mapView: MapView
    ) {
        if (drivingRouter == null)
            drivingRouter = DirectionsFactory.getInstance().createDrivingRouter()

        Routing.mapView = mapView
        isWaypoint = true

        if (mapObjects == null) {
            mapObjects = mapView.map?.mapObjects?.addCollection()
            boundingBox = BoundingBox(
                Point(startLocation.latitude, startLocation.longitude),
                Point(endLocation.latitude, endLocation.longitude)
            )

            route(startLocation, endLocation)
        }

        if (boundingBox != null) {
            enterAnimation()
        }
    }


    fun submitRequestDriver(
        startLocation: Point,
        endLocation: Point,
        mapView: MapView
    ) {
        if (drivingRouter == null)
            drivingRouter = DirectionsFactory.getInstance().createDrivingRouter()

        Routing.mapView = mapView
        isWaypoint = false

        if (mapObjectsDriver == null) {
            mapObjectsDriver = mapView.map?.mapObjects?.addCollection()

            route(startLocation, endLocation)
        }
    }


    fun submitRequestStory(
        startLocation: Point,
        endLocation: Point,
        mapView: MapView
    ) {
        if (drivingRouter == null)
            drivingRouter = DirectionsFactory.getInstance().createDrivingRouter()

        Routing.mapView = mapView
        isWaypoint = true

        if (mapObjects == null) {
            mapObjects = mapView.map?.mapObjects?.addCollection()
            boundingBox = BoundingBox(
                Point(startLocation.latitude, startLocation.longitude),
                Point(endLocation.latitude, endLocation.longitude)
            )

            route(startLocation, endLocation)

            boundingBox?.let {
                var cameraPosition = mapView.map?.cameraPosition(it)

                if (cameraPosition != null) {
                    cameraPosition = CameraPosition(
                        cameraPosition.target,
                        cameraPosition.zoom - 1.5f,
                        cameraPosition.azimuth,
                        cameraPosition.tilt
                    )

                    mapView.map?.move(
                        cameraPosition,
                        Animation(Animation.Type.SMOOTH, 0f),
                        null
                    )
                }
            }
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

                mapView?.map?.move(cameraPosition)

                cameraPosition = CameraPosition(
                    cameraPosition.target,
                    cameraPosition.zoom - 0.5f,
                    cameraPosition.azimuth,
                    cameraPosition.tilt
                )
                mapView?.map?.move(
                    cameraPosition,
                    Animation(Animation.Type.SMOOTH, 0.9f),
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