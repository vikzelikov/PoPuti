package bonch.dev.data.repository.common.profile

import bonch.dev.domain.entities.common.profile.Profile
import bonch.dev.presentation.interfaces.DataHandler
import bonch.dev.presentation.interfaces.ErrorHandler


interface IProfileRepository {

    fun saveProfile(id: Int, token: String, profileData: Profile, callback: ErrorHandler)

    fun getProfile(id: Int, token: String, callback: DataHandler<Profile?>)

}