package bonch.dev.presentation.modules.driver.getpassenger.presenter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.os.Handler
import android.os.Looper
import bonch.dev.App
import bonch.dev.Permissions
import bonch.dev.R
import bonch.dev.domain.entities.common.ride.RideInfo
import bonch.dev.domain.entities.common.ride.ActiveRide
import bonch.dev.domain.interactor.driver.getpassenger.IGetPassengerInteractor
import bonch.dev.presentation.base.BasePresenter
import bonch.dev.presentation.modules.driver.getpassenger.GetPassengerComponent
import bonch.dev.presentation.modules.driver.getpassenger.view.ContractView
import bonch.dev.presentation.modules.driver.getpassenger.view.MapOrderView
import com.yandex.mapkit.geometry.Point
import java.lang.IndexOutOfBoundsException
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

    var userPosition: Point? = null
    var isUserGeoAccess = false

    init {
        GetPassengerComponent.getPassengerComponent?.inject(this)
    }


    override fun onClickItem(order: RideInfo) {
        if (!isBlock) {
            getView()?.getFragment()?.context?.let {
                ActiveRide.activeRide = order

                //show detail order
                val intent = Intent(it, MapOrderView::class.java)
                getView()?.getFragment()?.startActivityForResult(intent, 1)
            }

            isBlock = true
        }
    }


    override fun initOrders() {
        mainHandler?.removeCallbacksAndMessages(null)

        getView()?.showOrdersLoading()

        getMyPosition()

        Thread(Runnable {
            while (true) {
                val userPoint = userPosition
                if (userPoint != null || !isUserGeoAccess) {
                    val mainHandler = Handler(Looper.getMainLooper())
                    val myRunnable = Runnable {
                        kotlin.run {
                            startSearchOrders()
                        }
                    }
                    mainHandler.post(myRunnable)

                    break
                }
            }
        }).start()
    }


    override fun startSearchOrders() {
        //send request every 15 sec
        val adapter = getView()?.getAdapter()
        mainHandler?.removeCallbacksAndMessages(null)
        mainHandler = Handler()

        mainHandler?.postDelayed(object : Runnable {
            override fun run() {
                //get new orders
                getPassengerInteractor.getNewOrder { newOrders, _ ->
                    if (newOrders != null) {
                        //get old orders
                        val oldOrders = adapter?.list

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
                                    try {
                                        adapter.notifyItemRemoved(oldIter.nextIndex() - 1)
                                        oldIter.remove()
                                    } catch (ex: IndexOutOfBoundsException) {
                                    }

                                    if (adapter.list.isNullOrEmpty()) getView()?.showOrdersLoading()
                                }
                            }
                        }

                        //after delete duplicates and old orders, do sorting
                        calcDistance(newOrders)

                    } else getView()?.showNotification(App.appComponent.getContext().getString(R.string.errorSystem)) //show error

                    mainHandler?.postDelayed(this, 5000)
                }
            }
        }, 0)
    }


    private fun calcDistance(orders: ArrayList<RideInfo>) {
        println("EEE ${orders.size}")
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
                                mergeOrders(orders, copyList)
                            }
                        }
                    }
                } else {
                    //geo not accessed
                    mergeOrders(orders, copyList)
                }
            }
        }
    }


    private fun mergeOrders(orders: ArrayList<RideInfo>, copyList: ArrayList<RideInfo>) {
        if (copyList.isNotEmpty()) {
            //we already have some orders in view
            copyList.addAll(orders)
            showOrder(copyList, false)

        } else {
            //the first showing
            showOrder(orders, true)
        }

        //end searching user position
        isGettingLocation = false
    }


    private fun showOrder(orders: ArrayList<RideInfo>, isFirst: Boolean) {
        var delay = 500L
        orders.sortBy { it.distance }

        orders.forEachIndexed { i, order ->
            if (!isFirst) {
                if (order.isNewOrder) {
                    //+1 so indexing in RecyclerView start from 1
                    //add delay for animation showing
                    Handler().postDelayed({
                        getView()?.getAdapter()?.setNewOrder(i, order)
                    }, delay)

                    delay += 500
                }
            }
        }

        //first showing
        if (isFirst) {
            getView()?.showRecycler()
            getView()?.getAdapter()?.setNewOrders(orders)
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


    //we already checked geo permission in onCreate in view fragment
    @SuppressLint("MissingPermission")
    private fun getMyPosition() {
        val app = App.appComponent.getApp()
        val locationManager = app.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (Permissions.isAccess(Permissions.GEO_PERMISSION, app.applicationContext)) {
            locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                1000,
                0f,
                object : ContractPresenter.UserLocationListener {
                    override fun onLocationChanged(location: Location?) {
                        val lat = location?.latitude
                        val lng = location?.longitude

                        if (lat != null && lng != null) {
                            userPosition = Point(lat, lng)
                        }
                    }
                })
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