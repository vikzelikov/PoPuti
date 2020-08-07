package bonch.dev.poputi.di.component

import android.content.Context
import android.content.SharedPreferences
import bonch.dev.poputi.App
import bonch.dev.poputi.MainActivity
import bonch.dev.poputi.di.module.AppModule
import bonch.dev.poputi.di.module.NetworkModule
import bonch.dev.poputi.di.scope.ApplicationContext
import bonch.dev.poputi.domain.interactor.BaseInteractor
import bonch.dev.poputi.presentation.base.MainPresenter
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