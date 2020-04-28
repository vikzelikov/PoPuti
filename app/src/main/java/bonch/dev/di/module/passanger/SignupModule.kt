package bonch.dev.di.module.passanger

import bonch.dev.data.repository.passanger.signup.ISignupRepository
import bonch.dev.data.repository.passanger.signup.SignupRepository
import bonch.dev.data.storage.passanger.signup.ISignupStorage
import bonch.dev.data.storage.passanger.signup.SignupStorage
import bonch.dev.domain.interactor.passanger.signup.ISignupInteractor
import bonch.dev.domain.interactor.passanger.signup.SignupInteractor
import bonch.dev.presentation.modules.passanger.signup.presenter.ConfirmPhonePresenter
import bonch.dev.presentation.modules.passanger.signup.presenter.ContractPresenter
import bonch.dev.presentation.modules.passanger.signup.presenter.FullNamePresenter
import bonch.dev.presentation.modules.passanger.signup.presenter.PhonePresenter
import bonch.dev.route.passanger.signup.ISignupRouter
import bonch.dev.route.passanger.signup.SignupRouter
import dagger.Module
import dagger.Provides


@Module
class SignupModule {

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
    fun provideSignupStorage(): ISignupStorage =
        SignupStorage()


    @Provides
    fun provideRouter(): ISignupRouter = SignupRouter()
}