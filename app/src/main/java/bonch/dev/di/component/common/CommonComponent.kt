package bonch.dev.di.component.common

import bonch.dev.di.component.AppComponent
import bonch.dev.di.module.common.CommonModule
import bonch.dev.di.scope.CompScope
import bonch.dev.domain.interactor.common.chat.ChatInteractor
import bonch.dev.domain.interactor.common.offerprice.OfferPriceInteractor
import bonch.dev.domain.interactor.common.rate.RateRideInteractor
import bonch.dev.presentation.modules.common.chat.presenter.ChatPresenter
import bonch.dev.presentation.modules.common.chat.view.ChatView
import bonch.dev.presentation.modules.common.ride.orfferprice.presenter.OfferPricePresenter
import bonch.dev.presentation.modules.common.ride.orfferprice.view.OfferPriceView
import bonch.dev.presentation.modules.common.ride.rate.presenter.RateRidePresenter
import bonch.dev.presentation.modules.common.ride.rate.view.RateRideView
import dagger.Component


@CompScope
@Component(modules = [CommonModule::class], dependencies = [AppComponent::class])
interface CommonComponent {

    //RATING
    fun inject(target: RateRideView)
    fun inject(target: RateRidePresenter)
    fun inject(target: RateRideInteractor)


    //CHAT
    fun inject(target: ChatView)
    fun inject(target: ChatPresenter)
    fun inject(target: ChatInteractor)


    //OFFER PRICE
    fun inject(target: OfferPriceView)
    fun inject(target: OfferPricePresenter)
    fun inject(target: OfferPriceInteractor)


}