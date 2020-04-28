package bonch.dev.di.module.passanger

import bonch.dev.di.scope.CompScope
import bonch.dev.presentation.modules.passanger.getdriver.ride.adapters.AddressesListAdapter
import bonch.dev.presentation.modules.passanger.getdriver.ride.presenter.ContractPresenter
import bonch.dev.presentation.modules.passanger.getdriver.ride.presenter.CreateRidePresenter
import bonch.dev.presentation.modules.passanger.getdriver.ride.presenter.DetailRidePresenter
import bonch.dev.presentation.modules.passanger.getdriver.ride.presenter.MapPresenter
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class GetDriverModule {

    @Provides
    @CompScope
    fun provideMapPresenter(): ContractPresenter.IMapPresenter = MapPresenter()


    @Provides
    @CompScope
    fun provideCreateRidePresenter(): ContractPresenter.ICreateRidePresenter = CreateRidePresenter()

//    @Provides
//    fun provideDetailRidePresenter(): DetailRidePresenter = DetailRidePresenter()

}