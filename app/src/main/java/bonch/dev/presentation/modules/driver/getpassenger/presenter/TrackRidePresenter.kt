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
import bonch.dev.domain.entities.common.chat.MessageObject
import bonch.dev.domain.entities.common.ride.ActiveRide
import bonch.dev.domain.entities.common.ride.Ride
import bonch.dev.domain.entities.common.ride.RideInfo
import bonch.dev.domain.entities.common.ride.StatusRide
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
                IntentFilter(DriverRideService.CHANGE_RIDE_TAG)
            )

            app.registerReceiver(
                chatReceiver,
                IntentFilter(DriverRideService.CHAT_TAG)
            )


            isRegistered = true
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
                var oldStatus = ActiveRide.activeRide?.statusId

                if (oldStatus == null) oldStatus = StatusRide.WAIT_FOR_DRIVER.status

                //passenger cancel ride
                if (status == StatusRide.CANCEL.status && userIdLocal == userIdRemote) {

                    //update status LOCAL
                    ActiveRide.activeRide?.statusId = StatusRide.CANCEL.status

                    val mainHandler = Handler(Looper.getMainLooper())
                    val myRunnable = Runnable {
                        kotlin.run {
                            //todo получить рельную компенсацию за отмену
                            getView()?.passengerCancelRide(43, oldStatus)
                        }
                    }

                    mainHandler.post(myRunnable)
                }
            } else getView()?.showNotification(res.getString(R.string.errorSystem))
        } else getView()?.showNotification(res.getString(R.string.errorSystem))
    }


    private fun onChat(intent: Intent?) {
        val data = intent?.getStringExtra(DriverRideService.CHAT_TAG)

        if (data != null) {
            val message = Gson().fromJson(data, MessageObject::class.java)?.message
            if (message != null) {
                getView()?.checkoutIconChat(true)
            }
        }
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
        val oldStep = ActiveRide.activeRide?.statusId
        val nextStep = getByValue(oldStep?.inc())
        val res = App.appComponent.getContext().resources

        if (nextStep != null) {
            ActiveRide.activeRide?.statusId = nextStep.status

            //change view without checking response from server
            if (nextStep != StatusRide.GET_PLACE) changeState(nextStep, false)
            else getView()?.showEndRideAnim()

            //update status ride with server
            getPassengerInteractor.updateRideStatus(nextStep) { isSuccess ->
                if (isSuccess) {
                    if (nextStep == StatusRide.GET_PLACE) changeState(nextStep, false)
                } else {
                    backStep()

                    getView()?.hideEndRideAnim()
                    getView()?.showNotification(res.getString(R.string.errorSystem))
                }
            }
        } else getView()?.showNotification(res.getString(R.string.errorSystem))
    }


    private fun backStep() {
        val backStep = ActiveRide.activeRide?.statusId?.dec()
        ActiveRide.activeRide?.statusId = backStep

        getByValue(backStep)?.let { changeState(it, false) }
    }


    //chage view for the next step of ride
    override fun changeState(step: StatusRide, isRestoreRide: Boolean) {
        if (step == StatusRide.WAIT_FOR_DRIVER) {
            getView()?.stepWaitDriver()

            timer?.cancelTimer()
            timer = null
        }

        if (step == StatusRide.WAIT_FOR_PASSANGER) {
            getView()?.stepWaitPassenger()

            if (timer == null) {
                timer = WaitingTimer()

                if (isRestoreRide) {
                    val waitTimestamp = getPassengerInteractor.getWaitTimestamp()
                    if (waitTimestamp != -1L) {
                        var waitingTime = (System.currentTimeMillis() - waitTimestamp) / 1000

                        if (waitingTime > WaitingTimer.WAIT_TIMER) {
                            timer?.isPaidWaiting = true
                            waitingTime -= WaitingTimer.WAIT_TIMER
                        }

                        timer?.waitingTime = waitingTime

                    } else getView()?.showNotification(
                        App.appComponent.getContext().getString(R.string.errorSystem)
                    )

                } else getPassengerInteractor.saveWaitTimestamp()

                timer?.startTimer(this)
            }
        }

        if (step == StatusRide.IN_WAY) {
            timer?.cancelTimer()
            getView()?.stepInWay()
        }

        if (step == StatusRide.GET_PLACE) {
            getView()?.stepDoneRide()
        }
    }


    override fun tickTimerWaitPassanger(sec: Long, isPaidWating: Boolean) {
        getView()?.tickTimerWaitPassenger(sec, isPaidWating)
    }


    override fun getByValue(status: Int?) = StatusRide.values().firstOrNull { it.status == status }


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


    override fun stopService() {
        val app = App.appComponent
        app.getApp().stopService(Intent(app.getContext(), DriverRideService::class.java))
    }


    override fun clearRide() {
        getPassengerInteractor.removeRideId()
        ActiveRide.activeRide = null
        timer?.cancelTimer()
        timer = null
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