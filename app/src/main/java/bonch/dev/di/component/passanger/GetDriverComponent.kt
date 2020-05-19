package bonch.dev.di.component.passanger

import bonch.dev.di.component.AppComponent
import bonch.dev.di.module.passanger.GetDriverModule
import bonch.dev.di.scope.CompScope
import bonch.dev.domain.interactor.passanger.getdriver.GetDriverInteractor
import bonch.dev.presentation.modules.passanger.getdriver.addcard.presenter.AddBankCardPresenter
import bonch.dev.presentation.modules.passanger.getdriver.addcard.view.AddBankCardView
import bonch.dev.presentation.modules.passanger.getdriver.ride.presenter.*
import bonch.dev.presentation.modules.passanger.getdriver.ride.view.*
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