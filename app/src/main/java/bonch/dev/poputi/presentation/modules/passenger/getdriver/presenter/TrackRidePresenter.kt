package bonch.dev.poputi.presentation.modules.passenger.getdriver.presenter

import android.animation.ValueAnimator
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.animation.LinearInterpolator
import androidx.fragment.app.Fragment
import bonch.dev.poputi.App
import bonch.dev.poputi.R
import bonch.dev.poputi.domain.entities.common.chat.MessageObject
import bonch.dev.poputi.domain.entities.common.ride.*
import bonch.dev.poputi.domain.entities.passenger.getdriver.ReasonCancel
import bonch.dev.poputi.domain.interactor.passenger.getdriver.IGetDriverInteractor
import bonch.dev.poputi.presentation.base.BasePresenter
import bonch.dev.poputi.presentation.modules.common.chat.view.ChatView
import bonch.dev.poputi.presentation.modules.common.ride.routing.Routing
import bonch.dev.poputi.presentation.modules.passenger.getdriver.GetDriverComponent
import bonch.dev.poputi.presentation.modules.passenger.getdriver.view.ContractView
import bonch.dev.poputi.service.passenger.PassengerRideService
import com.google.gson.Gson
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.PlacemarkMapObject
import javax.inject.Inject
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

class TrackRidePresenter : BasePresenter<ContractView.ITrackRideView>(),
    ContractPresenter.ITrackRidePresenter {

    @Inject
    lateinit var getDriverInteractor: IGetDriverInteractor

    @Inject
    lateinit var routing: Routing

    val CHAT_REQUEST = 9

    private var driverIcon: PlacemarkMapObject? = null
    private var driverIconHandler: Handler? = null

    private var previousPoint: Point? = null
    private var currentPoint: Point? = null
    private var isAllowRotate = true


    init {
        GetDriverComponent.getDriverComponent?.inject(this)
    }


    private val changeRideReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            onChangeRide(intent)
        }
    }


    private val chatReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            onChat(intent)
        }
    }


    private val driverLocationReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            onDriverLocation(intent)
        }
    }


    override fun registerReceivers() {
        val app = App.appComponent.getApp()

        //check regestered receivers before
        app.registerReceiver(
            changeRideReceiver,
            IntentFilter(PassengerRideService.CHANGE_RIDE_TAG)
        )

        app.registerReceiver(
            chatReceiver,
            IntentFilter(PassengerRideService.CHAT_TAG)
        )

        app.registerReceiver(
            driverLocationReceiver,
            IntentFilter(PassengerRideService.DRIVER_GEO_TAG)
        )
    }


    private fun onChangeRide(intent: Intent?) {
        val res = App.appComponent.getContext().resources

        val data = intent?.getStringExtra(PassengerRideService.CHANGE_RIDE_TAG)

        if (data == null) {
            getView()?.showNotification(res.getString(R.string.errorSystem))
        } else {
            val ride = Gson().fromJson(data, Ride::class.java)?.ride
            if (ride == null) {
                getView()?.showNotification(res.getString(R.string.errorSystem))
            } else {
                ride.statusId?.let { idStep ->
                    nextStep(idStep)
                }
            }
        }
    }


    override fun nextStep(idStep: Int) {
        val step = getByValue(idStep)
        val res = App.appComponent.getContext().resources

        if (step != null) {
            val mainHandler = Handler(Looper.getMainLooper())
            val myRunnable = Runnable {
                kotlin.run {
                    getView()?.checkoutStatusView(step)
                }
            }

            mainHandler.post(myRunnable)

        } else getView()?.showNotification(res.getString(R.string.errorSystem))
    }


    override fun getByValue(status: Int?) = StatusRide.values().firstOrNull { it.status == status }


    override fun getTaxMoney(): Int {
        //TODO рассчитать штраф
        return 100
    }


    override fun cancelDone(reasonID: ReasonCancel, textReason: String) {
        clearData()

        //send cancel reason
        ActiveRide.activeRide?.rideId?.let {
            getDriverInteractor.cancelRide(textReason, it)
        }

        if (reasonID == ReasonCancel.MISTAKE_ORDER || reasonID == ReasonCancel.OTHER_REASON) {
            Coordinate.toAdr = null
        }

        backFragment(reasonID)
    }


    override fun backFragment(reasonID: ReasonCancel) {
        ActiveRide.activeRide?.let { restoreRide(it) }

        ActiveRide.activeRide = null
        getDriverInteractor.removeRideId()

        val res = App.appComponent.getContext().resources
        getView()?.showNotification(res.getString(R.string.rideCancel))

        getView()?.onCancelRide(reasonID)
    }


    override fun cancelDoneOtherReason(comment: String?) {
        val res = App.appComponent.getContext().resources

        if (comment?.trim().isNullOrEmpty()) {
            getView()?.showNotification(res.getString(R.string.writeYourProblemComment))
        } else {
            val textReason = "OTHER_REASON: ".plus(comment)

            getView()?.hideKeyboard()

            cancelDone(ReasonCancel.OTHER_REASON, textReason)
        }
    }


    override fun clearData() {
        val app = App.appComponent

        stopDriverIconHandler()

        app.getApp().stopService(Intent(app.getContext(), PassengerRideService::class.java))
        getDriverInteractor.disconnectSocket()
        getDriverInteractor.removeRideId()
    }


    private fun onChat(intent: Intent?) {
        val data = intent?.getStringExtra(PassengerRideService.CHAT_TAG)

        if (data != null) {
            val message = Gson().fromJson(data, MessageObject::class.java)?.message
            if (message != null) {
                getView()?.checkoutIconChat(true)
            }
        }
    }


    private fun onDriverLocation(intent: Intent?) {
        val data = intent?.getStringExtra(PassengerRideService.DRIVER_GEO_TAG)

        if (data != null) {
            Log.e("TEST", data)
            val location = Gson().fromJson(data, Location::class.java)?.location
            if (location != null) {
                val point = Point(location.latitude, location.longitude)
                updateDriverLocation(point)
            }
        }
    }


    private fun updateDriverLocation(point: Point) {
        if (driverIcon == null) {
            startRotateDriverHandler()

            driverIcon = getView()?.addDriverIconF(point)
        }

        if (previousPoint == null) {
            currentPoint = point
            previousPoint = currentPoint
            currentPoint?.let { driverIcon?.geometry = it }

        } else {
            previousPoint = currentPoint
            currentPoint = point
            val valueAnimator = carAnimator()
            valueAnimator.addUpdateListener { va ->
                if (currentPoint != null && previousPoint != null) {
                    val multiplier = va.animatedFraction

                    val curLat = currentPoint?.latitude
                    val prevLat = previousPoint?.latitude

                    val curLon = currentPoint?.longitude
                    val prevLon = previousPoint?.longitude

                    if (curLat != null && prevLat != null && curLon != null && prevLon != null) {
                        val nextLocation = Point(
                            multiplier * curLat + (1 - multiplier) * prevLat,
                            multiplier * curLon + (1 - multiplier) * prevLon
                        )

                        driverIcon?.geometry = nextLocation

                        previousPoint?.let { prevPoint ->
                            val rotation = getRotation(prevPoint, nextLocation)

                            if (!rotation.isNaN() && isAllowRotate) {
                                driverIcon?.direction = rotation

                                isAllowRotate = false
                            }
                        }
                    }
                }
            }

            valueAnimator.start()

        }
    }


    private fun getRotation(from: Point, to: Point): Float {
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


    private fun carAnimator(): ValueAnimator {
        val valueAnimator = ValueAnimator.ofFloat(0f, 1f)
        valueAnimator.duration = 3000
        valueAnimator.interpolator = LinearInterpolator()
        return valueAnimator
    }


    override fun showChat(context: Context, fragment: Fragment) {
        val intent = Intent(context, ChatView::class.java)
        fragment.startActivityForResult(intent, CHAT_REQUEST)

        Handler().postDelayed({
            getView()?.checkoutIconChat(false)
        }, 1000)
    }


    private fun restoreRide(ride: RideInfo) {
        val fromLat = ride.fromLat
        val fromLng = ride.fromLng
        val toLat = ride.toLat
        val toLng = ride.toLng

        val fromPoint = if (fromLat != null && fromLng != null) AddressPoint(fromLat, fromLng)
        else null

        val toPoint = if (toLat != null && toLng != null) AddressPoint(toLat, toLng)
        else null

        if (fromPoint != null && toPoint != null) {
            val fromAdr = Address()
            fromAdr.point = fromPoint
            fromAdr.address = ride.position
            Coordinate.fromAdr = fromAdr

            val toAdr = Address()
            toAdr.point = toPoint
            toAdr.address = ride.destination
            Coordinate.toAdr = toAdr
        }
    }


    override fun route() {
        val fromLat = ActiveRide.activeRide?.fromLat
        val fromLng = ActiveRide.activeRide?.fromLng
        val toLat = ActiveRide.activeRide?.toLat
        val toLng = ActiveRide.activeRide?.toLng
        val map = getView()?.getMap()

        if (fromLat != null && fromLng != null && toLat != null && toLng != null && map != null) {
            val from = Point(fromLat, fromLng)
            val to = Point(toLat, toLng)

            routing.submitRequest(from, to, true, map)
        }
    }


    private fun startRotateDriverHandler() {
        if (driverIconHandler == null) {
            driverIconHandler = Handler()

            driverIconHandler?.postDelayed(object : Runnable {
                override fun run() {
                    isAllowRotate = true
                    driverIconHandler?.postDelayed(this, 500)
                }
            }, 0)
        }
    }


    private fun stopDriverIconHandler() {
        driverIconHandler?.removeCallbacksAndMessages(null)
        driverIcon = null
    }


    override fun showRoute() {
        routing.showRoute()
    }


    override fun removeRoute() {
        routing.removeRoute()
    }


    override fun instance() = this

}
