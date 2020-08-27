package bonch.dev.poputi.di.component.driver

import bonch.dev.poputi.di.component.AppComponent
import bonch.dev.poputi.di.module.driver.RatingModule
import bonch.dev.poputi.di.scope.CompScope
import bonch.dev.poputi.domain.interactor.driver.rating.RatingInteractor
import bonch.dev.poputi.presentation.modules.driver.rating.presenter.RatingPresenter
import bonch.dev.poputi.presentation.modules.driver.rating.view.RatingView
import dagger.Component


@CompScope
@Component(modules = [RatingModule::class], dependencies = [AppComponent::class])
interface RatingComponent {

    fun inject(target: RatingView)

    fun inject(target: RatingPresenter)

    fun inject(target: RatingInteractor)

}