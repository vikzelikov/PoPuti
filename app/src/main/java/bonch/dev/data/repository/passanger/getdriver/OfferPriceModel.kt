package bonch.dev.data.repository.passanger.getdriver

import bonch.dev.presentation.modules.passanger.getdriver.orfferprice.presenter.OfferPricePresenter

class OfferPriceModel(private val offerPricePresenter: OfferPricePresenter) {

    fun getAveragePrice(): Int {
        return 599
    }
}