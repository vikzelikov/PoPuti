package bonch.dev.di.module

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import bonch.dev.App
import bonch.dev.data.repository.IMainRepository
import bonch.dev.data.repository.MainRepository
import bonch.dev.data.repository.passenger.signup.ISignupRepository
import bonch.dev.data.repository.passenger.signup.SignupRepository
import bonch.dev.data.storage.common.profile.IProfileStorage
import bonch.dev.data.storage.common.profile.ProfileStorage
import bonch.dev.data.storage.passenger.getdriver.GetDriverStorage
import bonch.dev.data.storage.passenger.getdriver.IGetDriverStorage
import bonch.dev.di.scope.ApplicationContext
import bonch.dev.domain.interactor.BaseInteractor
import bonch.dev.domain.interactor.IBaseInteractor
import bonch.dev.presentation.base.MainPresenter
import bonch.dev.presentation.interfaces.IMainPresenter
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


@Module
class AppModule(private val application: App) {

    @Provides
    @Singleton
    fun provideApp(): App {
        return application
    }


    @Provides
    @Singleton
    fun provideSharedPref(): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(application.applicationContext)
    }


    @Provides
    @Singleton
    @ApplicationContext
    fun provideContext(): Context {
        return application
    }


    @Provides
    @Singleton
    fun provideMainPresenter(): IMainPresenter = MainPresenter()


    @Provides
    @Singleton
    fun provideBaseInteractor(): IBaseInteractor = BaseInteractor()


    @Provides
    @Singleton
    fun provideProfileStorage(): IProfileStorage = ProfileStorage()

    @Provides
    @Singleton
    fun provideGetDriverStorage(): IGetDriverStorage = GetDriverStorage()

    @Provides
    @Singleton
    fun provideMainRepository(): IMainRepository = MainRepository()

    @Provides
    @Singleton
    fun provideSignupRepository(): ISignupRepository = SignupRepository()
}