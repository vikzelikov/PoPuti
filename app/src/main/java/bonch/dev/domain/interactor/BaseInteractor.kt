package bonch.dev.domain.interactor

import bonch.dev.App
import bonch.dev.data.repository.passenger.signup.ISignupRepository
import bonch.dev.data.storage.common.profile.IProfileStorage
import bonch.dev.presentation.interfaces.SuccessHandler
import javax.inject.Inject

class BaseInteractor : IBaseInteractor {

    @Inject
    lateinit var signupRepository: ISignupRepository

    @Inject
    lateinit var profileStorage: IProfileStorage


    init {
        App.appComponent.inject(this)
    }


    override fun validateAccount(callback: SuccessHandler) {
        val token = profileStorage.getToken()
        if (token != null) {
            signupRepository.getUserId(token) { data, error ->
                if (data != null && error == null) {
                    callback(true)
                } else callback(false)
            }
        } else callback(false)
    }


    //for authorization
    override fun getToken(): String? {
        return profileStorage.getToken()
    }


    override fun getUserId(): Int {
        return profileStorage.getUserId()
    }


    //for access to driver UI
    override fun isCheckoutDriver(): Boolean {
        return profileStorage.isCheckoutDriver()
    }

}