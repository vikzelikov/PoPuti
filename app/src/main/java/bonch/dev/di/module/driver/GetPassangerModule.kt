package bonch.dev.di.module.driver

import bonch.dev.data.repository.driver.getpassanger.GetPassangerRepository
import bonch.dev.data.repository.driver.getpassanger.IGetPassangerRepository
import bonch.dev.di.scope.CompScope
import bonch.dev.domain.interactor.driver.getpassanger.GetPassangerInteractor
import bonch.dev.domain.interactor.driver.getpassanger.IGetPassangerInteractor
import bonch.dev.presentation.modules.driver.getpassanger.presenter.*
import dagger.Module
import dagger.Provides

@Module
class GetPassangerModule {

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
    fun provideGetPassangerInteractor(): IGetPassangerInteractor = GetPassangerInteractor()

    @Provides
    @CompScope
    fun provideGetPassangerRepository(): IGetPassangerRepository = GetPassangerRepository()

    @Provides
    @CompScope
    fun provideMapOrderPresenter(): ContractPresenter.IMapOrderPresenter = MapOrderPresenter()


}