package bonch.dev.presentation.modules.driver.getpassenger.presenter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import bonch.dev.App
import bonch.dev.Permissions
import bonch.dev.domain.entities.common.ride.RideInfo
import bonch.dev.domain.entities.driver.getpassenger.SelectOrder
import bonch.dev.domain.interactor.driver.getpassenger.IGetPassengerInteractor
import bonch.dev.presentation.base.BasePresenter
import bonch.dev.presentation.interfaces.ParentHandler
import bonch.dev.presentation.modules.driver.getpassenger.GetPassengerComponent
import bonch.dev.presentation.modules.driver.getpassenger.view.ContractView
import bonch.dev.presentation.modules.driver.getpassenger.view.MapOrderView
import com.yandex.mapkit.geometry.Point
import javax.inject.Inject
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt


class OrdersPresenter : BasePresenter<ContractView.IOrdersView>(),
    ContractPresenter.IOrdersPresenter {

    @Inject
    lateinit var getPassengerInteractor: IGetPassengerInteractor

    private var blockHandler: Handler? = null
    private var userPositionHandler: Handler? = null
    private var mainHandler: Handler? = null
    private var isBlock = false
    private var isGettingLocation = false

    private var userPosition: Point? = null

    var isUserGeoAccess = false

    init {
        GetPassengerComponent.getPassengerComponent?.inject(this)
    }


    override fun onClickItem(order: RideInfo) {
        if (!isBlock) {
            getView()?.getFragment()?.context?.let {
                SelectOrder.order = order

                //show detail order
                val intent = Intent(it, MapOrderView::class.java)
                getView()?.getFragment()?.startActivityForResult(intent, 1)
                startSearchOrders()
            }

            isBlock = true
        }
    }


    override fun startSearchOrders() {
        //send request every 15 sec
        mainHandler = Handler()
        mainHandler?.postDelayed(object : Runnable {
            override fun run() {
                getPassengerInteractor.getNewOrder { orders, _ ->
                    //get new orders
                    val newOrders = arrayListOf<RideInfo>()
                    newOrders.addAll(orders)

                    //get old orders
                    val oldOrders = getView()?.getAdapter()?.list

                    if (!oldOrders.isNullOrEmpty()) {
                        val oldIter = oldOrders.listIterator()

                        //remove duplicates
                        while (oldIter.hasNext()) {
                            val oldOrder = oldIter.next()

                            if (newOrders.contains(oldOrder)) {
                                val newIter = newOrders.listIterator()

                                while (newIter.hasNext()) {
                                    val newOrder = newIter.next()
                                    newOrder.isNewOrder = true

                                    if (oldOrder == newOrder) {
                                        newIter.remove()
                                    }
                                }
                            } else {
                                //delete orders if it is too old
                                //-1 so RecyclerView indexing another
                                getView()?.getAdapter()?.notifyItemRemoved(oldIter.nextIndex() - 1)
                                oldIter.remove()
                            }
                        }
                    }

                    //after delete duplicates and old orders, do sorting
                    getDistance(newOrders)
                    mainHandler?.postDelayed(this, 6000)

                }
            }
        }, 0)
    }


    private fun getDistance(newOrders: ArrayList<RideInfo>) {
        println("EEE")
        //calculate distance
        if (!newOrders.isNullOrEmpty() && !isGettingLocation) {
            isGettingLocation = true
            val list = getView()?.getAdapter()?.list
            if (list != null) {
                val copyList = arrayListOf<RideInfo>()
                copyList.addAll(list)

                newOrders.forEachIndexed { i, order ->
                    if (isUserGeoAccess) {
                        val fromLat = order.fromLat
                        val fromLng = order.fromLng
                        if (fromLat != null && fromLng != null) {
                            val point = Point(fromLat, fromLng)
                            //get my position (point)
                            getMyPosition {
                                //save distance
                                val distance = calcDistance(it, point)
                                order.distance = distance

                                //if last item, then show in view
                                if (i == newOrders.size - 1) {
                                    if (copyList.isNotEmpty()) {
                                        //we already have some orders in view
                                        copyList.addAll(newOrders)
                                        showOrder(copyList, false)

                                    } else {
                                        //the first showing
                                        showOrder(newOrders, true)
                                    }
                                }

                                //end searching user position
                                isGettingLocation = false
                            }
                        }
                    }
                }
            }
        }
    }


    private fun showOrder(orders: ArrayList<RideInfo>, isFirst: Boolean) {
        var delay = 500L
        orders.sortBy {
            it.distance
        }

        orders.forEachIndexed { i, order ->
            order.isNewOrder = false

            if (!isFirst) {
                if (order.isNewOrder) {
                    //+1 so indexing in RecyclerView start from 1
                    //add delay for animation showing
                    Handler().postDelayed({
                        getView()?.getAdapter()?.setNewOrder(i + 1, order)
                    }, delay)

                    delay += 500
                }
            }
        }

        //first showing
        if (isFirst) {
            getView()?.getAdapter()?.list?.addAll(orders)
        }

        getView()?.showRecycler()
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
                userPosition = null
                userPositionHandler?.postDelayed(this, 1000 * 60)
            }
        }, 0)
    }


    //we already checked geo permission in onCreate in view fragment
    @SuppressLint("MissingPermission")
    override fun getMyPosition(callback: ParentHandler<Point>) {
        val app = App.appComponent.getApp()
        val locationManager = app.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (Permissions.isAccess(Permissions.GEO_PERMISSION, app.applicationContext)) {

            val userPoint = userPosition
            if (userPoint != null) {
                callback(userPoint)
            } else {
                locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    1000,
                    0f,
                    object : android.location.LocationListener {
                        override fun onLocationChanged(location: Location?) {
                            val lat = location?.latitude
                            val lng = location?.longitude

                            if (lat != null && lng != null) {
                                userPosition = Point(lat, lng)
                                userPosition?.let {
                                    callback(it)
                                }
                            }
                        }

                        override fun onStatusChanged(
                            provider: String?,
                            status: Int,
                            extras: Bundle?
                        ) {
                        }

                        override fun onProviderEnabled(provider: String?) {}

                        override fun onProviderDisabled(provider: String?) {}
                    })
            }
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


    override fun instance(): OrdersPresenter {
        return this
    }

}