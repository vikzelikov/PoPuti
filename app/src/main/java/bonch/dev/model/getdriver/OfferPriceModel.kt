package bonch.dev.model.getdriver

import bonch.dev.presenter.getdriver.OfferPricePresenter

class OfferPriceModel(private val offerPricePresenter: OfferPricePresenter) {

    fun getAveragePrice(): Int {
        return 599
    }
}