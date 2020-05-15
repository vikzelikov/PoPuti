package bonch.dev.presentation.modules.common.orfferprice.presenter

import bonch.dev.domain.interactor.common.offerprice.IOfferPriceInteractor
import bonch.dev.domain.interactor.passanger.getdriver.IGetDriverInteractor
import bonch.dev.presentation.base.BasePresenter
import bonch.dev.presentation.modules.common.CommonComponent
import bonch.dev.presentation.modules.passanger.getdriver.GetDriverComponent
import bonch.dev.presentation.modules.common.orfferprice.view.IOfferPriceView
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


