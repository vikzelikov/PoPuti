package bonch.dev.poputi.di.component.driver

import bonch.dev.poputi.di.component.AppComponent
import bonch.dev.poputi.di.module.driver.DriverSignupModule
import bonch.dev.poputi.di.scope.CompScope
import bonch.dev.poputi.domain.interactor.driver.signup.SignupInteractor
import bonch.dev.poputi.presentation.modules.driver.signup.DriverSignupActivity
import bonch.dev.poputi.presentation.modules.driver.signup.carinfo.view.CarInfoView
import bonch.dev.poputi.presentation.modules.driver.signup.steps.presenter.SignupStepPresenter
import bonch.dev.poputi.presentation.modules.driver.signup.steps.view.SignupStepView
import bonch.dev.poputi.presentation.modules.driver.signup.suggest.view.SuggestView
import bonch.dev.poputi.presentation.modules.driver.signup.tabledocs.presenter.TableDocsPresenter
import bonch.dev.poputi.presentation.modules.driver.signup.tabledocs.view.TableDocsView
import dagger.Component

@CompScope
@Component(modules = [DriverSignupModule::class], dependencies = [AppComponent::class])
interface DriverSignupComponent {

    fun inject(taget: DriverSignupActivity)

    fun inject(target: CarInfoView)

    fun inject(target: SuggestView)

    fun inject(target: SignupStepView)

    fun inject(target: TableDocsView)

    fun inject(target: SignupStepPresenter)

    fun inject(target: TableDocsPresenter)

    fun inject(target: SignupInteractor)

}