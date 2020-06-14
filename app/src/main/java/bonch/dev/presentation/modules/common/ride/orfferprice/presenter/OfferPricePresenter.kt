package bonch.dev.presentation.modules.common.ride.orfferprice.presenter

import bonch.dev.domain.interactor.common.offerprice.IOfferPriceInteractor
import bonch.dev.presentation.base.BasePresenter
import bonch.dev.presentation.modules.common.CommonComponent
import bonch.dev.presentation.modules.common.ride.orfferprice.view.IOfferPriceView
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


