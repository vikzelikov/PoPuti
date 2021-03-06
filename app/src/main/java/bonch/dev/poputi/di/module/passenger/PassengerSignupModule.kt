package bonch.dev.poputi.di.module.passenger

import bonch.dev.poputi.data.repository.common.profile.IProfileRepository
import bonch.dev.poputi.data.repository.common.profile.ProfileRepository
import bonch.dev.poputi.data.repository.passenger.signup.ISignupRepository
import bonch.dev.poputi.data.repository.passenger.signup.SignupRepository
import bonch.dev.poputi.data.storage.common.profile.IProfileStorage
import bonch.dev.poputi.data.storage.common.profile.ProfileStorage
import bonch.dev.poputi.domain.interactor.passenger.signup.ISignupInteractor
import bonch.dev.poputi.domain.interactor.passenger.signup.SignupInteractor
import bonch.dev.poputi.presentation.modules.passenger.signup.presenter.ConfirmPhonePresenter
import bonch.dev.poputi.presentation.modules.passenger.signup.presenter.ContractPresenter
import bonch.dev.poputi.presentation.modules.passenger.signup.presenter.FullNamePresenter
import bonch.dev.poputi.presentation.modules.passenger.signup.presenter.PhonePresenter
import dagger.Module
import dagger.Provides


@Module
class PassengerSignupModule {

    @Provides
    fun providePhonePresenter(): ContractPresenter.IPhonePresenter = PhonePresenter()


    @Provides
    fun provideConfirmPhonePresenter(): ContractPresenter.IConfirmPhonePresenter =
        ConfirmPhonePresenter()


    @Provides
    fun provideFullNamePresenter(): ContractPresenter.IFullNamePresenter = FullNamePresenter()


    @Provides
    fun provideSignupInteractor(): ISignupInteractor = SignupInteractor()


    @Provides
    fun provideSignupRepository(): ISignupRepository = SignupRepository()


    @Provides
    fun provideProfileRepository(): IProfileRepository = ProfileRepository()


    @Provides
    fun provideProfileStorage(): IProfileStorage = ProfileStorage()
}