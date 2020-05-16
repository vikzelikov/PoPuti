package bonch.dev.di.component.passanger

import bonch.dev.di.component.AppComponent
import bonch.dev.di.module.passanger.PassangerSignupModule
import bonch.dev.di.scope.CompScope
import bonch.dev.domain.interactor.passanger.signup.SignupInteractor
import bonch.dev.presentation.modules.passanger.signup.presenter.ConfirmPhonePresenter
import bonch.dev.presentation.modules.passanger.signup.presenter.FullNamePresenter
import bonch.dev.presentation.modules.passanger.signup.presenter.PhonePresenter
import bonch.dev.presentation.modules.passanger.signup.view.ConfirmPhoneView
import bonch.dev.presentation.modules.passanger.signup.view.FullNameView
import bonch.dev.presentation.modules.passanger.signup.view.PhoneView
import dagger.Component

@CompScope
@Component(modules = [PassangerSignupModule::class], dependencies = [AppComponent::class])
interface PassangerSignupComponent {

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