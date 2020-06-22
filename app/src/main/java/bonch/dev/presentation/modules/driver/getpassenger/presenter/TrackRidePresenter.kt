package bonch.dev.presentation.modules.driver.getpassenger.presenter

import android.content.Context
import android.content.Intent
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
import com.google.gson.Gson
import javax.inject.Inject

class TrackRidePresenter : BasePresenter<ContractView.ITrackRideView>(),
    ContractPresenter.ITrackRidePresenter {

    @Inject
    lateinit var getPassengerInteractor: IGetPassengerInteractor

    private var timer: WaitingTimer? = null

    private val IS_DRIVER = "IS_DRIVER"


    init {
        GetPassengerComponent.getPassengerComponent?.inject(this)
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
                        }
                    }
                }
            } else getView()?.showNotification(App.appComponent.getContext().getString(R.string.errorSystem))
        }
    }


    override fun tickTimerWaitPassanger(sec: Int, isPaidWating: Boolean) {
        getView()?.tickTimerWaitPassanger(sec, isPaidWating)
    }


    private fun getByValue(status: Int) = StatusRide.values().firstOrNull { it.status == status }


    override fun cancelDone(reasonID: ReasonCancel) {
        //cancel ride remote
        getPassengerInteractor.updateRideStatus(StatusRide.CANCEL) {}

        //todo отправить на сервер

        //clear data
        ActiveRide.activeRide = null

        //redirect
        getView()?.finish()
    }


    override fun cancelDoneOtherReason(comment: String) {
        val res = App.appComponent.getContext().resources

        if (comment.trim().isEmpty()) {
            getView()?.showNotification(res.getString(R.string.writeYourProblemComment))
        } else {
            //TODO send reason to server

            getView()?.hideKeyboard()

            cancelDone(ReasonCancel.OTHER)
        }
    }


    override fun showChat(context: Context, fragment: Fragment) {
        val intent = Intent(context, ChatView::class.java)
        intent.putExtra(IS_DRIVER, true)
        fragment.startActivity(intent)
    }


    override fun onDestroy() {
        getPassengerInteractor.disconnectSocket()
        timer?.cancelTimer()
        timer = null
    }


    override fun instance(): TrackRidePresenter {
        return this
    }

}