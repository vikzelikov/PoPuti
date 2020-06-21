package bonch.dev.presentation.modules.driver.getpassenger.presenter

import android.content.Context
import android.content.Intent
import android.os.Handler
import androidx.fragment.app.Fragment
import bonch.dev.App
import bonch.dev.R
import bonch.dev.domain.entities.common.ride.*
import bonch.dev.domain.interactor.driver.getpassenger.IGetPassengerInteractor
import bonch.dev.presentation.base.BasePresenter
import bonch.dev.presentation.modules.common.ride.orfferprice.view.OfferPriceView
import bonch.dev.presentation.modules.common.ride.routing.Routing
import bonch.dev.presentation.modules.driver.getpassenger.GetPassengerComponent
import bonch.dev.presentation.modules.driver.getpassenger.view.ContractView
import com.google.gson.Gson
import com.yandex.mapkit.geometry.Point
import javax.inject.Inject

class DetailOrderPresenter : BasePresenter<ContractView.IDetailOrderView>(),
    ContractPresenter.IDetailOrderPresenter {

    @Inject
    lateinit var routing: Routing

    @Inject
    lateinit var getPassengerInteractor: IGetPassengerInteractor

    var handlerHotification: Handler? = null

    private val OFFER_PRICE_TIMEOUT = 30000L
    val OFFER_PRICE = 1

    init {
        GetPassengerComponent.getPassengerComponent?.inject(this)
    }


    override fun nextFragment() {
        val res = App.appComponent.getContext().resources

        RideStatus.status = StatusRide.WAIT_FOR_DRIVER
        //set this account of driver into ride
        getPassengerInteractor.setDriverInRide { isSuccess ->
            if (isSuccess) {
                getView()?.nextFragment()
            } else {
                getView()?.showNotification(res.getString(R.string.errorSystem))
            }
        }
    }


    override fun receiveOrder(order: RideInfo?) {
        if (order != null) {
            val map = getView()?.getMap()
            val fromLat = order.fromLat
            val fromLng = order.fromLng
            val toLat = order.toLat
            val toLng = order.toLng

            //check
            val fromPoint = if (fromLat != null && fromLng != null) {
                Point(fromLat, fromLng)
            } else {
                null
            }

            //check
            val toPoint = if (toLat != null && toLng != null) {
                Point(toLat, toLng)
            } else {
                null
            }

            //set directions
            if (fromPoint != null && toPoint != null && map != null) {
                //set routes
                routing.submitRequest(fromPoint, toPoint, true, map)
            }

            //set UI
            getView()?.setOrder(order)
        } else {
            val res = App.appComponent.getContext().resources
            getView()?.showNotification(res.getString(R.string.errorSystem))
        }
    }


    override fun subscribeOnChangeRide() {
        getPassengerInteractor.connectSocket { isSuccess ->
            if (isSuccess) {
                getPassengerInteractor.subscribeOnChangeRide { data, _ ->
                    if (data != null) {
                        val ride = Gson().fromJson(data, Ride::class.java)?.ride
                        if (ride != null) {
                            val userIdLocal = getPassengerInteractor.getUserId()
                            val userIdRemote = ride.driver?.id
                            val status = ride.statusId

                            //ride has got another driver
                            if (status != StatusRide.SEARCH.status && userIdLocal != userIdRemote) {
                                getView()?.passengerCancelRide()
                            }

                            //ride change for this driver
                            if (status == StatusRide.WAIT_FOR_DRIVER.status && userIdLocal == userIdRemote) {
                                getPassengerInteractor.disconnectSocket()
                                nextFragment()
                            }
                        }
                    }
                }
            } else getView()?.showNotification(App.appComponent.getContext().getString(R.string.errorSystem))
        }
    }


    private fun getUserPoint(): Point? {
        val userLocation = getView()?.getUserLocationLayer()
        return userLocation?.cameraPosition()?.target
    }


    override fun offerPrice(context: Context, fragment: Fragment) {
        val intent = Intent(context, OfferPriceView::class.java)
        fragment.startActivityForResult(intent, OFFER_PRICE)
    }


    override fun offerPriceDone(price: Int) {
        val rideId = ActiveRide.activeRide?.rideId

        if (rideId != null) {
            getPassengerInteractor.offerPrice(price, rideId) { isSuccess ->
                if (isSuccess) {
                    handlerHotification = Handler()
                    handlerHotification?.postDelayed({
                        getView()?.hideOfferPrice(true)
                    }, OFFER_PRICE_TIMEOUT)
                } else {
                    getView()?.hideOfferPrice(true)
                }
            }
        } else getView()?.showNotification(App.appComponent.getContext().getString(R.string.errorSystem))
    }


    override fun onObjectUpdate() {
        //app accessed user geo
        val userPoint = getUserPoint()
        val map = getView()?.getMap()
        val fromLat = ActiveRide.activeRide?.fromLat
        val fromLng = ActiveRide.activeRide?.fromLng
        val fromPoint = if (fromLat != null && fromLng != null) {
            Point(fromLat, fromLng)
        } else {
            null
        }

        if (userPoint != null && fromPoint != null && map != null) {
            Routing()
                .submitRequest(userPoint, fromPoint, false, map)
        }
    }


    override fun onDestroy() {
        getPassengerInteractor.disconnectSocket()
    }


    override fun showRoute() {
        routing.showRoute()
    }


    override fun instance(): DetailOrderPresenter {
        return this
    }

}