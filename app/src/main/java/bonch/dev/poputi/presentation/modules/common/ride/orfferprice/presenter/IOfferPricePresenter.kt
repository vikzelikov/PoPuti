package bonch.dev.poputi.presentation.modules.common.ride.orfferprice.presenter

interface IOfferPricePresenter {

    fun instance(): OfferPricePresenter

    fun getAveragePrice(): Int?

}