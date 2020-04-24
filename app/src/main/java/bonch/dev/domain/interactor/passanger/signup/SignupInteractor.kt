package bonch.dev.domain.interactor.passanger.signup

import bonch.dev.data.repository.passanger.signup.ISignupRepository
import bonch.dev.data.storage.passanger.signup.ISignupStorage
import bonch.dev.presentation.modules.passanger.signup.SignupComponent
import javax.inject.Inject

class SignupInteractor : ISignupInteractor {

    @Inject
    lateinit var signupRepository: ISignupRepository

    @Inject
    lateinit var signupStorage: ISignupStorage

    init {
        SignupComponent.signupComponent?.inject(this)
    }

    override fun sendSms(
        phone: String,
        callback: SignupHandler<String?>
    ) {
        signupRepository.sendSms(phone) { error ->
            if (error != null) {
                signupRepository.sendSms(phone, callback)
                callback(error)
            }
        }
    }


    override fun checkCode(
        phone: String,
        code: String,
        callback: SignupHandler<Boolean>
    ) {
        signupRepository.checkCode(phone, code) { status: Boolean, token: String? ->
            if(status && token != null){
                println("SAVE !!!!")
            }

            callback(status)
        }
    }


    override fun saveToken() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    override fun saveProfileData() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}