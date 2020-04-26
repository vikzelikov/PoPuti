package bonch.dev.domain.interactor.passanger.signup

import bonch.dev.data.repository.passanger.profile.pojo.Profile

typealias SignupHandler<T> = (success: T) -> Unit

interface ISignupInteractor {

    fun sendSms(
        phone: String,
        callback: SignupHandler<String?>
    )

    fun checkCode(
        phone: String,
        code: String,
        callback: SignupHandler<Boolean>
    )


    fun sendProfileData(token: String, profileData: Profile)


    fun saveToken(token: String)


    fun saveProfileData(profileData: Profile)

}