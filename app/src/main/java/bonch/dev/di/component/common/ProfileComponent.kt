package bonch.dev.di.component.common

import bonch.dev.di.component.AppComponent
import bonch.dev.di.module.common.ProfileModule
import bonch.dev.di.scope.CompScope
import bonch.dev.domain.interactor.common.profile.ProfileInteractor
import bonch.dev.presentation.modules.common.profile.presenter.ProfileDetailPresenter
import bonch.dev.presentation.modules.common.profile.presenter.ProfilePresenter
import bonch.dev.presentation.modules.common.profile.view.ProfileDetailView
import bonch.dev.presentation.modules.common.profile.view.ProfileView
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