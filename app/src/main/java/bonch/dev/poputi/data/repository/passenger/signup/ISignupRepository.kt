package bonch.dev.poputi.data.repository.passenger.signup

import bonch.dev.poputi.domain.entities.common.profile.Profile
import bonch.dev.poputi.presentation.interfaces.DataHandler
import bonch.dev.poputi.presentation.interfaces.SuccessHandler

interface ISignupRepository {

    fun sendSms(phone: String, callback: SuccessHandler)

    fun checkCode(phone: String, code: String, callback: DataHandler<String?>)

    fun getUserId(token: String, callback: DataHandler<Profile?>)

    fun updateFirebaseToken(firebaseToken: String, token: String)

}