package bonch.dev.di.module.driver

import bonch.dev.data.repository.common.media.IMediaRepository
import bonch.dev.data.repository.common.media.MediaRepository
import bonch.dev.data.repository.common.profile.IProfileRepository
import bonch.dev.data.repository.common.profile.ProfileRepository
import bonch.dev.data.repository.driver.signup.ISignupRepository
import bonch.dev.data.repository.driver.signup.SignupRepository
import bonch.dev.data.storage.common.profile.IProfileStorage
import bonch.dev.data.storage.common.profile.ProfileStorage
import bonch.dev.data.storage.driver.signup.ISignupStorage
import bonch.dev.data.storage.driver.signup.SignupStorage
import bonch.dev.di.scope.CompScope
import bonch.dev.domain.interactor.driver.signup.ISignupInteractor
import bonch.dev.domain.interactor.driver.signup.SignupInteractor
import bonch.dev.presentation.modules.driver.signup.carinfo.presenter.CarInfoPresenter
import bonch.dev.presentation.modules.driver.signup.carinfo.presenter.ICarInfoPresenter
import bonch.dev.presentation.modules.driver.signup.steps.presenter.ISignupStepPresenter
import bonch.dev.presentation.modules.driver.signup.steps.presenter.SignupStepPresenter
import bonch.dev.presentation.modules.driver.signup.suggest.presenter.ISuggestPresenter
import bonch.dev.presentation.modules.driver.signup.suggest.presenter.SuggestPresenter
import bonch.dev.presentation.modules.driver.signup.tabledocs.presenter.ITableDocsPresenter
import bonch.dev.presentation.modules.driver.signup.tabledocs.presenter.TableDocsPresenter
import dagger.Module
import dagger.Provides

@Module
class DriverSignupModule {

    @Provides
    @CompScope
    fun provideCarInfoPresenter(): ICarInfoPresenter = CarInfoPresenter()


    @Provides
    @CompScope
    fun provideSuggestPresenter(): ISuggestPresenter = SuggestPresenter()


    @Provides
    @CompScope
    fun provideSignupStepPresenter(): ISignupStepPresenter = SignupStepPresenter()


    @Provides
    @CompScope
    fun provideTableDocsPresenter(): ITableDocsPresenter = TableDocsPresenter()


    @Provides
    @CompScope
    fun provideSignupInteractor(): ISignupInteractor = SignupInteractor()


    @Provides
    @CompScope
    fun provideSignupRepository(): ISignupRepository = SignupRepository()


    @Provides
    @CompScope
    fun provideMediaRepository(): IMediaRepository = MediaRepository()


    @Provides
    @CompScope
    fun provideProfileRepository(): IProfileRepository = ProfileRepository()


    @Provides
    @CompScope
    fun provideSignupStorage(): ISignupStorage = SignupStorage()


    @Provides
    @CompScope
    fun provideProfileStorage(): IProfileStorage = ProfileStorage()

}