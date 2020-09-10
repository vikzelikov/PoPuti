package bonch.dev.poputi.domain.utils

import android.animation.ValueAnimator
import android.view.animation.LinearInterpolator
import com.yandex.mapkit.geometry.Point
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin


object Constants {
    const val API_KEY_YANDEX = "6e2e73e8-4a73-42f5-9bf1-35259708af3c"

    const val API_KEY_PUSHER = "4f8625f09b5081d92386"


    //    const val BASE_URL = "https://93dd6c7be5c3.ngrok.io"
    const val BASE_URL = "https://17037868-review-dev-4jxwt5.server.bonch.dev"

    const val CAMERA = 0
    const val GALLERY = 1

    const val AVERAGE_PRICE_K = 9.5

    const val FEE = 0.05



    fun getListOfLocations(): ArrayList<Point> {
        val locationList = ArrayList<Point>()

        locationList.add(Point(59.904849, 30.451771))
        locationList.add(Point(59.90485, 30.451775))
        locationList.add(Point(59.905165, 30.452646))
        locationList.add(Point(59.905242, 30.452805))
        locationList.add(Point(59.905254, 30.452986))
        locationList.add(Point(59.905267, 30.453252))
        locationList.add(Point(59.904847, 30.453965))
        locationList.add(Point(59.903846, 30.455667))
        locationList.add(Point(59.903533, 30.456204))
        locationList.add(Point(59.903308, 30.45659))
        locationList.add(Point(59.903127, 30.456912))
        locationList.add(Point(59.903028, 30.457066))
        locationList.add(Point(59.902924, 30.457217))
        locationList.add(Point(59.902808, 30.457365))
        locationList.add(Point(59.902666, 30.457524))
        locationList.add(Point(59.902535, 30.457647))
        locationList.add(Point(59.902379, 30.457767))
        locationList.add(Point(59.902207, 30.457875))
        locationList.add(Point(59.90214, 30.457907))
        locationList.add(Point(59.901306, 30.458312))
        locationList.add(Point(59.901081, 30.458422))
        locationList.add(Point(59.900909, 30.458503))
        locationList.add(Point(59.900944, 30.45884))
        locationList.add(Point(59.90113, 30.458754))
        locationList.add(Point(59.901351, 30.458667))
        locationList.add(Point(59.901732, 30.458516))
        locationList.add(Point(59.90209, 30.458356))
        locationList.add(Point(59.902208, 30.458308))
        locationList.add(Point(59.902373, 30.45819))
        locationList.add(Point(59.902519, 30.458082))
        locationList.add(Point(59.902796, 30.457809))
        locationList.add(Point(59.902965, 30.457595))
        locationList.add(Point(59.903248, 30.45717))
        locationList.add(Point(59.903695, 30.456422))
        locationList.add(Point(59.904223, 30.455492))
        locationList.add(Point(59.904645, 30.454795))
        locationList.add(Point(59.904962, 30.454249))
        locationList.add(Point(59.905076, 30.454053))
        locationList.add(Point(59.905808, 30.452756))
        locationList.add(Point(59.9061, 30.452239))
        locationList.add(Point(59.906189, 30.452272))
        locationList.add(Point(59.906274, 30.452292))
        return locationList
    }


    fun getRotation(from: Point, to: Point): Float {
        val lat1: Double = from.latitude * Math.PI / 180
        val long1: Double = from.longitude * Math.PI / 180
        val lat2: Double = to.latitude * Math.PI / 180
        val long2: Double = to.longitude * Math.PI / 180

        val dLon = long2 - long1

        val y = sin(dLon) * cos(lat2)
        val x = cos(lat1) * sin(lat2) - (sin(lat1) * cos(lat2) * cos(dLon))

        var brng = atan2(y, x)

        brng = Math.toDegrees(brng)
        brng = (brng + 360) % 360

        return brng.toFloat()
    }
    fun carAnimator(): ValueAnimator {
        val valueAnimator = ValueAnimator.ofFloat(0f, 1f)
        valueAnimator.duration = 4000
        valueAnimator.interpolator = LinearInterpolator()
        return valueAnimator
    }
}
