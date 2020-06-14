package bonch.dev.di.component.passenger

import bonch.dev.di.component.AppComponent
import bonch.dev.di.module.passenger.PassengerSignupModule
import bonch.dev.di.scope.CompScope
import bonch.dev.domain.interactor.passenger.signup.SignupInteractor
import bonch.dev.presentation.modules.passenger.signup.presenter.ConfirmPhonePresenter
import bonch.dev.presentation.modules.passenger.signup.presenter.FullNamePresenter
import bonch.dev.presentation.modules.passenger.signup.presenter.PhonePresenter
import bonch.dev.presentation.modules.passenger.signup.view.ConfirmPhoneView
import bonch.dev.presentation.modules.passenger.signup.view.FullNameView
import bonch.dev.presentation.modules.passenger.signup.view.PhoneView
import dagger.Component

@CompScope
@Component(modules = [PassengerSignupModule::class], dependencies = [AppComponent::class])
interface PassengerSignupComponent {

    //Views
    fun inject(target: PhoneView)

    fun inject(target: ConfirmPhoneView)

    fun inject(target: FullNameView)

    //Presenters
    fun inject(target: PhonePresenter)

    fun inject(target: ConfirmPhonePresenter)

    fun inject(target: FullNamePresenter)

    //Interactors
    fun inject(target: SignupInteractor)

}