package bonch.dev

import android.app.Application
import bonch.dev.di.component.AppComponent
import bonch.dev.di.component.DaggerAppComponent
import bonch.dev.di.component.driver.DaggerGetPassangerComponent
import bonch.dev.di.component.driver.GetPassangerComponent
import bonch.dev.di.module.AppModule
import bonch.dev.di.module.driver.getpassanger.GetPassangerModule


class App : Application() {
    var appComponent: AppComponent? = null

    companion object {
        var getPassangerComponent: GetPassangerComponent? = null
    }


    init {
        initDagger()
    }


    private fun initDagger() {
        //needs to run once to generate it
        appComponent = DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .build()

        getPassangerComponent = DaggerGetPassangerComponent.builder()
            .appComponent(appComponent)
            .getPassangerModule(GetPassangerModule())
            .build()

    }

}