package bonch.dev.di.module.passanger

import bonch.dev.di.scope.CompScope
import bonch.dev.presentation.modules.passanger.regulardrive.presenter.ContractPresenter
import bonch.dev.presentation.modules.passanger.regulardrive.presenter.CreateRegularDrivePresenter
import bonch.dev.presentation.modules.passanger.regulardrive.presenter.MapCreateRegDrivePresenter
import bonch.dev.presentation.modules.passanger.regulardrive.presenter.RegularDrivePresenter
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