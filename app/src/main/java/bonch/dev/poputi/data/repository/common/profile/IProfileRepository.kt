package bonch.dev.poputi.data.repository.common.profile

import bonch.dev.poputi.domain.entities.common.profile.Profile
import bonch.dev.poputi.domain.entities.common.profile.ProfilePhoto
import bonch.dev.poputi.presentation.interfaces.DataHandler
import bonch.dev.poputi.presentation.interfaces.SuccessHandler


interface IProfileRepository {

    fun saveProfile(userId: Int, token: String, profileData: Profile, callback: SuccessHandler)

    fun savePhoto(userId: Int, token: String, photo: ProfilePhoto, callback: SuccessHandler)

    fun getProfile(userId: Int, token: String, callback: DataHandler<Profile?>)

}