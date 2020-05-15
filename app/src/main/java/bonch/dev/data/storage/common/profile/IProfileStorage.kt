package bonch.dev.data.storage.common.profile

import bonch.dev.domain.entities.common.profile.Profile

interface IProfileStorage {

    fun initRealm()

    fun saveProfileData(profile: Profile)

    fun getProfileData(): Profile?

    fun removeProfileData()

    fun saveToken(token: String)

    fun getUserId(): Int

    fun saveDriverId(id: Int)

    fun getDriverId(): Int

    fun getToken(): String?

    fun saveDriverAccess()

    fun getDriverAccess(): Boolean

    fun removeToken()

    fun closeRealm()

}