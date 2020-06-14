package bonch.dev.domain.interactor.passenger.signup

import bonch.dev.data.repository.common.profile.IProfileRepository
import bonch.dev.data.repository.passenger.signup.ISignupRepository
import bonch.dev.data.storage.common.profile.IProfileStorage
import bonch.dev.domain.entities.common.profile.Profile
import bonch.dev.domain.entities.passenger.signup.DataSignup
import bonch.dev.presentation.modules.passenger.signup.SignupComponent
import javax.inject.Inject

class SignupInteractor : ISignupInteractor {

    @Inject
    lateinit var signupRepository: ISignupRepository

    @Inject
    lateinit var profileRepository: IProfileRepository

    @Inject
    lateinit var profileStorage: IProfileStorage

    init {
        SignupComponent.passengerSignupComponent?.inject(this)
    }


    //NETWORK
    override fun sendSms(
        phone: String,
        callback: SignupHandler<String?>
    ) {
        signupRepository.sendSms(phone) { error ->
            if (error != null) {
                //retry request
                signupRepository.sendSms(phone) {}
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
            if (token != null) {
                //save token
                DataSignup.token = token
            }

            callback(status)
        }
    }


    override fun sendProfileData(token: String, profileData: Profile) {
        signupRepository.getUserId(token) { id: Int?, _: String? ->
            if (id == null) {
                //Retry request
                retryGetUserId(token, profileData)
            } else {
                retrySendProfileData(id, token, profileData)
            }
        }
    }


    override fun retryGetUserId(token: String, profileData: Profile) {
        signupRepository.getUserId(token) { id: Int?, _: String? ->
            if (id != null) {
                retrySendProfileData(id, token, profileData)
            }
        }
    }


    override fun retrySendProfileData(id: Int, token: String, profileData: Profile) {
        profileRepository.saveProfile(id, token, profileData) { error ->
            if (error != null) {
                //Retry request
                profileRepository.saveProfile(id, token, profileData) {}
            }
        }

        profileData.id = id

        //remove old data
        profileStorage.removeProfileData()

        //local save data
        saveProfileData(profileData)
    }


    override fun initRealm() {
        profileStorage.initRealm()
    }


    override fun saveToken(token: String) {
        profileStorage.saveToken(token)
    }


    override fun saveProfileData(profileData: Profile) {
        profileStorage.saveProfileData(profileData)
    }


    override fun closeRealm() {
        profileStorage.closeRealm()
    }
}