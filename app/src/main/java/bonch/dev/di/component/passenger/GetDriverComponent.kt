package bonch.dev.di.component.passenger

import bonch.dev.di.component.AppComponent
import bonch.dev.di.module.passenger.GetDriverModule
import bonch.dev.di.scope.CompScope
import bonch.dev.domain.interactor.passenger.getdriver.GetDriverInteractor
import bonch.dev.presentation.modules.common.addbanking.presenter.AddBankCardPresenter
import bonch.dev.presentation.modules.common.addbanking.view.AddBankCardView
import bonch.dev.presentation.modules.passenger.getdriver.ride.presenter.*
import bonch.dev.presentation.modules.passenger.getdriver.ride.view.*
import dagger.Component

@CompScope
@Component(modules = [GetDriverModule::class], dependencies = [AppComponent::class])
interface GetDriverComponent {

    //yandex map create ride
    fun inject(target: MapCreateRideView)

    fun inject(target: MapCreateRidePresenter)


    //yandex map get driver
    fun inject(target: MapGetDriverView)


    //create ride
    fun inject(target: CreateRideView)

    fun inject(target: CreateRidePresenter)


    //detail ride
    fun inject(target: DetailRideView)

    fun inject(target: DetailRidePresenter)


    //add bank card
    fun inject(target: AddBankCardView)

    fun inject(target: AddBankCardPresenter)


    //get driver
    fun inject(target: GetDriverView)

    fun inject(target: GetDriverPresenter)


    //track ride
    fun inject(target: TrackRideView)

    fun inject(target: TrackRidePresenter)


    //interactors
    fun inject(target: GetDriverInteractor)

}