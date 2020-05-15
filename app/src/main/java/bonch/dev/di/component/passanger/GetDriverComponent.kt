package bonch.dev.di.component.passanger

import bonch.dev.di.component.AppComponent
import bonch.dev.di.module.passanger.GetDriverModule
import bonch.dev.di.scope.CompScope
import bonch.dev.domain.interactor.passanger.getdriver.GetDriverInteractor
import bonch.dev.presentation.modules.passanger.getdriver.addcard.presenter.AddBankCardPresenter
import bonch.dev.presentation.modules.passanger.getdriver.addcard.view.AddBankCardView
import bonch.dev.presentation.modules.common.orfferprice.presenter.OfferPricePresenter
import bonch.dev.presentation.modules.common.orfferprice.view.OfferPriceView
import bonch.dev.presentation.modules.passanger.getdriver.ride.presenter.*
import bonch.dev.presentation.modules.passanger.getdriver.ride.view.*
import dagger.Component

@CompScope
@Component(modules = [GetDriverModule::class], dependencies = [AppComponent::class])
interface GetDriverComponent {

    //views
    fun inject(target: MapCreateRideView)

    fun inject(target: CreateRideView)

    fun inject(target: DetailRideView)

    fun inject(target: AddBankCardView)

    fun inject(target: MapGetDriverView)

    fun inject(target: GetDriverView)

    fun inject(target: TrackRideView)

    //presenters
    fun inject(target: MapCreateRidePresenter)

    fun inject(target: CreateRidePresenter)

    fun inject(target: DetailRidePresenter)

    fun inject(target: AddBankCardPresenter)

    fun inject(target: GetDriverPresenter)

    fun inject(target: TrackRidePresenter)

    //interactors
    fun inject(target: GetDriverInteractor)

}