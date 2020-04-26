package bonch.dev.di.component

import android.content.Context
import android.content.SharedPreferences
import bonch.dev.App
import bonch.dev.di.module.AppModule
import bonch.dev.di.module.NetworkModule
import bonch.dev.di.scope.ApplicationContext
import dagger.Component
import retrofit2.Retrofit
import javax.inject.Singleton


@Singleton
@Component(modules = [AppModule::class, NetworkModule::class])
interface AppComponent {

    fun getApp(): App

    fun getNetworkModule(): Retrofit

    fun getSharedPref(): SharedPreferences

    @ApplicationContext
    fun getContext(): Context


}