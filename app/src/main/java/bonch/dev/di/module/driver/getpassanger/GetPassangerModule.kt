package bonch.dev.di.module.driver.getpassanger

import bonch.dev.di.scope.driver.OrdersScope
import bonch.dev.presenter.driver.getpassanger.OrdersPresenter
import dagger.Module
import dagger.Provides

@Module
class GetPassangerModule {

    @Provides
    @OrdersScope
    fun profideOrdersPresenter() : OrdersPresenter = OrdersPresenter()
}