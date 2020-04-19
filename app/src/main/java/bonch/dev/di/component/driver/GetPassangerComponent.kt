package bonch.dev.di.component.driver

import bonch.dev.di.module.driver.getpassanger.GetPassangerModule
import bonch.dev.di.scope.driver.OrdersScope
import bonch.dev.view.driver.getpassanger.OrdersView
import dagger.Component

@OrdersScope
@Component(modules = [GetPassangerModule::class])
interface GetPassangerComponent {
    fun inject(view: OrdersView)
}