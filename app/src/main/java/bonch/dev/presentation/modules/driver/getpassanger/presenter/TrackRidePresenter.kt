package bonch.dev.presentation.modules.driver.getpassanger.presenter

import android.content.Context
import androidx.fragment.app.Fragment
import bonch.dev.App
import bonch.dev.R
import bonch.dev.domain.entities.driver.getpassanger.Order
import bonch.dev.domain.entities.driver.getpassanger.ReasonCancel
import bonch.dev.domain.entities.driver.getpassanger.SelectOrder
import bonch.dev.domain.interactor.driver.getpassanger.IGetPassangerInteractor
import bonch.dev.presentation.base.BasePresenter
import bonch.dev.presentation.modules.driver.getpassanger.view.ContractView
import bonch.dev.route.MainRouter
import javax.inject.Inject

class TrackRidePresenter : BasePresenter<ContractView.ITrackRideView>(),
    ContractPresenter.ITrackRidePresenter {

    @Inject
    lateinit var getPassangerInteractor: IGetPassangerInteractor


    override fun receiveOrder(order: Order?) {
        if (order != null) {
            //set UI
            getView()?.setOrder(order)
        } else {
            val res = App.appComponent.getContext().resources
            getView()?.showNotification(res.getString(R.string.errorSystem))
        }
    }


    override fun cancelDone(reasonID: ReasonCancel) {
        //cancel ride remote
        //getPassangerInteractor.updateRideStatus(StatusRide.CANCEL)

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
        MainRouter.showView(R.id.show_chat, getView()?.getNavHost(), null)
    }


    override fun instance(): TrackRidePresenter {
        return this
    }

}