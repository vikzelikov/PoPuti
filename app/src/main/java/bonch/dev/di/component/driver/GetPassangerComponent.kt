package bonch.dev.di.component.driver

import bonch.dev.di.component.AppComponent
import bonch.dev.di.module.driver.GetPassangerModule
import bonch.dev.di.scope.CompScope
import bonch.dev.presentation.modules.driver.getpassanger.presenter.OrdersPresenter
import bonch.dev.presentation.modules.driver.getpassanger.view.OrdersView
import dagger.Component

@CompScope
@Component(modules = [GetPassangerModule::class], dependencies = [AppComponent::class])
interface GetPassangerComponent {

    //orders
    fun inject(view: OrdersView)

    fun inject(presenter: OrdersPresenter)

}