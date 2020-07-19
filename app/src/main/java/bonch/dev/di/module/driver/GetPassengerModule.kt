package bonch.dev.di.module.driver

import bonch.dev.data.repository.common.chat.ChatRepository
import bonch.dev.data.repository.common.chat.IChatRepository
import bonch.dev.data.repository.driver.getpassenger.GetPassengerRepository
import bonch.dev.data.repository.driver.getpassenger.IGetPassengerRepository
import bonch.dev.data.storage.common.profile.IProfileStorage
import bonch.dev.data.storage.common.profile.ProfileStorage
import bonch.dev.data.storage.driver.getpassenger.GetPassengerStorage
import bonch.dev.data.storage.driver.getpassenger.IGetPassengerStorage
import bonch.dev.di.scope.CompScope
import bonch.dev.domain.interactor.driver.getpassenger.GetPassengerInteractor
import bonch.dev.domain.interactor.driver.getpassenger.IGetPassengerInteractor
import bonch.dev.presentation.modules.driver.getpassenger.presenter.*
import dagger.Module
import dagger.Provides

@Module
class GetPassengerModule {

    @Provides
    @CompScope
    fun provideOrdersPresenter(): ContractPresenter.IOrdersPresenter = OrdersPresenter()


    @Provides
    @CompScope
    fun provideDetailOrderPresenter(): ContractPresenter.IDetailOrderPresenter = DetailOrderPresenter()


    @Provides
    @CompScope
    fun provideTrackRidePresenter(): ContractPresenter.ITrackRidePresenter = TrackRidePresenter()


    @Provides
    @CompScope
    fun provideMapOrderPresenter(): ContractPresenter.IMapOrderPresenter = MapOrderPresenter()


    @Provides
    @CompScope
    fun provideGetPassangerInteractor(): IGetPassengerInteractor = GetPassengerInteractor()


    @Provides
    @CompScope
    fun provideGetPassangerRepository(): IGetPassengerRepository = GetPassengerRepository()


    @Provides
    @CompScope
    fun provideChatRepository(): IChatRepository = ChatRepository()


    @Provides
    @CompScope
    fun provideProfileStorage(): IProfileStorage = ProfileStorage()


    @Provides
    @CompScope
    fun provideGetPassengerStorage(): IGetPassengerStorage = GetPassengerStorage()


}