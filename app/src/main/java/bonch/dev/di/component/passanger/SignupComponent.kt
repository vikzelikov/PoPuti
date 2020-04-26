package bonch.dev.di.component.passanger

import androidx.navigation.NavController
import bonch.dev.di.component.AppComponent
import bonch.dev.di.module.passanger.SignupModule
import bonch.dev.di.scope.OrdersScope
import bonch.dev.domain.interactor.passanger.signup.SignupInteractor
import bonch.dev.presentation.modules.passanger.signup.presenter.ConfirmPhonePresenter
import bonch.dev.presentation.modules.passanger.signup.presenter.FullNamePresenter
import bonch.dev.presentation.modules.passanger.signup.presenter.PhonePresenter
import bonch.dev.presentation.modules.passanger.signup.view.ConfirmPhoneView
import bonch.dev.presentation.modules.passanger.signup.view.FullNameView
import bonch.dev.presentation.modules.passanger.signup.view.PhoneView
import dagger.Component

@OrdersScope
@Component(modules = [SignupModule::class], dependencies = [AppComponent::class])
interface SignupComponent {

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