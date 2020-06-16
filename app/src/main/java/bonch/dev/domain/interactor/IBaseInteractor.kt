package bonch.dev.domain.interactor

interface IBaseInteractor {

    fun getToken(): String?

    fun getUserId(): Int

    fun isCheckoutDriver(): Boolean

}