package bonch.dev.di.module.driver

import bonch.dev.di.scope.CompScope
import bonch.dev.data.repository.driver.getpassanger.OrdersRepository
import bonch.dev.presentation.modules.driver.getpassanger.orders.presenter.OrdersPresenter
import bonch.dev.presentation.modules.driver.getpassanger.orders.adapters.OrdersAdapter
import dagger.Module
import dagger.Provides

@Module
class GetPassangerModule {

    @Provides
    @CompScope
    fun profideOrdersPresenter(): OrdersPresenter = OrdersPresenter()

    @Provides
    @CompScope
    fun profideOrdersModel(): OrdersRepository = OrdersRepository()


    @Provides
    fun providesOrdersAdapter(): OrdersAdapter = OrdersAdapter(list = ArrayList())
}