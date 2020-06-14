package bonch.dev.di.component.passenger

import bonch.dev.di.component.AppComponent
import bonch.dev.di.module.passenger.RegularDriveModule
import bonch.dev.di.scope.CompScope
import bonch.dev.presentation.modules.passenger.regulardrive.presenter.CreateRegularDrivePresenter
import bonch.dev.presentation.modules.passenger.regulardrive.presenter.MapCreateRegDrivePresenter
import bonch.dev.presentation.modules.passenger.regulardrive.presenter.RegularDrivePresenter
import bonch.dev.presentation.modules.passenger.regulardrive.view.CreateRegularDriveView
import bonch.dev.presentation.modules.passenger.regulardrive.view.MapCreateRegularDrive
import bonch.dev.presentation.modules.passenger.regulardrive.view.RegularDriveView
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