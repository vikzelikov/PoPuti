package bonch.dev.data.storage.common.profile

import bonch.dev.domain.entities.common.profile.Profile

interface IProfileStorage {

    fun initRealm()

    fun saveProfileData(profile: Profile)

    fun getProfileData(): Profile?

    fun removeProfileData()

    fun saveToken(token: String)

    fun getToken(): String?

    fun removeToken()

    fun getUserId(): Int

    fun saveDriverId(id: Int)

    fun getDriverId(): Int

    fun saveDriverAccess()

    fun getDriverAccess(): Boolean

    fun saveCheckoutDriver(isDriver: Boolean)

    fun isCheckoutDriver(): Boolean

    fun closeRealm()

}