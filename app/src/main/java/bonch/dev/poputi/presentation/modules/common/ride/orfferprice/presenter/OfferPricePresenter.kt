package bonch.dev.poputi.presentation.modules.common.ride.orfferprice.presenter

import bonch.dev.poputi.domain.interactor.common.offerprice.IOfferPriceInteractor
import bonch.dev.poputi.presentation.base.BasePresenter
import bonch.dev.poputi.presentation.modules.common.CommonComponent
import bonch.dev.poputi.presentation.modules.common.ride.orfferprice.view.IOfferPriceView
import javax.inject.Inject


class OfferPricePresenter : BasePresenter<IOfferPriceView>(), IOfferPricePresenter {

    @Inject
    lateinit var offerPriceInteractor: IOfferPriceInteractor

    init {
        CommonComponent.commonComponent?.inject(this)
    }


    override fun getAveragePrice(): Int? {
        return offerPriceInteractor.getAveragePrice()
    }


    override fun instance(): OfferPricePresenter {
        return this
    }
}


