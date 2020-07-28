package bonch.dev.presentation.modules.driver.getpassenger.presenter

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import bonch.dev.App
import bonch.dev.R
import bonch.dev.domain.entities.common.ride.*
import bonch.dev.domain.interactor.driver.getpassenger.IGetPassengerInteractor
import bonch.dev.domain.utils.Vibration
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

    var blockHandler: Handler? = null
    var handlerHotification: Handler? = null

    private var activeOfferId: Int? = null
    private var activeOfferPrice: Int? = null
    private var isBlock = false
    var isStop = true

    private val OFFER_PRICE_TIMEOUT = 30000L
    val OFFER_PRICE = 1

    init {
        GetPassengerComponent.getPassengerComponent?.inject(this)
    }


    override fun nextFragment() {
        getView()?.showLoading()

        val context = App.appComponent.getContext()

        //update status
        ActiveRide.activeRide?.statusId = StatusRide.WAIT_FOR_DRIVER.status

        if (!isBlock) {
            isBlock = true

            //set this account of driver into ride
            getPassengerInteractor.setDriverInRide { isSuccess ->

                val rideId = ActiveRide.activeRide?.rideId

                getView()?.hideLoading()

                if (isSuccess && rideId != null) {
                    isStop = false

                    getPassengerInteractor.saveRideId(rideId)

                    context.let { Vibration.start(it) }
                    getView()?.showNotification(context.getString(R.string.rideCreated))

                    getView()?.nextFragment()
                } else {
                    getView()?.showNotification(context.getString(R.string.errorSystem))
                }
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
            val fromPoint = if (fromLat != null && fromLng != null) Point(fromLat, fromLng)
            else null

            //check
            val toPoint = if (toLat != null && toLng != null) Point(toLat, toLng)
            else null

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


    //SOCKET: change ride status (cancel) or cancel offer price
    override fun subscribeOnRide() {
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
                                ActiveRide.activeRide?.price = activeOfferPrice
                                clearData()
                                nextFragment()
                            }
                        }
                    }
                }

                getPassengerInteractor.subscribeOnDeleteOffer { data, _ ->
                    if (data != null) {
                        val offerId =
                            Gson().fromJson(data, OfferPrice::class.java)?.offerPrice?.offerId

                        if (offerId == activeOfferId) {
                            cancelOffer(false)
                        }
                    }
                }
            } else getView()?.showNotification(
                App.appComponent.getContext().getString(R.string.errorSystem)
            )
        }
    }


    private fun getUserPoint(): Point? {
        val userLocation = getView()?.getUserLocationLayer()
        return userLocation?.cameraPosition()?.target
    }


    override fun offerPrice(context: Context, fragment: Fragment) {
        val intent = Intent(context, OfferPriceView::class.java)
        fragment.startActivityForResult(intent, OFFER_PRICE)

        //start offer price process
        isStop = false
    }


    override fun offerPriceDone(price: Int) {
        val rideId = ActiveRide.activeRide?.rideId

        if (rideId != null) {
            getPassengerInteractor.offerPrice(price, rideId) { offer, _ ->
                if (offer != null) {
                    //save offerId for delete in future and price
                    offer.offerId?.let { activeOfferId = it }
                    offer.price?.let { activeOfferPrice = it }

                    handlerHotification = Handler()
                    handlerHotification?.postDelayed({
                        getView()?.hideOfferPrice(true)
                    }, OFFER_PRICE_TIMEOUT)
                } else {
                    getView()?.hideOfferPrice(true)
                }
            }
        } else getView()?.showNotification(
            App.appComponent.getContext().getString(R.string.errorSystem)
        )
    }


    override fun cancelOffer(byDriver: Boolean) {
        val offerId = activeOfferId

        val mainHandler = Handler(Looper.getMainLooper())
        val myRunnable = Runnable {
            kotlin.run {
                getView()?.hideOfferPrice(false)

                if (!byDriver) getView()?.showNotification(
                    App.appComponent.getApp().getString(R.string.userCancellPrice)
                )
            }
        }

        mainHandler.post(myRunnable)

        offerId?.let {
            if (byDriver)
                getPassengerInteractor.deleteOffer(offerId) {}

            activeOfferId = null
        }
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
            Routing().submitRequest(userPoint, fromPoint, false, map)
        }
    }


    override fun startProcessBlock() {
        if (blockHandler == null) {
            blockHandler = Handler()
        }

        blockHandler?.postDelayed(object : Runnable {
            override fun run() {
                isBlock = false
                blockHandler?.postDelayed(this, 3500)
            }
        }, 0)
    }


    override fun onDestroy() {
        cancelOffer(true)

        clearData()

        ActiveRide.activeRide = null
    }


    private fun clearData() {
        handlerHotification?.removeCallbacksAndMessages(null)
        blockHandler?.removeCallbacksAndMessages(null)
        getPassengerInteractor.disconnectSocket()
    }


    override fun showRoute() {
        routing.showRoute()
    }


    override fun instance(): DetailOrderPresenter {
        return this
    }

}