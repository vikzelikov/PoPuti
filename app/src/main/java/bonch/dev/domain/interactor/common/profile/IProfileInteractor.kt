package bonch.dev.domain.interactor.common.profile

import bonch.dev.domain.entities.common.profile.Profile
import bonch.dev.presentation.interfaces.DataHandler
import bonch.dev.presentation.interfaces.SuccessHandler
import java.io.File


interface IProfileInteractor {

    fun initRealm()

    fun saveProfileData(profile: Profile)

    fun removeProfileData()

    fun sendProfileData(profile: Profile)

    fun getProfileRemote(callback: DataHandler<Profile?>)

    fun loadPhoto(image: File, profile: Profile, callback: SuccessHandler)

    fun getProfileLocate(): Profile?

    fun getDriverAccess(): Boolean

    fun saveCheckoutDriver(isDriver: Boolean)

    fun isCheckoutDriver(): Boolean

    fun removeToken()

    fun closeRealm()

}