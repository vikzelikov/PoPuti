package bonch.dev.data.repository.passenger.signup

typealias SignupHandler = (error: String?) -> Unit
typealias SignupCheckHandler<T> = (T, data: String?) -> Unit

interface ISignupRepository {

    fun sendSms(phone: String, callback: SignupHandler)

    fun checkCode(phone: String, code: String, callback: SignupCheckHandler<Boolean>)

    fun getUserId(token: String, callback: SignupCheckHandler<Int?>)

}