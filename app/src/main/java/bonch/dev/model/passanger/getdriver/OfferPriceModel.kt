package bonch.dev.model.passanger.getdriver

import bonch.dev.presenter.passanger.getdriver.OfferPricePresenter

class OfferPriceModel(private val offerPricePresenter: OfferPricePresenter) {

    fun getAveragePrice(): Int {
        return 599
    }
}