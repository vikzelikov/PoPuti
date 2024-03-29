package bonch.dev.poputi.di.component.common

import bonch.dev.poputi.di.component.AppComponent
import bonch.dev.poputi.di.module.common.ProfileModule
import bonch.dev.poputi.di.scope.CompScope
import bonch.dev.poputi.domain.interactor.common.profile.ProfileInteractor
import bonch.dev.poputi.presentation.modules.common.profile.banking.BankingPresenter
import bonch.dev.poputi.presentation.modules.common.profile.banking.BankingView
import bonch.dev.poputi.presentation.modules.common.profile.city.SelectCityPresenter
import bonch.dev.poputi.presentation.modules.common.profile.city.SelectCityView
import bonch.dev.poputi.presentation.modules.common.profile.driver.carinfo.CarInfoPresenter
import bonch.dev.poputi.presentation.modules.common.profile.driver.carinfo.CarInfoView
import bonch.dev.poputi.presentation.modules.common.profile.driver.profits.ProfitsPresenter
import bonch.dev.poputi.presentation.modules.common.profile.driver.profits.ProfitsView
import bonch.dev.poputi.presentation.modules.common.profile.language.ChangeLangPresenter
import bonch.dev.poputi.presentation.modules.common.profile.language.ChangeLangView
import bonch.dev.poputi.presentation.modules.common.profile.me.presenter.ProfileDetailPresenter
import bonch.dev.poputi.presentation.modules.common.profile.me.view.ProfileDetailView
import bonch.dev.poputi.presentation.modules.common.profile.menu.presenter.ProfilePresenter
import bonch.dev.poputi.presentation.modules.common.profile.menu.view.ProfileView
import bonch.dev.poputi.presentation.modules.common.profile.passenger.rating.RatingPresenter
import bonch.dev.poputi.presentation.modules.common.profile.passenger.rating.RatingView
import bonch.dev.poputi.presentation.modules.common.profile.passenger.verification.VerifyPresenter
import bonch.dev.poputi.presentation.modules.common.profile.passenger.verification.VerifyView
import bonch.dev.poputi.presentation.modules.common.profile.story.presenter.DetailStoryPresenter
import bonch.dev.poputi.presentation.modules.common.profile.story.presenter.StoryPresenter
import bonch.dev.poputi.presentation.modules.common.profile.story.view.DetailStoryView
import bonch.dev.poputi.presentation.modules.common.profile.story.view.StoryView
import bonch.dev.poputi.presentation.modules.common.profile.support.SupportPresenter
import bonch.dev.poputi.presentation.modules.common.profile.support.SupportView
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


    //verification
    fun inject(target: VerifyView)
    fun inject(target: VerifyPresenter)


    //profits
    fun inject(target: ProfitsView)
    fun inject(target: ProfitsPresenter)


    //story
    fun inject(target: StoryView)
    fun inject(target: StoryPresenter)


    //detail story
    fun inject(target: DetailStoryView)
    fun inject(target: DetailStoryPresenter)


    //rating
    fun inject(target: RatingView)
    fun inject(target: RatingPresenter)


    //select city
    fun inject(target: SelectCityView)
    fun inject(target: SelectCityPresenter)


    //car info
    fun inject(target: CarInfoView)
    fun inject(target: CarInfoPresenter)


    //change lang
    fun inject(target: ChangeLangView)
    fun inject(target: ChangeLangPresenter)


    //support
    fun inject(target: SupportView)
    fun inject(target: SupportPresenter)


    //common
    fun inject(target: ProfileInteractor)


}