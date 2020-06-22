package bonch.dev.data.storage.common.profile

import bonch.dev.domain.entities.common.profile.Profile

interface IProfileStorage {

    fun initRealm()

    fun saveProfile(profile: Profile)

    fun getProfile(): Profile?

    fun removeProfile()

    fun saveToken(token: String)

    fun getToken(): String?

    fun getUserId(): Int

    fun saveDriverId(id: Int)

    fun getDriverId(): Int

    fun saveDriverAccess()

    fun removeDriverAccess()

    fun getDriverAccess(): Boolean

    fun saveCheckoutDriver(isDriver: Boolean)

    fun isCheckoutDriver(): Boolean

    fun closeRealm()

}