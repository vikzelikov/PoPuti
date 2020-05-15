package bonch.dev.presentation.modules.common.orfferprice.presenter

interface IOfferPricePresenter {

    fun instance(): OfferPricePresenter

    fun getAveragePrice(): Int?

}