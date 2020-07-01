package bonch.dev.data.repository.common.profile

import bonch.dev.domain.entities.common.profile.Profile
import bonch.dev.domain.entities.common.profile.ProfilePhoto
import bonch.dev.presentation.interfaces.DataHandler
import bonch.dev.presentation.interfaces.SuccessHandler


interface IProfileRepository {

    fun saveProfile(userId: Int, token: String, profileData: Profile, callback: SuccessHandler)

    fun savePhoto(userId: Int, token: String, photo: ProfilePhoto, callback: SuccessHandler)

    fun getProfile(userId: Int, token: String, callback: DataHandler<Profile?>)

}