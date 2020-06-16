package bonch.dev.presentation.modules.driver.getpassenger.presenter

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import bonch.dev.App
import bonch.dev.R
import bonch.dev.domain.entities.common.ride.RideInfo
import bonch.dev.domain.entities.common.ride.RideStatus
import bonch.dev.domain.entities.common.ride.StatusRide
import bonch.dev.domain.entities.driver.getpassenger.ReasonCancel
import bonch.dev.domain.entities.driver.getpassenger.SelectOrder
import bonch.dev.domain.interactor.driver.getpassenger.IGetPassengerInteractor
import bonch.dev.presentation.base.BasePresenter
import bonch.dev.presentation.modules.common.chat.view.ChatView
import bonch.dev.presentation.modules.driver.getpassenger.GetPassengerComponent
import bonch.dev.presentation.modules.driver.getpassenger.view.ContractView
import javax.inject.Inject

class TrackRidePresenter : BasePresenter<ContractView.ITrackRideView>(),
    ContractPresenter.ITrackRidePresenter {

    @Inject
    lateinit var getPassengerInteractor: IGetPassengerInteractor

    var timer: WaitingTimer? = null


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

            //update status with server
            getPassengerInteractor.updateRideStatus(nextStep) { isSuccess ->
                if (isSuccess) {
                    if (nextStep == StatusRide.WAIT_FOR_PASSANGER) {
                        getView()?.stepWaitPassanger()
                        timer = WaitingTimer()
                        timer?.startTimer(this)
                    }

                    if (nextStep == StatusRide.IN_WAY) {
                        timer?.cancelTimer()
                        getView()?.stepInWay()
                    }

                    if (nextStep == StatusRide.GET_PLACE) {
                        getView()?.stepDoneRide()
                    }

                } else getView()?.showNotification(res.getString(R.string.errorSystem))
            }
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
        SelectOrder.order = null

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
        fragment.startActivityForResult(intent, 1)
    }


    override fun instance(): TrackRidePresenter {
        return this
    }

}