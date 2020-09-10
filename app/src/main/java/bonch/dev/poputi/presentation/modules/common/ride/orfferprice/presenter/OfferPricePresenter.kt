package bonch.dev.poputi.presentation.modules.common.ride.orfferprice.presenter

import bonch.dev.poputi.presentation.base.BasePresenter
import bonch.dev.poputi.presentation.modules.common.ride.orfferprice.view.IOfferPriceView


class OfferPricePresenter : BasePresenter<IOfferPriceView>(), IOfferPricePresenter {

    override fun instance(): OfferPricePresenter {
        return this
    }
}


