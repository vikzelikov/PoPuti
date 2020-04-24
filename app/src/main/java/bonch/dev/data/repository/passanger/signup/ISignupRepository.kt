package bonch.dev.data.repository.passanger.signup

typealias SignupHandler = (error: String?) -> Unit
typealias SignupCheckHandler<T> = (T, token: String?) -> Unit

interface ISignupRepository {

    fun sendSms(phone: String, callback: SignupHandler)

    fun checkCode(phone: String, code: String, callback: SignupCheckHandler<Boolean>)

}