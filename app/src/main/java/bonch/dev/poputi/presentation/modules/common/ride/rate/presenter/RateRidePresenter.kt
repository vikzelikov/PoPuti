package bonch.dev.poputi.presentation.modules.common.ride.rate.presenter

import android.app.Activity.RESULT_OK
import bonch.dev.poputi.App
import bonch.dev.poputi.R
import bonch.dev.poputi.domain.entities.common.rate.Review
import bonch.dev.poputi.domain.entities.common.ride.ActiveRide
import bonch.dev.poputi.domain.interactor.common.rate.IRateRideInteractor
import bonch.dev.poputi.presentation.base.BasePresenter
import bonch.dev.poputi.presentation.modules.common.CommonComponent
import bonch.dev.poputi.presentation.modules.common.ride.rate.view.IRateRideView
import bonch.dev.poputi.route.MainRouter
import java.lang.Exception
import javax.inject.Inject

class RateRidePresenter : BasePresenter<IRateRideView>(), IRateRidePresenter {

    @Inject
    lateinit var rateRideInteractor: IRateRideInteractor

    private val FEEDBACK = 1

    init {
        CommonComponent.commonComponent?.inject(this)
    }


    override fun rateDone(rating: Int, comment: String?) {
        val res = App.appComponent.getContext().resources
        val isForPassanger = getView()?.isPassanger()

        if (isForPassanger != null) {
            val review = Review()
            comment?.let { review.text = it }
            review.rating = rating
            //send review to SERVER
            rateRideInteractor.sendReview(review, isForPassanger) {}

            clearData()

            if (isForPassanger) {
                //passenger - this is not new Activity, show general notification
                getView()?.showNotification(res.getString(R.string.thanksForRate))
                MainRouter.showView(R.id.show_back_view, getView()?.getNavHost(), null)
            } else {
                //driver - this New Activity, show notification in start launch fragment
                getView()?.finish(FEEDBACK)
            }
        } else {
            getView()?.showNotification(res.getString(R.string.errorSystem))

            failLogout()
        }
    }


    override fun closeRate() {
        val res = App.appComponent.getContext().resources

        val isForPassanger = getView()?.isPassanger()

        if (isForPassanger != null) {

            clearData()

            if (isForPassanger) {
                MainRouter.showView(R.id.show_back_view, getView()?.getNavHost(), null)
            } else {
                getView()?.finish(RESULT_OK)
            }
        } else {
            getView()?.showNotification(res.getString(R.string.errorSystem))

            failLogout()
        }
    }


    private fun failLogout() {
        try {
            MainRouter.showView(R.id.show_back_view, getView()?.getNavHost(), null)
        } catch (ex: Exception) {
            getView()?.finish(RESULT_OK)
        }

        clearData()
    }


    private fun clearData(){
        ActiveRide.activeRide = null
    }


    override fun instance(): RateRidePresenter {
        return this
    }
}