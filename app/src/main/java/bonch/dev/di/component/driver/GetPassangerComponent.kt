package bonch.dev.di.component.driver

import bonch.dev.di.component.AppComponent
import bonch.dev.di.module.driver.getpassanger.GetPassangerModule
import bonch.dev.di.scope.driver.OrdersScope
import bonch.dev.presentation.presenter.driver.getpassanger.OrdersPresenter
import bonch.dev.presentation.ui.driver.getpassanger.OrdersView
import dagger.Component

@OrdersScope
@Component(modules = [GetPassangerModule::class], dependencies = [AppComponent::class])
interface GetPassangerComponent {

    fun inject(view: OrdersView)

    fun inject(presenter: OrdersPresenter)
}