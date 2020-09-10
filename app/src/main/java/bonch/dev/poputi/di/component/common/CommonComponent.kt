package bonch.dev.poputi.di.component.common

import bonch.dev.poputi.di.component.AppComponent
import bonch.dev.poputi.di.module.common.CommonModule
import bonch.dev.poputi.di.scope.CompScope
import bonch.dev.poputi.domain.interactor.common.chat.ChatInteractor
import bonch.dev.poputi.domain.interactor.common.rate.RateRideInteractor
import bonch.dev.poputi.presentation.modules.common.chat.presenter.ChatPresenter
import bonch.dev.poputi.presentation.modules.common.chat.view.ChatView
import bonch.dev.poputi.presentation.modules.common.onboarding.presenter.OnboardingPresenter
import bonch.dev.poputi.presentation.modules.common.onboarding.view.OnboardingView
import bonch.dev.poputi.presentation.modules.common.ride.orfferprice.presenter.OfferPricePresenter
import bonch.dev.poputi.presentation.modules.common.ride.orfferprice.view.OfferPriceView
import bonch.dev.poputi.presentation.modules.common.ride.rate.presenter.RateRidePresenter
import bonch.dev.poputi.presentation.modules.common.ride.rate.view.RateRideView
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


    //ONBOARDING
    fun inject(target: OnboardingView)
    fun inject(target: OnboardingPresenter)

}