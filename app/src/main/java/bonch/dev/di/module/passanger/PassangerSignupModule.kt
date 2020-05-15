package bonch.dev.di.module.passanger

import bonch.dev.data.repository.common.profile.IProfileRepository
import bonch.dev.data.repository.common.profile.ProfileRepository
import bonch.dev.data.repository.passanger.signup.ISignupRepository
import bonch.dev.data.repository.passanger.signup.SignupRepository
import bonch.dev.data.storage.common.profile.IProfileStorage
import bonch.dev.data.storage.common.profile.ProfileStorage
import bonch.dev.domain.interactor.passanger.signup.ISignupInteractor
import bonch.dev.domain.interactor.passanger.signup.SignupInteractor
import bonch.dev.presentation.modules.passanger.signup.presenter.ConfirmPhonePresenter
import bonch.dev.presentation.modules.passanger.signup.presenter.ContractPresenter
import bonch.dev.presentation.modules.passanger.signup.presenter.FullNamePresenter
import bonch.dev.presentation.modules.passanger.signup.presenter.PhonePresenter
import dagger.Module
import dagger.Provides


@Module
class PassangerSignupModule {

    @Provides
    fun providePhonePresenter(): ContractPresenter.IPhonePresenter =
        PhonePresenter()


    @Provides
    fun provideConfirmPhonePresenter(): ContractPresenter.IConfirmPhonePresenter =
        ConfirmPhonePresenter()


    @Provides
    fun provideFullNamePresenter(): ContractPresenter.IFullNamePresenter =
        FullNamePresenter()


    @Provides
    fun provideSignupInteractor(): ISignupInteractor =
        SignupInteractor()


    @Provides
    fun provideSignupRepository(): ISignupRepository =
        SignupRepository()


    @Provides
    fun provideProfileRepository(): IProfileRepository =
        ProfileRepository()


    @Provides
    fun provideProfileStorage(): IProfileStorage =
        ProfileStorage()
}