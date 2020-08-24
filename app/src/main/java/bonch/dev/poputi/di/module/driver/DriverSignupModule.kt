package bonch.dev.poputi.di.module.driver

import bonch.dev.poputi.data.repository.common.media.IMediaRepository
import bonch.dev.poputi.data.repository.common.media.MediaRepository
import bonch.dev.poputi.data.repository.common.profile.IProfileRepository
import bonch.dev.poputi.data.repository.common.profile.ProfileRepository
import bonch.dev.poputi.data.repository.driver.signup.ISignupRepository
import bonch.dev.poputi.data.repository.driver.signup.SignupRepository
import bonch.dev.poputi.data.storage.common.profile.IProfileStorage
import bonch.dev.poputi.data.storage.common.profile.ProfileStorage
import bonch.dev.poputi.di.scope.CompScope
import bonch.dev.poputi.domain.interactor.driver.signup.ISignupInteractor
import bonch.dev.poputi.domain.interactor.driver.signup.SignupInteractor
import bonch.dev.poputi.presentation.modules.driver.signup.banking.presenter.BankingSelectPresenter
import bonch.dev.poputi.presentation.modules.driver.signup.banking.presenter.IBankingSelectPresenter
import bonch.dev.poputi.presentation.modules.driver.signup.carinfo.presenter.CarInfoPresenter
import bonch.dev.poputi.presentation.modules.driver.signup.carinfo.presenter.ICarInfoPresenter
import bonch.dev.poputi.presentation.modules.driver.signup.steps.presenter.ISignupStepPresenter
import bonch.dev.poputi.presentation.modules.driver.signup.steps.presenter.SignupStepPresenter
import bonch.dev.poputi.presentation.modules.driver.signup.suggest.presenter.ISuggestPresenter
import bonch.dev.poputi.presentation.modules.driver.signup.suggest.presenter.SuggestPresenter
import bonch.dev.poputi.presentation.modules.driver.signup.tabledocs.presenter.ITableDocsPresenter
import bonch.dev.poputi.presentation.modules.driver.signup.tabledocs.presenter.TableDocsPresenter
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
    fun provideBankingSelectPresenter(): IBankingSelectPresenter = BankingSelectPresenter()


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
    fun provideProfileStorage(): IProfileStorage = ProfileStorage()

}