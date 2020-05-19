package bonch.dev.domain.interactor

interface IBaseInteractor {

    fun getToken(): String?

    fun isCheckoutDriver(): Boolean

}