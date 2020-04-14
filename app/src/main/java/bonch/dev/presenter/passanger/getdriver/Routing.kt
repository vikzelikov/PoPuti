package bonch.dev.presenter.passanger.getdriver

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.core.content.ContextCompat
import bonch.dev.R
import bonch.dev.view.passanger.getdriver.DetailRideView
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

class Routing(var context: Context, private var detailRideView: DetailRideView) :
    DrivingSession.DrivingRouteListener {

    private var mapView: MapView? = null
    private var mapObjects: MapObjectCollection? = null
    private var drivingRouter: DrivingRouter? = null
    private var drivingSession: DrivingSession? = null
    private var boundingBox: BoundingBox? = null


    init {
        mapView = detailRideView.createRideView.mapView
        drivingRouter = DirectionsFactory.getInstance().createDrivingRouter()
        mapObjects = mapView!!.map.mapObjects.addCollection()
    }


    override fun onDrivingRoutesError(p0: Error) {}


    override fun onDrivingRoutes(routes: MutableList<DrivingRoute>) {
        //unsupported Yandex Map exception
        try {
            for (route in routes) {
                mapObjects?.addPolyline(route.geometry)
            }

            //adding start and end placemarks
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
        } catch (ex: Exception) {
        }


        //move camera to show the route
        showRoute()
    }


    fun submitRequest(startLocation: Point, endLocation: Point) {
        val drivingOptions = DrivingOptions().apply {
            alternativeCount = 1
        }
        val requestPoints = ArrayList<RequestPoint>()

        //get boundingBox around two point
        boundingBox = BoundingBox(
            Point(startLocation.latitude, startLocation.longitude),
            Point(endLocation.latitude, endLocation.longitude)
        )

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

        drivingSession = drivingRouter!!.requestRoutes(requestPoints, drivingOptions, this)
    }


    fun removeRoute() {
        if (mapView != null) {
            mapView!!.map.mapObjects.remove(mapObjects!!)
        }
    }


    fun showRoute(){
        var cameraPosition = mapView!!.map.cameraPosition(boundingBox!!)

        cameraPosition = CameraPosition(
            cameraPosition.target,
            cameraPosition.zoom - 0.3f,
            cameraPosition.azimuth,
            cameraPosition.tilt
        )

        mapView!!.map.move(
            cameraPosition,
            Animation(Animation.Type.SMOOTH, 1f),
            null
        )
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