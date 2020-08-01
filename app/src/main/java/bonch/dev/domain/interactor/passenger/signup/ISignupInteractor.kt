package bonch.dev.domain.interactor.passenger.signup

import bonch.dev.domain.entities.common.profile.Profile
import bonch.dev.presentation.interfaces.SuccessHandler


interface ISignupInteractor {

    fun sendSms(
        phone: String,
        callback: SuccessHandler
    )

    fun checkCode(
        phone: String,
        code: String,
        callback: SuccessHandler
    )


    fun initRealm()


    fun saveProfile(token: String, profileData: Profile, callback: SuccessHandler)


    fun saveToken(token: String)


    fun resetProfile()


    fun closeRealm()

}