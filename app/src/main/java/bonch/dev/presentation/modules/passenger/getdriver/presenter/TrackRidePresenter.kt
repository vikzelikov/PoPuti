package bonch.dev.presentation.modules.passenger.getdriver.presenter

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import bonch.dev.App
import bonch.dev.R
import bonch.dev.domain.entities.common.ride.Coordinate
import bonch.dev.domain.entities.common.ride.RideStatus
import bonch.dev.domain.entities.common.ride.StatusRide
import bonch.dev.domain.entities.passenger.getdriver.Driver
import bonch.dev.domain.entities.passenger.getdriver.DriverObject
import bonch.dev.domain.entities.passenger.getdriver.ReasonCancel
import bonch.dev.domain.interactor.passenger.getdriver.IGetDriverInteractor
import bonch.dev.presentation.base.BasePresenter
import bonch.dev.presentation.modules.passenger.getdriver.GetDriverComponent
import bonch.dev.presentation.modules.passenger.getdriver.view.ContractView
import bonch.dev.route.MainRouter
import javax.inject.Inject

class TrackRidePresenter : BasePresenter<ContractView.ITrackRideView>(),
    ContractPresenter.ITrackRidePresenter {

    @Inject
    lateinit var getDriverInteractor: IGetDriverInteractor

    private val REASON = "REASON"


    init {
        GetDriverComponent.getDriverComponent?.inject(this)
    }


    override fun setInfoDriver(driver: Driver) {
        getView()?.setInfoDriver(driver)

        //save driver in case close app
        if (driver.nameDriver != null) {
            getDriverInteractor.saveDriver(driver)
        }
    }


    override fun initTracking() {
        getDriverInteractor.listenerOnChangeRideStatus { data, error ->
            //driver accept this ride
            //parsing driver object
            //parsing ride
            //nextStep()
        }
    }


    override fun nextStep(idStep: Int) {
        val step = getByValue(idStep)
        val res = App.appComponent.getContext().resources

        if (step != null) {
            RideStatus.status = step

            getView()?.checkoutStatusView(step)

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
        DriverObject.driver = null
        getDriverInteractor.removeDriver()
    }


    override fun showChat(context: Context, fragment: Fragment) {
        MainRouter.showView(R.id.show_chat, getView()?.getNavHost(), null)
    }


    override fun instance(): TrackRidePresenter {
        return this
    }


}
