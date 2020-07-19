package bonch.dev.presentation.modules.passenger.getdriver.presenter

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import bonch.dev.App
import bonch.dev.R
import bonch.dev.domain.entities.common.chat.MessageObject
import bonch.dev.domain.entities.common.ride.*
import bonch.dev.domain.entities.passenger.getdriver.ReasonCancel
import bonch.dev.domain.interactor.passenger.getdriver.IGetDriverInteractor
import bonch.dev.presentation.base.BasePresenter
import bonch.dev.presentation.modules.passenger.getdriver.GetDriverComponent
import bonch.dev.presentation.modules.passenger.getdriver.view.ContractView
import bonch.dev.route.MainRouter
import bonch.dev.service.passenger.PassengerRideService
import com.google.gson.Gson
import javax.inject.Inject

class TrackRidePresenter : BasePresenter<ContractView.ITrackRideView>(),
    ContractPresenter.ITrackRidePresenter {

    @Inject
    lateinit var getDriverInteractor: IGetDriverInteractor

    private val REASON = "REASON"

    private var isRegistered = false

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


    override fun registerReceivers() {
        val app = App.appComponent.getApp()

        //check regestered receivers before
        if (!isRegistered) {
            app.registerReceiver(
                changeRideReceiver,
                IntentFilter(PassengerRideService.CHANGE_RIDE_TAG)
            )

            app.registerReceiver(
                chatReceiver,
                IntentFilter(PassengerRideService.CHAT_TAG)
            )

            isRegistered = true
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

        //cancel ride remote
        getDriverInteractor.updateRideStatus(StatusRide.CANCEL) {}

        //send cancel reason
        getDriverInteractor.sendReason(textReason) {}

        if (reasonID == ReasonCancel.MISTAKE_ORDER || reasonID == ReasonCancel.OTHER_REASON) {
            Coordinate.toAdr = null
        }

        backFragment(reasonID)
    }


    override fun backFragment(reasonID: ReasonCancel) {
        if (reasonID != ReasonCancel.MISTAKE_ORDER && reasonID != ReasonCancel.OTHER_REASON) {
            ActiveRide.activeRide?.let { restoreRide(it) }
        }

        ActiveRide.activeRide = null
        getDriverInteractor.removeRideId()

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
        }
    }


    override fun clearData() {
        val app = App.appComponent

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


    override fun showChat(context: Context, fragment: Fragment) {
        MainRouter.showView(R.id.show_chat, getView()?.getNavHost(), null)
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


    override fun instance(): TrackRidePresenter {
        return this
    }


}
