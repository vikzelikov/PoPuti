package bonch.dev.poputi.presentation.modules.common.ride.rate.presenter

interface IRateRidePresenter {

    fun instance(): RateRidePresenter

    fun rateDone(rating: Int, comment: String?)

    fun closeRate()

}