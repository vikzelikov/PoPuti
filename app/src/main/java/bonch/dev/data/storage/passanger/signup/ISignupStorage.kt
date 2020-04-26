package bonch.dev.data.storage.passanger.signup

import bonch.dev.data.repository.passanger.profile.pojo.Profile

interface ISignupStorage {

    fun saveToken(token: String)

    fun saveProfileData(profileData: Profile)

}