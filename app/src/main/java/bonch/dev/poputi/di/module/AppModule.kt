package bonch.dev.poputi.di.module

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import bonch.dev.poputi.App
import bonch.dev.poputi.data.repository.common.ride.IRideRepository
import bonch.dev.poputi.data.repository.common.ride.RideRepository
import bonch.dev.poputi.data.repository.passenger.signup.ISignupRepository
import bonch.dev.poputi.data.repository.passenger.signup.SignupRepository
import bonch.dev.poputi.data.storage.common.profile.IProfileStorage
import bonch.dev.poputi.data.storage.common.profile.ProfileStorage
import bonch.dev.poputi.data.storage.passenger.getdriver.GetDriverStorage
import bonch.dev.poputi.data.storage.passenger.getdriver.IGetDriverStorage
import bonch.dev.poputi.di.scope.ApplicationContext
import bonch.dev.poputi.domain.interactor.BaseInteractor
import bonch.dev.poputi.domain.interactor.IBaseInteractor
import bonch.dev.poputi.presentation.base.MainPresenter
import bonch.dev.poputi.presentation.interfaces.IMainPresenter
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
    fun provideSignupRepository(): ISignupRepository = SignupRepository()

    @Provides
    @Singleton
    fun provideRideRepository(): IRideRepository = RideRepository()
}