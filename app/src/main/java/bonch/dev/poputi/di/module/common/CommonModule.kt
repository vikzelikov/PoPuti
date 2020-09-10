package bonch.dev.poputi.di.module.common

import bonch.dev.poputi.data.repository.common.chat.ChatRepository
import bonch.dev.poputi.data.repository.common.chat.IChatRepository
import bonch.dev.poputi.data.repository.common.rate.IRateRideRepository
import bonch.dev.poputi.data.repository.common.rate.RateRideRepository
import bonch.dev.poputi.data.storage.common.profile.IProfileStorage
import bonch.dev.poputi.data.storage.common.profile.ProfileStorage
import bonch.dev.poputi.data.storage.driver.getpassenger.GetPassengerStorage
import bonch.dev.poputi.data.storage.driver.getpassenger.IGetPassengerStorage
import bonch.dev.poputi.data.storage.passenger.getdriver.GetDriverStorage
import bonch.dev.poputi.data.storage.passenger.getdriver.IGetDriverStorage
import bonch.dev.poputi.di.scope.CompScope
import bonch.dev.poputi.domain.interactor.common.chat.ChatInteractor
import bonch.dev.poputi.domain.interactor.common.chat.IChatInteractor
import bonch.dev.poputi.domain.interactor.common.profile.IProfileInteractor
import bonch.dev.poputi.domain.interactor.common.profile.ProfileInteractor
import bonch.dev.poputi.domain.interactor.common.rate.IRateRideInteractor
import bonch.dev.poputi.domain.interactor.common.rate.RateRideInteractor
import bonch.dev.poputi.presentation.modules.common.chat.presenter.ChatPresenter
import bonch.dev.poputi.presentation.modules.common.chat.presenter.IChatPresenter
import bonch.dev.poputi.presentation.modules.common.onboarding.presenter.IOnboardingPresenter
import bonch.dev.poputi.presentation.modules.common.onboarding.presenter.OnboardingPresenter
import bonch.dev.poputi.presentation.modules.common.ride.orfferprice.presenter.IOfferPricePresenter
import bonch.dev.poputi.presentation.modules.common.ride.orfferprice.presenter.OfferPricePresenter
import bonch.dev.poputi.presentation.modules.common.ride.rate.presenter.IRateRidePresenter
import bonch.dev.poputi.presentation.modules.common.ride.rate.presenter.RateRidePresenter
import dagger.Module
import dagger.Provides


@Module
class CommonModule {

    //RATING
    @Provides
    @CompScope
    fun providesRateRidePresenter(): IRateRidePresenter = RateRidePresenter()

    @Provides
    @CompScope
    fun providesRateRideInteractor(): IRateRideInteractor = RateRideInteractor()

    @Provides
    @CompScope
    fun providesRateRideRepository(): IRateRideRepository = RateRideRepository()


    //CHAT
    @Provides
    @CompScope
    fun provideChatPresenter(): IChatPresenter = ChatPresenter()

    @Provides
    @CompScope
    fun provideChatInteractor(): IChatInteractor = ChatInteractor()

    @Provides
    @CompScope
    fun provideChatRepository(): IChatRepository = ChatRepository()


    //OFFER PRICE
    @Provides
    @CompScope
    fun provideOfferPricePresenter(): IOfferPricePresenter = OfferPricePresenter()


    //ONBOARDING
    @Provides
    @CompScope
    fun provideOnboardingPresenter(): IOnboardingPresenter = OnboardingPresenter()



    //COMMON
    @Provides
    @CompScope
    fun provideProfileStorage(): IProfileStorage = ProfileStorage()

    @Provides
    @CompScope
    fun provideGetPassengerStorage(): IGetPassengerStorage = GetPassengerStorage()

    @Provides
    @CompScope
    fun provideGetDriverStorage(): IGetDriverStorage = GetDriverStorage()

    @Provides
    @CompScope
    fun provideProfileInteractor(): IProfileInteractor = ProfileInteractor()

}