package bonch.dev.domain.interactor.common.profile

import bonch.dev.domain.entities.common.profile.Profile
import java.io.File

typealias ProfileHandler = (error: String?) -> Unit
typealias ProfileDataHandler<T> = (data: T, error: String?) -> Unit

interface IProfileInteractor {

    fun initRealm()

    fun saveProfileData(profile: Profile)

    fun removeProfileData()

    fun sendProfileData(profile: Profile)

    fun loadPhoto(image: File)

    fun getProfileData(): Profile?

    fun getDriverAccess(): Boolean

    fun removeToken()

    fun closeRealm()

}