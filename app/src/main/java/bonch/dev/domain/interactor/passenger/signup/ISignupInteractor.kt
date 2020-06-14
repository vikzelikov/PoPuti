package bonch.dev.domain.interactor.passenger.signup

import bonch.dev.domain.entities.common.profile.Profile

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


    fun initRealm()


    fun sendProfileData(token: String, profileData: Profile)


    fun retryGetUserId(token: String, profileData: Profile)


    fun retrySendProfileData(id: Int, token: String, profileData: Profile)


    fun saveToken(token: String)


    fun saveProfileData(profileData: Profile)


    fun closeRealm()

}