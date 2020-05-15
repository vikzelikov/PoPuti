package bonch.dev.data.repository.common.profile

import bonch.dev.domain.entities.common.profile.Profile
import bonch.dev.domain.interactor.common.profile.ProfileDataHandler
import bonch.dev.domain.interactor.common.profile.ProfileHandler


interface IProfileRepository {

    fun saveProfile(id: Int, token: String, profileData: Profile, callback: ProfileHandler)

    fun getProfile(id: Int, token: String, callback: ProfileDataHandler<Profile?>)

}