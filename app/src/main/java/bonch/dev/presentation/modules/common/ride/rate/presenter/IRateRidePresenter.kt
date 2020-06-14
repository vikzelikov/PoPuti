package bonch.dev.presentation.modules.common.ride.rate.presenter

interface IRateRidePresenter {

    fun instance(): RateRidePresenter

    fun rateDone(rating: Float, comment: String?)

    fun closeRate()

}