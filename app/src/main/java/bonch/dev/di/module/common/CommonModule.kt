package bonch.dev.di.module.common

import bonch.dev.data.repository.common.chat.ChatRepository
import bonch.dev.data.repository.common.chat.IChatRepository
import bonch.dev.di.scope.CompScope
import bonch.dev.domain.interactor.common.chat.ChatInteractor
import bonch.dev.domain.interactor.common.chat.IChatInteractor
import bonch.dev.domain.interactor.common.offerprice.IOfferPriceInteractor
import bonch.dev.domain.interactor.common.offerprice.OfferPriceInteractor
import bonch.dev.domain.interactor.common.rate.IRateRideInteractor
import bonch.dev.domain.interactor.common.rate.RateRideInteractor
import bonch.dev.presentation.modules.common.chat.presenter.ChatPresenter
import bonch.dev.presentation.modules.common.chat.presenter.IChatPresenter
import bonch.dev.presentation.modules.common.ride.orfferprice.presenter.IOfferPricePresenter
import bonch.dev.presentation.modules.common.ride.orfferprice.presenter.OfferPricePresenter
import bonch.dev.presentation.modules.common.ride.rate.presenter.IRateRidePresenter
import bonch.dev.presentation.modules.common.ride.rate.presenter.RateRidePresenter
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

    @Provides
    @CompScope
    fun provideOfferPriceInteractor(): IOfferPriceInteractor = OfferPriceInteractor()

}