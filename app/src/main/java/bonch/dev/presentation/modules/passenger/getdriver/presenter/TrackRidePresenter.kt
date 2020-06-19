package bonch.dev.presentation.modules.passenger.getdriver.presenter

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import bonch.dev.App
import bonch.dev.R
import bonch.dev.domain.entities.common.ride.*
import bonch.dev.domain.entities.passenger.getdriver.ReasonCancel
import bonch.dev.domain.interactor.passenger.getdriver.IGetDriverInteractor
import bonch.dev.presentation.base.BasePresenter
import bonch.dev.presentation.modules.passenger.getdriver.GetDriverComponent
import bonch.dev.presentation.modules.passenger.getdriver.view.ContractView
import bonch.dev.route.MainRouter
import com.google.gson.Gson
import javax.inject.Inject

class TrackRidePresenter : BasePresenter<ContractView.ITrackRideView>(),
    ContractPresenter.ITrackRidePresenter {

    @Inject
    lateinit var getDriverInteractor: IGetDriverInteractor

    private val REASON = "REASON"


    init {
        GetDriverComponent.getDriverComponent?.inject(this)
    }


    override fun initTracking() {
        val res = App.appComponent.getContext().resources

        getDriverInteractor.connectSocket { isSuccess ->
            if (isSuccess) {
                //change ride status
                getDriverInteractor.subscribeOnChangeRide { data, error ->
                    if (error != null || data == null) {
                        getView()?.showNotification(res.getString(R.string.errorSystem))
                    } else {
                        val ride = Gson().fromJson(data, Ride::class.java)?.ride
                        if (ride == null) {
                            getView()?.showNotification(res.getString(R.string.errorSystem))
                        } else {
                            ride.statusId?.let { idStep ->
                                ActiveRide.activeRide = ride
                                nextStep(idStep)
                            }
                        }
                    }
                }
            } else getView()?.showNotification(res.getString(R.string.errorSystem))
        }
    }


    override fun nextStep(idStep: Int) {
        val step = getByValue(idStep)
        val res = App.appComponent.getContext().resources

        if (step != null) {
            RideStatus.status = step

            val mainHandler = Handler(Looper.getMainLooper())
            val myRunnable = Runnable {
                kotlin.run {
                    getView()?.checkoutStatusView(step)
                }
            }

            mainHandler.post(myRunnable)

        } else getView()?.showNotification(res.getString(R.string.errorSystem))
    }


    private fun getByValue(status: Int) = StatusRide.values().firstOrNull { it.status == status }


    override fun getTaxMoney(): Int {
        //TODO рассчитать штраф
        return 100
    }


    override fun cancelDone(reasonID: ReasonCancel) {
        //cancel ride remote
        getDriverInteractor.updateRideStatus(StatusRide.CANCEL) {}

        if (reasonID == ReasonCancel.MISTAKE || reasonID == ReasonCancel.OTHER) {
            Coordinate.toAdr = null
        }

        if (RideStatus.status == StatusRide.WAIT_FOR_PASSANGER) {
            //TODO
            //вычесть бабки
        }

        backFragment(reasonID)
    }


    override fun backFragment(reasonID: ReasonCancel) {
        //clear data
        clearData()

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
            //TODO send reason to server

            getView()?.hideKeyboard()

            cancelDone(ReasonCancel.OTHER)
        }
    }


    private fun clearData() {
        ActiveRide.activeRide = null
    }


    override fun showChat(context: Context, fragment: Fragment) {
        MainRouter.showView(R.id.show_chat, getView()?.getNavHost(), null)
    }


    override fun instance(): TrackRidePresenter {
        return this
    }


}
