package bonch.dev.di.module.passenger

import bonch.dev.data.repository.passenger.getdriver.GetDriverRepository
import bonch.dev.data.repository.passenger.getdriver.IGetDriverRepository
import bonch.dev.data.storage.passenger.getdriver.GetDriverStorage
import bonch.dev.data.storage.passenger.getdriver.IGetDriverStorage
import bonch.dev.data.storage.common.profile.IProfileStorage
import bonch.dev.data.storage.common.profile.ProfileStorage
import bonch.dev.domain.interactor.passenger.getdriver.GetDriverInteractor
import bonch.dev.domain.interactor.passenger.getdriver.IGetDriverInteractor
import bonch.dev.presentation.modules.common.addbanking.presenter.AddBankCardPresenter
import bonch.dev.presentation.modules.common.addbanking.presenter.IAddBankCardPresenter
import bonch.dev.di.scope.CompScope
import bonch.dev.presentation.modules.passenger.getdriver.presenter.*
import dagger.Module
import dagger.Provides

@Module
class GetDriverModule {

    @Provides
    @CompScope
    fun provideMapCreateRidePresenter(): ContractPresenter.IMapCreateRidePresenter =
        MapCreateRidePresenter()


    @Provides
    @CompScope
    fun provideCreateRidePresenter(): ContractPresenter.ICreateRidePresenter = CreateRidePresenter()


    @Provides
    @CompScope
    fun provideDetailRidePresenter(): ContractPresenter.IDetailRidePresenter = DetailRidePresenter()


    @Provides
    @CompScope
    fun provideAddBankCardPresenter(): IAddBankCardPresenter =
        AddBankCardPresenter()


    @Provides
    @CompScope
    fun provideMapGetDriverPresenter(): ContractPresenter.IMapGetDriverPresenter =
        MapGetDriverPresenter()

    @Provides
    @CompScope
    fun providesGetDriverPresenter(): ContractPresenter.IGetDriverPresenter = GetDriverPresenter()


    @Provides
    @CompScope
    fun providesTrackRidePresenter(): ContractPresenter.ITrackRidePresenter = TrackRidePresenter()


    @Provides
    @CompScope
    fun provideGetDriverInteractor(): IGetDriverInteractor = GetDriverInteractor()


    @Provides
    @CompScope
    fun provideGetDriverRepository(): IGetDriverRepository = GetDriverRepository()


    @Provides
    @CompScope
    fun provideGetDriverStorage(): IGetDriverStorage = GetDriverStorage()


    @Provides
    @CompScope
    fun provideProfileStorage(): IProfileStorage = ProfileStorage()

}