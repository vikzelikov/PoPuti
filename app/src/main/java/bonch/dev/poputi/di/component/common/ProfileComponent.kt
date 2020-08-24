package bonch.dev.poputi.di.component.common

import bonch.dev.poputi.di.component.AppComponent
import bonch.dev.poputi.di.module.common.ProfileModule
import bonch.dev.poputi.di.scope.CompScope
import bonch.dev.poputi.domain.interactor.common.profile.ProfileInteractor
import bonch.dev.poputi.presentation.modules.common.profile.banking.BankingPresenter
import bonch.dev.poputi.presentation.modules.common.profile.banking.BankingView
import bonch.dev.poputi.presentation.modules.common.profile.me.presenter.ProfileDetailPresenter
import bonch.dev.poputi.presentation.modules.common.profile.me.view.ProfileDetailView
import bonch.dev.poputi.presentation.modules.common.profile.menu.presenter.ProfilePresenter
import bonch.dev.poputi.presentation.modules.common.profile.menu.view.ProfileView
import dagger.Component

@CompScope
@Component(modules = [ProfileModule::class], dependencies = [AppComponent::class])
interface ProfileComponent {

    //menu
    fun inject(target: ProfileView)
    fun inject(target: ProfilePresenter)


    //me
    fun inject(target: ProfileDetailView)
    fun inject(target: ProfileDetailPresenter)


    //banking
    fun inject(target: BankingView)
    fun inject(target: BankingPresenter)



    //common
    fun inject(target: ProfileInteractor)


}