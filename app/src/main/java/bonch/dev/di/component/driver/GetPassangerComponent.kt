package bonch.dev.di.component.driver

import bonch.dev.di.component.AppComponent
import bonch.dev.di.module.driver.GetPassangerModule
import bonch.dev.di.scope.OrdersScope
import bonch.dev.presentation.modules.driver.getpassanger.orders.presenter.OrdersPresenter
import bonch.dev.presentation.modules.driver.getpassanger.orders.view.OrdersView
import dagger.Component

@OrdersScope
@Component(modules = [GetPassangerModule::class], dependencies = [AppComponent::class])
interface GetPassangerComponent {

    fun inject(view: OrdersView)

    fun inject(presenter: OrdersPresenter)
}