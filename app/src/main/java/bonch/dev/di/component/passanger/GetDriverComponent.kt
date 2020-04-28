package bonch.dev.di.component.passanger

import bonch.dev.di.component.AppComponent
import bonch.dev.di.module.passanger.GetDriverModule
import bonch.dev.di.scope.CompScope
import bonch.dev.presentation.modules.passanger.getdriver.ride.presenter.CreateRidePresenter
import bonch.dev.presentation.modules.passanger.getdriver.ride.presenter.MapPresenter
import bonch.dev.presentation.modules.passanger.getdriver.ride.view.CreateRideView
import bonch.dev.presentation.modules.passanger.getdriver.ride.view.DetailRideView
import bonch.dev.presentation.modules.passanger.getdriver.ride.view.MapView
import dagger.Component

@CompScope
@Component(modules = [GetDriverModule::class], dependencies = [AppComponent::class])
interface GetDriverComponent {
    //views
    fun inject(target: MapView)

    fun inject(target: CreateRideView)

    fun inject(target: DetailRideView)

    fun inject(target: MapPresenter)

    fun inject(target: CreateRidePresenter)
}