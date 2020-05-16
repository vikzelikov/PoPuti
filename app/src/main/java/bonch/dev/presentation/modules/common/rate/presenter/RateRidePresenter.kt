package bonch.dev.presentation.modules.common.rate.presenter

import bonch.dev.App
import bonch.dev.R
import bonch.dev.domain.interactor.common.rate.IRateRideInteractor
import bonch.dev.presentation.base.BasePresenter
import bonch.dev.presentation.modules.common.CommonComponent
import bonch.dev.presentation.modules.common.rate.view.IRateRideView
import bonch.dev.route.MainRouter
import javax.inject.Inject

class RateRidePresenter : BasePresenter<IRateRideView>(), IRateRidePresenter {

    @Inject
    lateinit var rateRideInteractor: IRateRideInteractor

    init {
        CommonComponent.commonComponent?.inject(this)
    }


    override fun rateDone(rating: Float, comment: String?) {
        //TODO send to server data about rate
        val res = App.appComponent.getContext().resources
        getView()?.showNotification(res.getString(R.string.thanksForRate))

        val isForPassanger = getView()?.isPassanger()

        if (isForPassanger != null) {
            if (isForPassanger) {
                MainRouter.showView(R.id.show_back_view, getView()?.getNavHost(), null)
            } else {
                //other redirect
            }
        }
    }


    override fun closeRate() {
        val isForPassanger = getView()?.isPassanger()

        if (isForPassanger != null) {
            if (isForPassanger) {
                MainRouter.showView(R.id.show_back_view, getView()?.getNavHost(), null)
            } else {
                //other redirect
            }
        }
    }


    override fun instance(): RateRidePresenter {
        return this
    }
}