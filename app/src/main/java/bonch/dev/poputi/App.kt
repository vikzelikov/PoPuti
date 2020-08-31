package bonch.dev.poputi

import android.app.Application
import android.util.Log
import bonch.dev.poputi.di.component.AppComponent
import bonch.dev.poputi.di.component.DaggerAppComponent
import bonch.dev.poputi.di.module.AppModule


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


    override fun onLowMemory() {
        super.onLowMemory()
        System.gc()
    }
}