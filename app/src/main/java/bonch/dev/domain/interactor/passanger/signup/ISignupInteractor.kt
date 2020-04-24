package bonch.dev.domain.interactor.passanger.signup

typealias SignupHandler<T> = (error: T) -> Unit

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

    fun saveToken()

    fun saveProfileData()

}