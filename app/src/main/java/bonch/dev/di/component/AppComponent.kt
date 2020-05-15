package bonch.dev.di.component

import android.content.Context
import android.content.SharedPreferences
import bonch.dev.App
import bonch.dev.MainActivity
import bonch.dev.di.module.AppModule
import bonch.dev.di.module.NetworkModule
import bonch.dev.di.scope.ApplicationContext
import bonch.dev.domain.interactor.BaseInteractor
import bonch.dev.presentation.base.MainPresenter
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

    fun inject(target: BaseInteractor)

    fun inject(target: MainActivity)

    fun inject(target: MainPresenter)

}