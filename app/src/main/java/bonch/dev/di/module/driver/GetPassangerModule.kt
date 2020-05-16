package bonch.dev.di.module.driver

import bonch.dev.data.repository.driver.getpassanger.GetPassangerRepository
import bonch.dev.data.repository.driver.getpassanger.IGetPassangerRepository
import bonch.dev.di.scope.CompScope
import bonch.dev.domain.interactor.driver.getpassanger.GetPassangerInteractor
import bonch.dev.domain.interactor.driver.getpassanger.IGetPassangerInteractor
import bonch.dev.presentation.modules.driver.getpassanger.presenter.OrdersPresenter
import bonch.dev.presentation.modules.driver.getpassanger.presenter.ContractPresenter
import dagger.Module
import dagger.Provides

@Module
class GetPassangerModule {

    @Provides
    @CompScope
    fun provideOrdersPresenter(): ContractPresenter.IOrdersPresenter = OrdersPresenter()

    @Provides
    @CompScope
    fun provideGetPassangerInteractor(): IGetPassangerInteractor = GetPassangerInteractor()

    @Provides
    @CompScope
    fun provideGetPassangerRepository(): IGetPassangerRepository = GetPassangerRepository()


}