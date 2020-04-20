package bonch.dev.data.repository.passanger.getdriver

import bonch.dev.presentation.presenter.passanger.getdriver.OfferPricePresenter

class OfferPriceModel(private val offerPricePresenter: OfferPricePresenter) {

    fun getAveragePrice(): Int {
        return 599
    }
}