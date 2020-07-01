package bonch.dev.presentation.modules.driver.getpassenger.presenter

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import bonch.dev.App
import bonch.dev.R
import bonch.dev.domain.entities.common.ride.*
import bonch.dev.domain.entities.driver.getpassenger.ReasonCancel
import bonch.dev.domain.interactor.driver.getpassenger.IGetPassengerInteractor
import bonch.dev.presentation.base.BasePresenter
import bonch.dev.presentation.modules.common.chat.view.ChatView
import bonch.dev.presentation.modules.driver.getpassenger.GetPassengerComponent
import bonch.dev.presentation.modules.driver.getpassenger.view.ContractView
import bonch.dev.service.driver.DriverRideService
import com.google.gson.Gson
import javax.inject.Inject

class TrackRidePresenter : BasePresenter<ContractView.ITrackRideView>(),
    ContractPresenter.ITrackRidePresenter {

    @Inject
    lateinit var getPassengerInteractor: IGetPassengerInteractor

    private var timer: WaitingTimer? = null

    private val IS_DRIVER = "IS_DRIVER"

    private var isRegistered = false


    init {
        GetPassengerComponent.getPassengerComponent?.inject(this)
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
                IntentFilter(DriverRideService.CHANGE_RIDE_TAG)
            )

            isRegistered = true
        }
    }


    private fun unregisterReceivers() {
        try {
            App.appComponent.getApp().unregisterReceiver(changeRideReceiver)
        } catch (ex: IllegalArgumentException) {

        }
    }


    private fun onChangeRide(intent: Intent?) {
        val res = App.appComponent.getContext().resources

        val data = intent?.getStringExtra(DriverRideService.CHANGE_RIDE_TAG)

        if (data != null) {
            val ride = Gson().fromJson(data, Ride::class.java)?.ride
            if (ride != null) {
                val userIdLocal = getPassengerInteractor.getUserId()
                val userIdRemote = ride.driver?.id
                val status = ride.statusId

                //passenger cancel ride
                if (status == StatusRide.CANCEL.status && userIdLocal == userIdRemote) {
                    val mainHandler = Handler(Looper.getMainLooper())
                    val myRunnable = Runnable {
                        kotlin.run {
                            //todo получить рельную компенсацию за отмену
                            getView()?.passengerCancelRide(43)
                        }
                    }

                    mainHandler.post(myRunnable)
                }
            } else getView()?.showNotification(res.getString(R.string.errorSystem))
        } else getView()?.showNotification(res.getString(R.string.errorSystem))
    }


    override fun receiveOrder(order: RideInfo?) {
        val res = App.appComponent.getContext().resources

        if (order != null) {
            //set UI
            getView()?.setOrder(order)
        } else {
            getView()?.showNotification(res.getString(R.string.errorSystem))
        }
    }


    override fun nextStep() {
        val oldStep = RideStatus.status
        val nextStep = getByValue(oldStep.status.inc())
        val res = App.appComponent.getContext().resources

        if (nextStep != null) {
            RideStatus.status = nextStep

            changeView(nextStep)

            //update status with server
            getPassengerInteractor.updateRideStatus(nextStep) { isSuccess ->
                if (!isSuccess) {
                    backStep()

                    getView()?.showNotification(res.getString(R.string.errorSystem))
                }
            }
        } else getView()?.showNotification(res.getString(R.string.errorSystem))
    }


    private fun backStep() {
        val oldStep = RideStatus.status
        val backStep = getByValue(oldStep.status.dec())

        if (backStep != null) {
            changeView(backStep)
        }
    }


    private fun changeView(step: StatusRide) {
        if (step == StatusRide.WAIT_FOR_PASSANGER) {
            getView()?.stepWaitPassanger()
            timer?.cancelTimer()
            timer = WaitingTimer()
            timer?.startTimer(this)
        }

        if (step == StatusRide.IN_WAY) {
            timer?.cancelTimer()
            getView()?.stepInWay()
        }

        if (step == StatusRide.GET_PLACE) {
            getView()?.stepDoneRide()
        }
    }


    override fun tickTimerWaitPassanger(sec: Int, isPaidWating: Boolean) {
        getView()?.tickTimerWaitPassanger(sec, isPaidWating)
    }


    private fun getByValue(status: Int) = StatusRide.values().firstOrNull { it.status == status }


    override fun cancelDone(reasonID: ReasonCancel, textReason: String) {
        //cancel ride remote
        getPassengerInteractor.updateRideStatus(StatusRide.CANCEL) {}

        //send cancel reason
        getPassengerInteractor.sendReason(textReason) {}

        stopService()
        clearRide()

        //redirect
        getView()?.finish()
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


    override fun stopService(){
        val app = App.appComponent
        app.getApp().stopService(Intent(app.getContext(), DriverRideService::class.java))
    }


    override fun clearRide() {
        ActiveRide.activeRide = null
        RideStatus.status = StatusRide.SEARCH
        getPassengerInteractor.removeRideId()
    }


    override fun showChat(context: Context, fragment: Fragment) {
        val intent = Intent(context, ChatView::class.java)
        intent.putExtra(IS_DRIVER, true)
        fragment.startActivity(intent)
    }


    override fun instance(): TrackRidePresenter {
        return this
    }

}