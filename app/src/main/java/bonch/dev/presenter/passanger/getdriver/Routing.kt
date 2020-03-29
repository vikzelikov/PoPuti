package bonch.dev.presenter.passanger.getdriver

import android.content.Context
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
import java.util.*

class Routing(var context: Context, var detailRideView: DetailRideView) :
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
        for (route in routes) {
            mapObjects?.addPolyline(route.geometry)
        }

        detailRideView.createRideView.mapView!!.map.move(
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
}