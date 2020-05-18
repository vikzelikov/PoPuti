package bonch.dev.di.component.driver

import bonch.dev.di.component.AppComponent
import bonch.dev.di.module.driver.GetPassangerModule
import bonch.dev.di.scope.CompScope
import bonch.dev.domain.interactor.driver.getpassanger.GetPassangerInteractor
import bonch.dev.presentation.modules.driver.getpassanger.presenter.DetailOrderPresenter
import bonch.dev.presentation.modules.driver.getpassanger.presenter.OrdersPresenter
import bonch.dev.presentation.modules.driver.getpassanger.presenter.TrackRidePresenter
import bonch.dev.presentation.modules.driver.getpassanger.view.DetailOrderView
import bonch.dev.presentation.modules.driver.getpassanger.view.MapOrderView
import bonch.dev.presentation.modules.driver.getpassanger.view.OrdersView
import bonch.dev.presentation.modules.driver.getpassanger.view.TrackRideView
import dagger.Component

@CompScope
@Component(modules = [GetPassangerModule::class], dependencies = [AppComponent::class])
interface GetPassangerComponent {

    //orders
    fun inject(target: OrdersView)

    fun inject(target: OrdersPresenter)


    //yandex map
    fun inject(target: MapOrderView)


    //detail order
    fun inject(target: DetailOrderView)

    fun inject(target: DetailOrderPresenter)


    //track ride
    fun inject(target: TrackRideView)

    fun inject(target: TrackRidePresenter)


    //interactors
    fun inject(target: GetPassangerInteractor)

}