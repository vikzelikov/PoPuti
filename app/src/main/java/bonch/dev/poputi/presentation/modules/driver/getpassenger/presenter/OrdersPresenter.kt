package bonch.dev.poputi.presentation.modules.driver.getpassenger.presenter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Handler
import android.os.Looper
import bonch.dev.poputi.App
import bonch.dev.poputi.R
import bonch.dev.poputi.domain.entities.common.ride.ActiveRide
import bonch.dev.poputi.domain.entities.common.ride.Ride
import bonch.dev.poputi.domain.entities.common.ride.RideInfo
import bonch.dev.poputi.domain.interactor.driver.getpassenger.IGetPassengerInteractor
import bonch.dev.poputi.domain.utils.Geo
import bonch.dev.poputi.presentation.base.BasePresenter
import bonch.dev.poputi.presentation.modules.driver.getpassenger.GetPassengerComponent
import bonch.dev.poputi.presentation.modules.driver.getpassenger.view.ContractView
import bonch.dev.poputi.presentation.modules.driver.getpassenger.view.MapOrderView
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationServices.getFusedLocationProviderClient
import com.google.gson.Gson
import com.yandex.mapkit.geometry.Point
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt


class OrdersPresenter : BasePresenter<ContractView.IOrdersView>(),
    ContractPresenter.IOrdersPresenter {

    @Inject
    lateinit var getPassengerInteractor: IGetPassengerInteractor

    private val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
    private val calendar = Calendar.getInstance()

    private var blockHandler: Handler? = null
    private var userPositionHandler: Handler? = null
    private var isBlock = false
    private var isGettingLocation = false

    private val UPDATE_INTERVAL = 10 * 1000.toLong()
    private val FASTEST_INTERVAL: Long = 2000

    private var mLocationRequest: LocationRequest? = null
    var userPosition: Point? = null
    var isUserGeoAccess = false

    init {
        GetPassengerComponent.getPassengerComponent?.inject(this)
    }


    override fun selectOrder(order: RideInfo) {
        if (!isBlock) {
            getView()?.getFragment()?.context?.let {

                Geo.showAlertEnable(it as Activity)

                Geo.isEnabled(it)?.let { enable ->
                    if (enable) {
                        //set active ride
                        ActiveRide.activeRide = order

                        val intent = Intent(it, MapOrderView::class.java)

                        //show detail order
                        getView()?.getFragment()?.startActivityForResult(intent, 1)
                    }
                }
            }

            isBlock = true
        }
    }


    override fun initOrders() {
        getView()?.showOrdersLoading()

        startLocationUpdates()

        Thread(Runnable {
            while (true) {
                val userPoint = userPosition
                if (userPoint != null || !isUserGeoAccess) {
                    val mainHandler = Handler(Looper.getMainLooper())
                    val myRunnable = Runnable {
                        kotlin.run {
                            getOrders()

                            subscribeOnGetOrders()
                        }
                    }
                    mainHandler.post(myRunnable)

                    break
                }
            }
        }).start()
    }


    override fun subscribeOnGetOrders() {
        getPassengerInteractor.connectSocketOrders { isSuccess ->
            if (isSuccess) {
                getPassengerInteractor.subscribeOnGetOrders { data, _ ->
                    if (data != null) {
                        val ride = Gson().fromJson(data, Ride::class.java)?.ride
                        if (ride != null) {
                            val mainHandler = Handler(Looper.getMainLooper())
                            val myRunnable = Runnable {
                                kotlin.run {
                                    ride.isNewOrder = true
                                    mergeOrders(arrayListOf(ride))
                                }
                            }

                            mainHandler.post(myRunnable)
                        }
                    }
                }

                getPassengerInteractor.subscribeOnDeleteOffer { data, _ ->
                    if (data != null) {
                        val ride = Gson().fromJson(data, Ride::class.java)?.ride
                        ride?.rideId?.let {
                            val mainHandler = Handler(Looper.getMainLooper())
                            val myRunnable = Runnable {
                                kotlin.run {
                                    getView()?.getAdapter()?.deleteOrder(it)
                                }
                            }

                            mainHandler.post(myRunnable)
                        }
                    }
                }
            }
        }
    }


    override fun getOrders() {
        //get new orders
        getPassengerInteractor.getNewOrder { orders, _ ->
            if (!orders.isNullOrEmpty()) {
                val list = arrayListOf<RideInfo>()
                orders.forEach { if (checkRide(it)) list.add(it) }

                mergeOrders(list)
            }
        }
    }


    private fun mergeOrders(orders: ArrayList<RideInfo>) {
        //calculate distance
        if (!orders.isNullOrEmpty() && !isGettingLocation) {

            isGettingLocation = true

            getView()?.getAdapter()?.list?.let { recyclerList ->
                val copyList = arrayListOf<RideInfo>()
                copyList.addAll(recyclerList)

                val userPoint = userPosition
                if (userPoint != null) {
                    //geo accessed
                    orders.forEachIndexed { i, order ->
                        val fromLat = order.fromLat
                        val fromLng = order.fromLng
                        if (fromLat != null && fromLng != null) {
                            val point = Point(fromLat, fromLng)
                            //save distance
                            val distance = calcDistance(userPoint, point)
                            order.distance = distance

                            //if last item, then show in view
                            if (i == orders.lastIndex) {
                                copyList.addAll(orders)
                                showOrder(copyList, recyclerList.isEmpty())
                            }
                        }
                    }
                } else {
                    //geo not accessed
                    showOrder(orders, recyclerList.isEmpty())
                }

                isGettingLocation = false

            }
        }
    }


    private fun showOrder(orders: ArrayList<RideInfo>, isFirst: Boolean) {
        var delay = 250L
        orders.sortBy { it.distance }

        //first showing
        if (isFirst) {

            getView()?.showRecycler()
            getView()?.getAdapter()?.setNewOrders(orders)

        } else {
            orders.forEachIndexed { i, order ->
                if (order.isNewOrder) {
                    //+1 so indexing in RecyclerView start from 1
                    //add delay for animation showing
                    Handler().postDelayed({
                        getView()?.getAdapter()?.setNewOrder(i, order)
                    }, delay)

                    delay += 250
                }
            }
        }
    }


    override fun startProcessBlock() {
        if (blockHandler == null) {
            blockHandler = Handler()
        }

        if (userPositionHandler == null) {
            userPositionHandler = Handler()
        }

        blockHandler?.postDelayed(object : Runnable {
            override fun run() {
                isBlock = false
                blockHandler?.postDelayed(this, 3500)
            }
        }, 0)

        //every minute update geo of driver
        userPositionHandler?.postDelayed(object : Runnable {
            override fun run() {
                //userPosition = null
                userPositionHandler?.postDelayed(this, 1000 * 60)
            }
        }, 0)
    }

    //we already checked geo permission
    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        mLocationRequest = LocationRequest()
        mLocationRequest?.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest?.interval = UPDATE_INTERVAL
        mLocationRequest?.fastestInterval = FASTEST_INTERVAL

        val builder = LocationSettingsRequest.Builder()

        mLocationRequest?.let {
            builder.addLocationRequest(it)
            val locationSettingsRequest = builder.build()

            val settingsClient = LocationServices.getSettingsClient(App.appComponent.getContext())
            settingsClient.checkLocationSettings(locationSettingsRequest)

            getFusedLocationProviderClient(App.appComponent.getContext()).requestLocationUpdates(
                mLocationRequest, object : LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult) {
                        val location = locationResult.lastLocation
                        val lat = location?.latitude
                        val lng = location?.longitude

                        if (lat != null && lng != null) {
                            userPosition = Point(lat, lng)
                        }
                    }
                },
                Looper.myLooper()
            )
        }
    }


    override fun calcDistance(from: Point, to: Point): Int {
        val radiusEarth = 6371e3

        val fromRad = Math.toRadians(from.latitude)
        val toRad = Math.toRadians(to.latitude)
        val dx = Math.toRadians(to.latitude - from.latitude)
        val dy = Math.toRadians(to.longitude - from.longitude)

        val atan = sin(dx / 2) * sin(dx / 2) + cos(fromRad) * cos(toRad) * sin(dy / 2) * sin(dy / 2)
        val c = 2 * atan2(sqrt(atan), sqrt(1 - atan))
        return (radiusEarth * c).toInt()
    }


    //checking the actual of the trip
    private fun checkRide(rideInfo: RideInfo): Boolean {
        var result = true

        val createdAt = rideInfo.createdAt

        if (createdAt != null) {
            try {
                val parseDate = format.parse(createdAt)
                parseDate?.let {
                    val time = parseDate.time
                    if (calendar.time.time - time > 300 * 1000) {

                        rideInfo.rideId?.let {
                            getPassengerInteractor.cancelRide(
                                App.appComponent.getApp().getString(R.string.mistake_order), it
                            )
                        }

                        result = false
                    }
                }
            } catch (ex: ParseException) {
            }
        }

        return result
    }


    override fun stopSearchOrders() {
        blockHandler?.removeCallbacksAndMessages(null)
        userPositionHandler?.removeCallbacksAndMessages(null)
        blockHandler = null
        userPositionHandler = null
    }


    override fun instance(): OrdersPresenter {
        return this
    }

}