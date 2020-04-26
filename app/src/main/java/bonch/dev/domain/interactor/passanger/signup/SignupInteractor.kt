package bonch.dev.domain.interactor.passanger.signup

import bonch.dev.data.repository.passanger.profile.pojo.Profile
import bonch.dev.data.repository.passanger.signup.ISignupRepository
import bonch.dev.data.storage.passanger.signup.ISignupStorage
import bonch.dev.domain.entities.passanger.signup.DataSignup
import bonch.dev.domain.entities.passanger.signup.Token
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
                //retry request
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
            if (status && token != null) {
                //save token
                DataSignup.token = token
            }

            callback(status)
        }
    }


    override fun sendProfileData(token: String, profileData: Profile) {
        signupRepository.getUserId(token) { id: Int?, _: String? ->
            if (id != null) {
                signupRepository.sendProfileData(id, token, profileData)

                profileData.id = id

                println("EEEEE !!!!!!! $id ${profileData.id} ${profileData.firstName}")

                //update profile
                saveProfileData(profileData)
            }
        }
    }


    override fun saveToken(token: String) {
        signupStorage.saveToken(token)
    }


    override fun saveProfileData(profileData: Profile) {
        signupStorage.saveProfileData(profileData)
    }
}