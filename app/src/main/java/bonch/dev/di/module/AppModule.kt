package bonch.dev.di.module

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import bonch.dev.App
import bonch.dev.di.scope.ApplicationContext
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

}