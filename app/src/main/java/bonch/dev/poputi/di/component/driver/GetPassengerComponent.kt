package bonch.dev.poputi.di.component.driver

import bonch.dev.poputi.di.component.AppComponent
import bonch.dev.poputi.di.module.driver.GetPassengerModule
import bonch.dev.poputi.di.scope.CompScope
import bonch.dev.poputi.domain.interactor.driver.getpassenger.GetPassengerInteractor
import bonch.dev.poputi.presentation.modules.driver.getpassenger.presenter.DetailOrderPresenter
import bonch.dev.poputi.presentation.modules.driver.getpassenger.presenter.OrdersPresenter
import bonch.dev.poputi.presentation.modules.driver.getpassenger.presenter.TrackRidePresenter
import bonch.dev.poputi.presentation.modules.driver.getpassenger.view.DetailOrderView
import bonch.dev.poputi.presentation.modules.driver.getpassenger.view.MapOrderView
import bonch.dev.poputi.presentation.modules.driver.getpassenger.view.OrdersView
import bonch.dev.poputi.presentation.modules.driver.getpassenger.view.TrackRideView
import dagger.Component

@CompScope
@Component(modules = [GetPassengerModule::class], dependencies = [AppComponent::class])
interface GetPassengerComponent {

    //orders
    fun inject(target: OrdersView)

    fun inject(target: OrdersPresenter)


    //yandex map
    fun inject(target: MapOrderView)


    //detail order
    fun inject(target: DetailOrderView)

    fun inject(target: DetailOrderPresenter)


    //bg_seekbar_track ride
    fun inject(target: TrackRideView)

    fun inject(target: TrackRidePresenter)


    //interactors
    fun inject(target: GetPassengerInteractor)

}