package bonch.dev.poputi.di.module.passenger

import bonch.dev.poputi.data.repository.passenger.regular.ride.IRegularRidesRepository
import bonch.dev.poputi.data.repository.passenger.regular.ride.RegularRidesRepository
import bonch.dev.poputi.data.storage.common.profile.IProfileStorage
import bonch.dev.poputi.data.storage.common.profile.ProfileStorage
import bonch.dev.poputi.di.scope.CompScope
import bonch.dev.poputi.domain.interactor.passenger.getdriver.GetDriverInteractor
import bonch.dev.poputi.domain.interactor.passenger.getdriver.IGetDriverInteractor
import bonch.dev.poputi.domain.interactor.passenger.regular.ride.IRegularRidesInteractor
import bonch.dev.poputi.domain.interactor.passenger.regular.ride.RegularRidesInteractor
import bonch.dev.poputi.presentation.modules.passenger.regular.ride.presenter.*
import dagger.Module
import dagger.Provides

@Module
class RegularRidesModule {

    @Provides
    @CompScope
    fun provideRegularRidesPresenter(): ContractPresenter.IRegularDrivePresenter =
        RegularRidesPresenter()


    @Provides
    @CompScope
    fun provideMapCreateRegRidePresenter(): ContractPresenter.IMapCreateRegDrivePresenter =
        MapCreateRegRidePresenter()


    @Provides
    @CompScope
    fun provideCreateRegularRidePresenter(): ContractPresenter.ICreateRegularDrivePresenter =
        CreateRegularRidePresenter()


    @Provides
    @CompScope
    fun provideActiveRidesPresenter(): ContractPresenter.IActiveRidesPresenter =
        ActiveRidesPresenter()


    @Provides
    @CompScope
    fun provideArchiveRidesPresenter(): ContractPresenter.IArchiveRidesPresenter =
        ArchiveRidesPresenter()


    @Provides
    @CompScope
    fun provideRegularRidesInteractor(): IRegularRidesInteractor =
        RegularRidesInteractor()


    @Provides
    @CompScope
    fun provideGetDriverInteractor(): IGetDriverInteractor = GetDriverInteractor()


    @Provides
    @CompScope
    fun provideRegularRidesRepository(): IRegularRidesRepository = RegularRidesRepository()


    @Provides
    @CompScope
    fun provideProfileStorage(): IProfileStorage = ProfileStorage()
}