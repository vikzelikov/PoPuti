package bonch.dev.presentation.modules.passanger.getdriver.ride.presenter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import bonch.dev.App
import bonch.dev.R
import bonch.dev.domain.entities.common.ride.RideStatus
import bonch.dev.domain.entities.common.ride.StatusRide
import bonch.dev.domain.entities.passanger.getdriver.*
import bonch.dev.domain.interactor.passanger.getdriver.IGetDriverInteractor
import bonch.dev.domain.utils.Vibration
import bonch.dev.presentation.base.BasePresenter
import bonch.dev.presentation.modules.common.chat.view.ChatView
import bonch.dev.presentation.modules.passanger.getdriver.GetDriverComponent
import bonch.dev.presentation.modules.passanger.getdriver.ride.view.ContractView
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


    override fun startTrackingDriver() {
        //TODO
        //update icon driver on map
        //send request to server every 5 seconds
        val context = App.appComponent.getContext()

        //TODO ***************************
        Handler().postDelayed({
            RideStatus.status = StatusRide.WAIT_FOR_PASSANGER
            Vibration.start(context)
            getView()?.checkoutStatusView()
        }, 4000)

        //TODO ***************************
    }


    override fun getTaxMoney(): Int {
        //TODO рассчитать штраф
        return 100
    }


    override fun cancelDone(reasonID: ReasonCancel) {
        //cancel ride remote
        getDriverInteractor.updateRideStatus(StatusRide.CANCEL)

        if(reasonID == ReasonCancel.MISTAKE || reasonID == ReasonCancel.OTHER){
            Coordinate.toAdr = null
        }

        if (RideStatus.status == StatusRide.WAIT_FOR_PASSANGER) {
            //TODO
            //вычесть бабки
        }

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
        //MainRouter.showView(R.id.show_chat, getView()?.getNavHost(), null)
        //TODO remove it
        val intent = Intent(context, ChatView::class.java)
        fragment.startActivityForResult(intent, 1)
    }


    override fun instance(): TrackRidePresenter {
        return this
    }


}
