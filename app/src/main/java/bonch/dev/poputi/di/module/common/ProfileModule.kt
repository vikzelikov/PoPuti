package bonch.dev.poputi.di.module.common

import bonch.dev.poputi.data.repository.common.media.IMediaRepository
import bonch.dev.poputi.data.repository.common.media.MediaRepository
import bonch.dev.poputi.data.repository.common.profile.IProfileRepository
import bonch.dev.poputi.data.repository.common.profile.ProfileRepository
import bonch.dev.poputi.data.repository.common.rate.IRateRideRepository
import bonch.dev.poputi.data.repository.common.rate.RateRideRepository
import bonch.dev.poputi.data.storage.common.profile.IProfileStorage
import bonch.dev.poputi.data.storage.common.profile.ProfileStorage
import bonch.dev.poputi.di.scope.CompScope
import bonch.dev.poputi.domain.interactor.common.profile.IProfileInteractor
import bonch.dev.poputi.domain.interactor.common.profile.ProfileInteractor
import bonch.dev.poputi.presentation.modules.common.profile.ContractPresenter
import bonch.dev.poputi.presentation.modules.common.profile.banking.BankingPresenter
import bonch.dev.poputi.presentation.modules.common.profile.city.SelectCityPresenter
import bonch.dev.poputi.presentation.modules.common.profile.driver.carinfo.CarInfoPresenter
import bonch.dev.poputi.presentation.modules.common.profile.driver.profits.ProfitsPresenter
import bonch.dev.poputi.presentation.modules.common.profile.language.ChangeLangPresenter
import bonch.dev.poputi.presentation.modules.common.profile.me.presenter.IProfileDetailPresenter
import bonch.dev.poputi.presentation.modules.common.profile.me.presenter.ProfileDetailPresenter
import bonch.dev.poputi.presentation.modules.common.profile.menu.presenter.IProfilePresenter
import bonch.dev.poputi.presentation.modules.common.profile.menu.presenter.ProfilePresenter
import bonch.dev.poputi.presentation.modules.common.profile.passenger.rating.RatingPresenter
import bonch.dev.poputi.presentation.modules.common.profile.passenger.verification.VerifyPresenter
import bonch.dev.poputi.presentation.modules.common.profile.story.presenter.DetailStoryPresenter
import bonch.dev.poputi.presentation.modules.common.profile.story.presenter.StoryPresenter
import bonch.dev.poputi.presentation.modules.common.profile.support.SupportPresenter
import dagger.Module
import dagger.Provides

@Module
class ProfileModule {

    @Provides
    @CompScope
    fun provideProfilePresenter(): IProfilePresenter = ProfilePresenter()

    @Provides
    @CompScope
    fun provideProfileDetailPresenter(): IProfileDetailPresenter = ProfileDetailPresenter()

    @Provides
    @CompScope
    fun provideBankingPresenter(): ContractPresenter.IBankingPresenter = BankingPresenter()

    @Provides
    @CompScope
    fun provideVerifyPresenter(): ContractPresenter.IVerifyPresenter = VerifyPresenter()

    @Provides
    @CompScope
    fun provideProfitsPresenter(): ContractPresenter.IProfitsPresenter = ProfitsPresenter()

    @Provides
    @CompScope
    fun provideStoryPresenter(): ContractPresenter.IStoryPresenter = StoryPresenter()

    @Provides
    @CompScope
    fun provideDetailStoryPresenter(): ContractPresenter.IDetailStoryPresenter =
        DetailStoryPresenter()

    @Provides
    @CompScope
    fun provideRatingPresenter(): ContractPresenter.IRatingPresenter = RatingPresenter()

    @Provides
    @CompScope
    fun provideSelectCityPresenter(): ContractPresenter.ISelectCityPresenter = SelectCityPresenter()

    @Provides
    @CompScope
    fun provideCarInfoPresenter(): ContractPresenter.ICarInfoPresenter = CarInfoPresenter()

    @Provides
    @CompScope
    fun provideChangeLangPresenter(): ContractPresenter.IChangeLangPresenter = ChangeLangPresenter()

    @Provides
    @CompScope
    fun provideSupportPresenter(): ContractPresenter.ISupportPresenter = SupportPresenter()

    @Provides
    @CompScope
    fun provideProfileInteractor(): IProfileInteractor = ProfileInteractor()

    @Provides
    @CompScope
    fun provideProfileStorage(): IProfileStorage = ProfileStorage()

    @Provides
    @CompScope
    fun provideProfileRepository(): IProfileRepository = ProfileRepository()

    @Provides
    @CompScope
    fun provideMediaRepository(): IMediaRepository = MediaRepository()

    @Provides
    @CompScope
    fun providesRateRideRepository(): IRateRideRepository = RateRideRepository()

}