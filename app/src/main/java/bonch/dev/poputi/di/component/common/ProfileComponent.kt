package bonch.dev.poputi.di.component.common

import bonch.dev.poputi.di.component.AppComponent
import bonch.dev.poputi.di.module.common.ProfileModule
import bonch.dev.poputi.di.scope.CompScope
import bonch.dev.poputi.domain.interactor.common.profile.ProfileInteractor
import bonch.dev.poputi.presentation.modules.common.profile.presenter.ProfileDetailPresenter
import bonch.dev.poputi.presentation.modules.common.profile.presenter.ProfilePresenter
import bonch.dev.poputi.presentation.modules.common.profile.view.ProfileDetailView
import bonch.dev.poputi.presentation.modules.common.profile.view.ProfileView
import dagger.Component

@CompScope
@Component(modules = [ProfileModule::class], dependencies = [AppComponent::class])
interface ProfileComponent {

    fun inject(target: ProfileView)

    fun inject(target: ProfileDetailView)

    fun inject(target: ProfilePresenter)

    fun inject(target: ProfileDetailPresenter)

    fun inject(target: ProfileInteractor)
}