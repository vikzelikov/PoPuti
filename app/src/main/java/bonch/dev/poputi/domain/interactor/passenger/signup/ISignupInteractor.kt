package bonch.dev.poputi.domain.interactor.passenger.signup

import bonch.dev.poputi.domain.entities.common.banking.BankCard
import bonch.dev.poputi.domain.entities.common.profile.Profile
import bonch.dev.poputi.presentation.interfaces.SuccessHandler


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


    fun getUserId(token: String, callback: SuccessHandler)


    fun initRealm()


    fun saveProfile(userId: Int, token: String, profileData: Profile)


    fun saveUserId()


    fun checkProfile(callback: SuccessHandler)


    fun saveToken(token: String)


    fun resetProfile()


    fun saveGooglePay(bankCard: BankCard)


    fun closeRealm()

}