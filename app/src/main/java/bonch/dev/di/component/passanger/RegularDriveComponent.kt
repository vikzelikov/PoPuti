package bonch.dev.di.component.passanger

import bonch.dev.di.component.AppComponent
import bonch.dev.di.module.passanger.RegularDriveModule
import bonch.dev.di.scope.CompScope
import bonch.dev.presentation.modules.passanger.regulardrive.presenter.CreateRegularDrivePresenter
import bonch.dev.presentation.modules.passanger.regulardrive.presenter.MapCreateRegDrivePresenter
import bonch.dev.presentation.modules.passanger.regulardrive.presenter.RegularDrivePresenter
import bonch.dev.presentation.modules.passanger.regulardrive.view.CreateRegularDriveView
import bonch.dev.presentation.modules.passanger.regulardrive.view.MapCreateRegularDrive
import bonch.dev.presentation.modules.passanger.regulardrive.view.RegularDriveView
import dagger.Component


@CompScope
@Component(modules = [RegularDriveModule::class], dependencies = [AppComponent::class])
interface RegularDriveComponent {

    //main regular drive
    fun inject(target: RegularDriveView)

    fun inject(target: RegularDrivePresenter)


    //yandex map create regular drive
    fun inject(target: MapCreateRegularDrive)

    fun inject(target: MapCreateRegDrivePresenter)


    //create regular drive
    fun inject(target: CreateRegularDriveView)

    fun inject(target: CreateRegularDrivePresenter)

}