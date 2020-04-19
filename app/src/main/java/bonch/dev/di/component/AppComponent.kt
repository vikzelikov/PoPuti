package bonch.dev.di.component

import bonch.dev.di.module.AppModule
import bonch.dev.MainActivity
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {
    fun inject(target: MainActivity?)
}