package bonch.dev.di.module.passenger

import bonch.dev.di.scope.CompScope
import bonch.dev.presentation.modules.passenger.regulardrive.presenter.ContractPresenter
import bonch.dev.presentation.modules.passenger.regulardrive.presenter.CreateRegularDrivePresenter
import bonch.dev.presentation.modules.passenger.regulardrive.presenter.MapCreateRegDrivePresenter
import bonch.dev.presentation.modules.passenger.regulardrive.presenter.RegularDrivePresenter
import dagger.Module
import dagger.Provides

@Module
class RegularDriveModule {

    @Provides
    @CompScope
    fun provideRegularDrivePresenter(): ContractPresenter.IRegularDrivePresenter =
        RegularDrivePresenter()


    @Provides
    @CompScope
    fun provideMapCreateRegDrivePresenter(): ContractPresenter.IMapCreateRegDrivePresenter =
        MapCreateRegDrivePresenter()


    @Provides
    @CompScope
    fun provideCreateRegularDrivePresenter(): ContractPresenter.ICreateRegularDrivePresenter =
        CreateRegularDrivePresenter()


}