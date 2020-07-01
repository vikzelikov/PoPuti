package bonch.dev.presentation.modules.passenger.getdriver.presenter

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import bonch.dev.App
import bonch.dev.R
import bonch.dev.domain.entities.common.ride.*
import bonch.dev.domain.entities.passenger.getdriver.ReasonCancel
import bonch.dev.domain.interactor.passenger.getdriver.IGetDriverInteractor
import bonch.dev.domain.utils.Vibration
import bonch.dev.presentation.base.BasePresenter
import bonch.dev.presentation.modules.passenger.getdriver.GetDriverComponent
import bonch.dev.presentation.modules.passenger.getdriver.view.ContractView
import bonch.dev.route.MainRouter
import bonch.dev.service.passenger.PassengerRideService
import com.google.gson.Gson
import com.yandex.mapkit.Animation
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import javax.inject.Inject


class GetDriverPresenter : BasePresenter<ContractView.IGetDriverView>(),
    ContractPresenter.IGetDriverPresenter {

    @Inject
    lateinit var getDriverInteractor: IGetDriverInteractor

    private var mainHandler: Handler? = null
    private var isAnimaionSearching = false
    private var isVibration = false
    private var isRegistered = false

    private val REASON = "REASON"

    private var list: ArrayList<Offer> = arrayListOf()
    private var offer: Offer? = null

    init {
        GetDriverComponent.getDriverComponent?.inject(this)
    }


    private val offerPriceReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            onOfferPrice(intent)
        }
    }


    private val deleteOfferReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            onDeleteOffer(intent)
        }
    }


    private val changeRideReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            onChangeRide(intent)
        }
    }


    override fun registerReceivers() {
        val app = App.appComponent.getApp()

        //check regestered receivers before
        if (!isRegistered) {
            app.registerReceiver(
                changeRideReceiver,
                IntentFilter(PassengerRideService.CHANGE_RIDE_TAG)
            )
            app.registerReceiver(
                offerPriceReceiver,
                IntentFilter(PassengerRideService.OFFER_PRICE_TAG)
            )
            app.registerReceiver(
                deleteOfferReceiver,
                IntentFilter(PassengerRideService.DELETE_OFFER_TAG)
            )

            isRegistered = true
        }
    }


    private fun onOfferPrice(intent: Intent?) {
        val res = App.appComponent.getContext().resources

        val data = intent?.getStringExtra(PassengerRideService.OFFER_PRICE_TAG)

        if (data != null) {
            val offer = Gson().fromJson(data, OfferPrice::class.java)?.offerPrice
            if (offer != null) {
                val mainHandler = Handler(Looper.getMainLooper())
                val myRunnable = Runnable {
                    kotlin.run {
                        setNewOffer(offer)
                    }
                }

                mainHandler.post(myRunnable)
            }
        } else getView()?.showNotification(res.getString(R.string.errorSystem))
    }


    private fun onDeleteOffer(intent: Intent?) {
        val data = intent?.getStringExtra(PassengerRideService.DELETE_OFFER_TAG)

        if (data != null) {
            val offerId = Gson().fromJson(data, OfferPrice::class.java)?.offerPrice?.offerId
            if (offerId != null) {
                getView()?.getAdapter()?.deleteOffer(offerId)
            }
        }
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
                ActiveRide.activeRide = ride
                getDriverDone(false)
            }
        }
    }


    private fun getDriverDone(isAcceptByPassenger: Boolean) {
        val res = App.appComponent.getContext().resources

        if (isAcceptByPassenger) {
            val driverId = offer?.driver?.id
            if (driverId != null) {
                getDriverInteractor.setDriverInRide(driverId) { isSuccess ->
                    if (!isSuccess) getView()?.showNotification(res.getString(R.string.errorSystem))
                }
            } else getView()?.showNotification(res.getString(R.string.errorSystem))
        } else {
            //driver has confirmed with a price
            val status = ActiveRide.activeRide?.statusId
            status?.let {
                getByValue(it)?.let { status ->
                    if (status == StatusRide.WAIT_FOR_DRIVER) {
                        //next step
                        val mainHandler = Handler(Looper.getMainLooper())
                        val myRunnable = Runnable {
                            kotlin.run {
                                nextFragment()
                            }
                        }

                        mainHandler.post(myRunnable)
                    }
                }
            }
        }
    }


    private fun nextFragment() {
        if (isAnimaionSearching) {
            //animation off
            val zoom = getView()?.getMap()?.map?.cameraPosition?.zoom
            val userPoint = getUserPoint()

            if (zoom != null && userPoint != null) {
                moveCamera(zoom, userPoint)
            }
        }

        //update ride status LOCAL
        RideStatus.status = StatusRide.WAIT_FOR_DRIVER

        getView()?.removeBackground()

        //stop getting new driver
        clearData()

        //start new background service
        val app = App.appComponent.getApp()
        app.startService(Intent(app.applicationContext, PassengerRideService::class.java))

        getView()?.nextFragment()
    }


    private fun getByValue(status: Int) = StatusRide.values().firstOrNull { it.status == status }


    private fun setNewOffer(offer: Offer) {
        getView()?.getAdapter()?.setNewOffer(offer)

        if (isAnimaionSearching) {
            //animation off
            val zoom = getView()?.getMap()?.map?.cameraPosition?.zoom
            val userPoint = getUserPoint()

            if (zoom != null && userPoint != null) {
                moveCamera(zoom, userPoint)
            }
        }

        if (!isVibration) {
            val context = App.appComponent.getContext()
            Vibration.start(context)

            isVibration = true
        }
    }


    override fun deleteOffer(offerId: Int) {
        getDriverInteractor.deleteOffer(offerId) {}
    }


    override fun confirmAccept() {
        offer?.let {
            getDriverDone(true)
        }
    }


    override fun moveCamera(zoom: Float, point: Point) {
        getView()?.getMap()?.map?.move(
            CameraPosition(point, zoom, 0.0f, 0.0f),
            Animation(Animation.Type.SMOOTH, 30f),
            null
        )
    }


    override fun cancelDone(reasonID: ReasonCancel, textReason: String) {
        //cancel ride remote
        getDriverInteractor.updateRideStatus(StatusRide.CANCEL) {}

        //send cancel reason
        getDriverInteractor.sendReason(textReason) {}

        if (reasonID == ReasonCancel.MISTAKE_ORDER || reasonID == ReasonCancel.OTHER_REASON) {
            Coordinate.toAdr = null
        }

        //stop getting new offers
        clearData()
        ActiveRide.activeRide = null
        getDriverInteractor.removeRideId()
    }


    override fun backFragment(reasonID: ReasonCancel) {
        val res = App.appComponent.getContext().resources
        getView()?.showNotification(res.getString(R.string.rideCancel))

        //redirect
        val bundle = Bundle()
        bundle.putInt(REASON, reasonID.reason)
        MainRouter.showView(R.id.show_back_view, getView()?.getNavHost(), bundle)
    }


    override fun cancelDoneOtherReason(comment: String) {
        val res = App.appComponent.getContext().resources

        if (comment.trim().isEmpty()) {
            getView()?.showNotification(res.getString(R.string.writeYourProblemComment))
        } else {
            val textReason = "OTHER_REASON: ".plus(comment)

            getView()?.hideKeyboard()

            cancelDone(ReasonCancel.OTHER_REASON, textReason)

            backFragment(ReasonCancel.OTHER_REASON)
        }
    }


    override fun timeExpired(textReason: String) {
        getView()?.hideKeyboard()

        cancelDone(ReasonCancel.MISTAKE_ORDER, textReason)
    }


    private fun clearData() {
        val app = App.appComponent

        app.getApp().stopService(Intent(app.getContext(), PassengerRideService::class.java))
        mainHandler?.removeCallbacksAndMessages(null)
        OffersMainTimer.getInstance()?.cancel()
        OffersMainTimer.deleteInstance()
        list.clear()
    }


    private fun getUserPoint(): Point? {
        val userLocation = getView()?.getUserLocationLayer()
        return userLocation?.cameraPosition()?.target
    }


    override fun onUserLocationAttach() {
        //start animation searching
        val userPoint = getUserPoint()

        if (userPoint != null && !isAnimaionSearching) {
            getView()?.startAnimSearch(
                Point(
                    userPoint.latitude,
                    userPoint.longitude
                )
            )

            isAnimaionSearching = true
        }
    }


    override fun setOffer(offer: Offer) {
        this.offer = offer
    }


    override fun getOffer(): Offer? = this.offer


    override fun getOffers(): ArrayList<Offer> {
        return list
    }


    override fun instance(): GetDriverPresenter {
        return this
    }
}
