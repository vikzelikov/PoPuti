package bonch.dev.domain.interactor

import bonch.dev.presentation.interfaces.SuccessHandler

interface IBaseInteractor {

    fun getToken(): String?

    fun getUserId(): Int

    fun isCheckoutDriver(): Boolean

    fun validateAccount(callback: SuccessHandler)

}