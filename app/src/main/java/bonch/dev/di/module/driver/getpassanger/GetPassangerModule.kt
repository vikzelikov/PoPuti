package bonch.dev.di.module.driver.getpassanger

import bonch.dev.di.scope.driver.OrdersScope
import bonch.dev.data.repository.driver.getpassanger.OrdersRepository
import bonch.dev.presentation.presenter.driver.getpassanger.OrdersPresenter
import bonch.dev.presentation.presenter.driver.getpassanger.adapters.OrdersAdapter
import dagger.Module
import dagger.Provides

@Module
class GetPassangerModule {

    @Provides
    @OrdersScope
    fun profideOrdersPresenter(): OrdersPresenter = OrdersPresenter()

    @Provides
    @OrdersScope
    fun profideOrdersModel(): OrdersRepository = OrdersRepository()


    @Provides
    fun providesOrdersAdapter(): OrdersAdapter = OrdersAdapter(list = ArrayList())
}