package bonch.dev.poputi.di.component.passenger

import bonch.dev.poputi.di.component.AppComponent
import bonch.dev.poputi.di.module.passenger.RegularRidesModule
import bonch.dev.poputi.di.scope.CompScope
import bonch.dev.poputi.domain.interactor.passenger.regular.ride.RegularRidesInteractor
import bonch.dev.poputi.presentation.modules.passenger.regular.ride.presenter.*
import bonch.dev.poputi.presentation.modules.passenger.regular.ride.view.*
import dagger.Component


@CompScope
@Component(modules = [RegularRidesModule::class], dependencies = [AppComponent::class])
interface RegularDriveComponent {

    //main regular drive
    fun inject(target: RegularRidesView)

    fun inject(target: RegularRidesPresenter)


    //yandex map create regular drive
    fun inject(target: MapCreateRegularRide)

    fun inject(target: MapCreateRegRidePresenter)


    //create regular drive
    fun inject(target: CreateRegularRideView)

    fun inject(target: CreateRegularRidePresenter)


    //active rides list
    fun inject(target: ActiveRidesView)
    fun inject(target: ActiveRidesPresenter)


    //archive rides list
    fun inject(target: ArchiveRidesView)
    fun inject(target: ArchiveRidesPresenter)


    //interactor
    fun inject(target: RegularRidesInteractor)


}