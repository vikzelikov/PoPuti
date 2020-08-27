package bonch.dev.poputi.di.module.driver

import bonch.dev.poputi.data.repository.common.profile.IProfileRepository
import bonch.dev.poputi.data.repository.common.profile.ProfileRepository
import bonch.dev.poputi.data.repository.common.rate.IRateRideRepository
import bonch.dev.poputi.data.repository.common.rate.RateRideRepository
import bonch.dev.poputi.data.storage.common.profile.IProfileStorage
import bonch.dev.poputi.data.storage.common.profile.ProfileStorage
import bonch.dev.poputi.di.scope.CompScope
import bonch.dev.poputi.domain.interactor.driver.rating.IRatingInteractor
import bonch.dev.poputi.domain.interactor.driver.rating.RatingInteractor
import bonch.dev.poputi.presentation.modules.driver.rating.presenter.IRatingPresenter
import bonch.dev.poputi.presentation.modules.driver.rating.presenter.RatingPresenter
import dagger.Module
import dagger.Provides

@Module
class RatingModule {

    @Provides
    @CompScope
    fun provideRatingPresenter(): IRatingPresenter = RatingPresenter()


    @Provides
    @CompScope
    fun provideRatingInteractor(): IRatingInteractor = RatingInteractor()


    @Provides
    @CompScope
    fun providesRateRideRepository(): IRateRideRepository = RateRideRepository()


    @Provides
    @CompScope
    fun provideProfileStorage(): IProfileStorage = ProfileStorage()


    @Provides
    @CompScope
    fun provideProfileRepository(): IProfileRepository = ProfileRepository()

}