package bonch.dev.poputi.presentation.modules.passenger.getdriver.presenter

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Handler
import android.os.Looper
import bonch.dev.poputi.App
import bonch.dev.poputi.R
import bonch.dev.poputi.domain.entities.common.ride.*
import bonch.dev.poputi.domain.entities.passenger.getdriver.ReasonCancel
import bonch.dev.poputi.domain.interactor.passenger.getdriver.IGetDriverInteractor
import bonch.dev.poputi.domain.utils.Vibration
import bonch.dev.poputi.presentation.base.BasePresenter
import bonch.dev.poputi.presentation.modules.passenger.getdriver.GetDriverComponent
import bonch.dev.poputi.presentation.modules.passenger.getdriver.view.ContractView
import bonch.dev.poputi.service.passenger.PassengerRideService
import com.google.gson.Gson
import com.yandex.mapkit.Animation
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


class GetOffersPresenter : BasePresenter<ContractView.IGetOffersView>(),
    ContractPresenter.IGetOffersPresenter {

    @Inject
    lateinit var getDriverInteractor: IGetDriverInteractor

    private var isVibration = false
    private var isNextStep = false

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

        isVibration = false
        isNextStep = false
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
                getOfferDone(false)
            }
        }
    }


    private fun getOfferDone(isAcceptByPassenger: Boolean) {
        val res = App.appComponent.getContext().resources

        if (!isNextStep) {
            if (isAcceptByPassenger) {
                val driverId = offer?.driver?.id
                val price = offer?.price

                if (driverId != null && price != null) {

                    nextFragment()

                    isNextStep = true

                    getDriverInteractor.setDriverInRide(driverId, price) { isSuccess ->
                        if (!isSuccess) {
                            //back step
                            val mainHandler = Handler(Looper.getMainLooper())
                            val myRunnable = Runnable {
                                kotlin.run {
                                    getView()?.showNotification(res.getString(R.string.errorSystem))

                                    getView()?.getOfferFail()
                                }
                            }
                            mainHandler.post(myRunnable)
                        }
                    }
                } else getView()?.showNotification(res.getString(R.string.errorSystem))
            } else {
                //driver has confirmed with a price
                val status = ActiveRide.activeRide?.statusId
                status?.let {
                    getByValue(it)?.let { status ->
                        if (status == StatusRide.WAIT_FOR_DRIVER) {

                            isNextStep = true

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
    }


    private fun nextFragment() {
        //animation off
        val zoom = getView()?.getMap()?.map?.cameraPosition?.zoom
        val userPoint = getUserPoint()

        if (zoom != null && userPoint != null) {
            moveCamera(zoom, userPoint, 0f)
        }

        //update ride status LOCAL
        ActiveRide.activeRide?.statusId = StatusRide.WAIT_FOR_DRIVER.status

        getView()?.removeBackground()

        //stop getting new driver
        clearData()

        getView()?.attachTrackRide()
    }


    private fun getByValue(status: Int) =
        StatusRide.values().firstOrNull { it.status == status }


    private fun setNewOffer(offer: Offer) {
        getView()?.getAdapter()?.setNewOffer(offer)

        //animation off
        val zoom = getView()?.getMap()?.map?.cameraPosition?.zoom
        val userPoint = getUserPoint()

        if (zoom != null && userPoint != null) {
            moveCamera(zoom, userPoint, 0f)
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


    override fun checkOnOffers() {
        getDriverInteractor.getOffers { offers, _ ->
            if (offers != null) {
                val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                val today = Calendar.getInstance().timeInMillis
                val thatDay = Calendar.getInstance()

                isVibration = true

                offers.forEach { offer ->
                    try {
                        val createdDate = offer.createDate
                        if (createdDate != null) {
                            val parseDate = format.parse(createdDate)
                            parseDate?.let {
                                thatDay.time = parseDate
                                val diff = today - thatDay.timeInMillis
                                offer.timeLine =
                                    OffersMainTimer.TIME_EXPIRED_ITEM - (diff / 1000)
                            }
                        }
                    } catch (e: ParseException) {
                        e.printStackTrace()
                    }
                }

                var delay = 500L
                offers.sortBy { it.timeLine }

                offers.forEach { offer ->
                    if (offer.timeLine > 1 && offer.timeLine < OffersMainTimer.TIME_EXPIRED_ITEM) {
                        Handler().postDelayed({
                            setNewOffer(offer)
                        }, delay)

                        delay += 500
                    }
                }
            }
        }
    }


    override fun confirmAccept() {
        offer?.let {
            getOfferDone(true)
        }
    }


    private fun moveCamera(zoom: Float, point: Point, duration: Float) {
        getView()?.getMap()?.map?.move(
            CameraPosition(point, zoom, 0.0f, 0.0f),
            Animation(Animation.Type.LINEAR, duration),
            null
        )
    }


    override fun cancelDone(reasonID: ReasonCancel, textReason: String) {
        //stop getting new offers
        val app = App.appComponent
        app.getApp().stopService(Intent(app.getContext(), PassengerRideService::class.java))

        ActiveRide.activeRide?.let { restoreRide(it) }

        cancelRideListener(reasonID, textReason)

        clearData()
        ActiveRide.activeRide = null
        getDriverInteractor.removeRideId()
    }


    private fun cancelRideListener(reasonID: ReasonCancel, textReason: String) {
        getDriverInteractor.updateRideStatus(StatusRide.CANCEL) { isSuccess ->
            if (!isSuccess) {
                Handler().postDelayed({
                    cancelDone(reasonID, textReason)
                }, 500)
            }
        }

        getDriverInteractor.sendReason(textReason) {}

        ActiveRide.activeRide = null
        getDriverInteractor.removeRideId()
    }


    override fun backFragment(reasonID: ReasonCancel) {
        val res = App.appComponent.getContext().resources
        getView()?.showNotification(res.getString(R.string.rideCancel))

        //redirect
        getView()?.onCancelRide(reasonID)
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
        OffersMainTimer.getInstance()?.cancel()
        OffersMainTimer.deleteInstance()
        list.clear()
    }


    private fun getUserPoint(): Point? {
        val userLocation = getView()?.getUserLocationLayer()
        return userLocation?.cameraPosition()?.target
    }


    override fun startSearchAnimation() {
        val userPoint = getUserPoint()

        if (userPoint != null) {
            val zoom = getView()?.getMap()?.map?.cameraPosition?.zoom?.minus(1)
            zoom?.let {
                moveCamera(35f, userPoint, 0f)
                moveCamera(zoom, userPoint, 150f)
            }
        }
    }


    private fun restoreRide(ride: RideInfo) {
        val fromLat = ride.fromLat
        val fromLng = ride.fromLng
        val toLat = ride.toLat
        val toLng = ride.toLng

        val fromPoint =
            if (fromLat != null && fromLng != null) AddressPoint(fromLat, fromLng)
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


    override fun setOffer(offer: Offer) {
        this.offer = offer
    }


    override fun getOffer(): Offer? = this.offer


    override fun getOffers() = list


    override fun instance() = this

}
