package bonch.dev.di.module.driver

import bonch.dev.di.scope.CompScope
import bonch.dev.data.repository.driver.getpassanger.OrdersRepository
import bonch.dev.presentation.modules.driver.getpassanger.presenter.OrdersPresenter
import bonch.dev.presentation.modules.driver.getpassanger.adapters.OrdersAdapter
import bonch.dev.presentation.modules.driver.getpassanger.presenter.ContractPresenter
import dagger.Module
import dagger.Provides

@Module
class GetPassangerModule {

    @Provides
    @CompScope
    fun profideOrdersPresenter(): ContractPresenter.IOrdersPresenter = OrdersPresenter()


}