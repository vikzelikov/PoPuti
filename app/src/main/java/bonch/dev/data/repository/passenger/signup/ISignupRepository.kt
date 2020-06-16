package bonch.dev.data.repository.passenger.signup

import bonch.dev.presentation.interfaces.DataHandler
import bonch.dev.presentation.interfaces.SuccessHandler

interface ISignupRepository {

    fun sendSms(phone: String, callback: SuccessHandler)

    fun checkCode(phone: String, code: String, callback: DataHandler<String?>)

    fun getUserId(token: String, callback: DataHandler<Int?>)

}