package bonch.dev.presenter.passanger.getdriver

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import bonch.dev.R
import bonch.dev.model.passanger.getdriver.pojo.Coordinate.fromAdr
import bonch.dev.view.passanger.getdriver.DetailRideView
import com.yandex.mapkit.Animation
import com.yandex.mapkit.RequestPoint
import com.yandex.mapkit.RequestPointType
import com.yandex.mapkit.directions.DirectionsFactory
import com.yandex.mapkit.directions.driving.DrivingOptions
import com.yandex.mapkit.directions.driving.DrivingRoute
import com.yandex.mapkit.directions.driving.DrivingRouter
import com.yandex.mapkit.directions.driving.DrivingSession
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.Error
import com.yandex.runtime.image.ImageProvider
import java.lang.Exception
import java.util.*

class Routing(var context: Context, private var detailRideView: DetailRideView) :
    DrivingSession.DrivingRouteListener {

    private var mapView: MapView? = null
    private var screenCenter: Point? = null
    private var mapObjects: MapObjectCollection? = null
    private var drivingRouter: DrivingRouter? = null
    private var drivingSession: DrivingSession? = null


    init {
        mapView = detailRideView.createRideView.mapView
        DirectionsFactory.initialize(context)
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

        println(routes.size)

        detailRideView.getView().mapView!!.map.move(
            CameraPosition(screenCenter!!, 13f, 0f, 0f),
            Animation(Animation.Type.SMOOTH, 1f),
            null
        )
    }


    fun submitRequest(startLocation: Point, endLocation: Point) {
        val drivingOptions = DrivingOptions()
        val requestPoints = ArrayList<RequestPoint>()
        drivingOptions.alternativeCount = 1

        screenCenter = Point(
            (startLocation.latitude + endLocation.latitude) / 2,
            (startLocation.longitude + endLocation.longitude) / 2
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