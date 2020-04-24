package bonch.dev

import android.app.Application
import bonch.dev.di.component.AppComponent
import bonch.dev.di.component.DaggerAppComponent
import bonch.dev.di.module.AppModule


class App : Application() {

    companion object {
        lateinit var appComponent: AppComponent
    }


    init {
        initDagger()
    }


    private fun initDagger() {
        //needs to run once to generate it
        appComponent = DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .build()
    }

}