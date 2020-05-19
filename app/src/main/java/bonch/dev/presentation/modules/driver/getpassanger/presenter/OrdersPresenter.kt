package bonch.dev.presentation.modules.driver.getpassanger.presenter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import bonch.dev.App
import bonch.dev.Permissions
import bonch.dev.domain.entities.driver.getpassanger.Order
import bonch.dev.domain.entities.driver.getpassanger.SelectOrder
import bonch.dev.domain.interactor.driver.getpassanger.IGetPassangerInteractor
import bonch.dev.presentation.base.BasePresenter
import bonch.dev.presentation.interfaces.ParentHandler
import bonch.dev.presentation.modules.driver.getpassanger.GetPassangerComponent
import bonch.dev.presentation.modules.driver.getpassanger.view.ContractView
import bonch.dev.presentation.modules.driver.getpassanger.view.MapOrderView
import com.yandex.mapkit.geometry.Point
import javax.inject.Inject
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt


class OrdersPresenter : BasePresenter<ContractView.IOrdersView>(),
    ContractPresenter.IOrdersPresenter {

    @Inject
    lateinit var getPassangerInteractor: IGetPassangerInteractor

    private var blockHandler: Handler? = null
    private var userPositionHandler: Handler? = null
    private var mainHandler: Handler? = null
    private var isBlock = false

    private var userPosition: Point? = null

    var isUserGeoAccess = false

    init {
        GetPassangerComponent.getPassangerComponent?.inject(this)
    }


    override fun onClickItem(order: Order) {
        if (!isBlock) {
            getView()?.getFragment()?.context?.let {
                SelectOrder.order = order

                //show detail order
                val intent = Intent(it, MapOrderView::class.java)
                getView()?.getFragment()?.startActivityForResult(intent, 1)
            }

            isBlock = true
        }
    }


    override fun startSearchOrders() {
        //TODO replace to searching with server
        mainHandler = Handler()
        mainHandler?.postDelayed(object : Runnable {
            override fun run() {
                getPassangerInteractor.getNewOrder {
                    getView()?.showRecycler()
                    getView()?.getAdapter()?.setNewOrder(it)
                }
                mainHandler?.postDelayed(this, 5000)
            }
        }, 3000)
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

        userPositionHandler?.postDelayed(object : Runnable {
            override fun run() {
                userPosition = null
                userPositionHandler?.postDelayed(this, 1000 * 60 * 3)
            }
        }, 0)
    }


    //we check geo permission in onCreate in view fragment
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