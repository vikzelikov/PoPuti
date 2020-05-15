package bonch.dev.di.module

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import bonch.dev.App
import bonch.dev.data.storage.common.profile.IProfileStorage
import bonch.dev.data.storage.common.profile.ProfileStorage
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
}