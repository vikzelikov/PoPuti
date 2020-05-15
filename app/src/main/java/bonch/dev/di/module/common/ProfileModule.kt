package bonch.dev.di.module.common

import bonch.dev.data.repository.common.media.IMediaRepository
import bonch.dev.data.repository.common.media.MediaRepository
import bonch.dev.data.repository.common.profile.IProfileRepository
import bonch.dev.data.repository.common.profile.ProfileRepository
import bonch.dev.data.storage.common.profile.IProfileStorage
import bonch.dev.data.storage.common.profile.ProfileStorage
import bonch.dev.di.scope.CompScope
import bonch.dev.domain.interactor.common.profile.IProfileInteractor
import bonch.dev.domain.interactor.common.profile.ProfileInteractor
import bonch.dev.presentation.modules.common.profile.presenter.IProfileDetailPresenter
import bonch.dev.presentation.modules.common.profile.presenter.IProfilePresenter
import bonch.dev.presentation.modules.common.profile.presenter.ProfileDetailPresenter
import bonch.dev.presentation.modules.common.profile.presenter.ProfilePresenter
import dagger.Module
import dagger.Provides

@Module
class ProfileModule {

    @Provides
    @CompScope
    fun provideProfilePresenter(): IProfilePresenter = ProfilePresenter()

    @Provides
    @CompScope
    fun provideProfileDetailPresenter(): IProfileDetailPresenter = ProfileDetailPresenter()

    @Provides
    @CompScope
    fun provideProfileInteractor(): IProfileInteractor = ProfileInteractor()

    @Provides
    @CompScope
    fun provideProfileStorage(): IProfileStorage = ProfileStorage()

    @Provides
    @CompScope
    fun provideProfileRepository(): IProfileRepository = ProfileRepository()

    @Provides
    @CompScope
    fun provideMediaRepository(): IMediaRepository = MediaRepository()

}