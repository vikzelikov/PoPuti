package bonch.dev

import android.app.Application
import bonch.dev.di.component.AppComponent
import bonch.dev.di.component.DaggerAppComponent
import bonch.dev.di.component.driver.DaggerGetPassangerComponent
import bonch.dev.di.component.driver.GetPassangerComponent
import bonch.dev.di.module.AppModule


class App : Application() {
    var component: AppComponent? = null

    companion object {
        var mGetPassangerComponent: GetPassangerComponent? = null
    }


    init {
        initDagger()
    }


    private fun initDagger() {

        //needs to run once to generate it
        component = DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .build()

        mGetPassangerComponent = DaggerGetPassangerComponent.builder()

            .build()

    }

}