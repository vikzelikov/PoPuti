package bonch.dev.di.module.driver

import bonch.dev.data.repository.driver.getpassenger.GetPassangerRepository
import bonch.dev.data.repository.driver.getpassenger.IGetPassangerRepository
import bonch.dev.data.storage.common.profile.IProfileStorage
import bonch.dev.data.storage.common.profile.ProfileStorage
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
    fun provideGetPassangerRepository(): IGetPassangerRepository = GetPassangerRepository()

    @Provides
    @CompScope
    fun provideProfileStorage(): IProfileStorage = ProfileStorage()


}