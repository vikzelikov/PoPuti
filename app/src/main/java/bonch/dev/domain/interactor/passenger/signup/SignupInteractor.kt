package bonch.dev.domain.interactor.passenger.signup

import bonch.dev.data.repository.common.profile.IProfileRepository
import bonch.dev.data.repository.passenger.signup.ISignupRepository
import bonch.dev.data.storage.common.profile.IProfileStorage
import bonch.dev.domain.entities.common.profile.Profile
import bonch.dev.domain.entities.passenger.signup.DataSignup
import bonch.dev.presentation.interfaces.SuccessHandler
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
        callback: SuccessHandler
    ) {
        signupRepository.sendSms(phone) { isSuccess ->
            if (!isSuccess) {
                //retry request
                signupRepository.sendSms(phone) {}
                callback(false)
            } else callback(true)
        }
    }


    override fun checkCode(
        phone: String,
        code: String,
        callback: SuccessHandler
    ) {
        signupRepository.checkCode(phone, code) { token: String?, error: String? ->
            when {
                error != null -> {
                    //retry request
                    signupRepository.checkCode(phone, code) { accessToken: String?, _ ->
                        if (accessToken != null) {
                            //save token
                            DataSignup.token = token
                            callback(true)

                        } else callback(false)
                    }
                }
                token != null -> {
                    //save token
                    DataSignup.token = token
                    callback(true)
                }
                else -> callback(false)
            }
        }
    }


    override fun saveProfile(token: String, profileData: Profile, callback: SuccessHandler) {
        signupRepository.getUserId(token) { id: Int?, error: String? ->
            when {
                error != null -> {
                    //retry request
                    signupRepository.getUserId(token) { userId: Int?, _: String? ->
                        if (userId != null) {
                            saveProfile(userId, token, profileData)
                            callback(true)

                        } else callback(false)
                    }
                }
                id != null -> {
                    saveProfile(id, token, profileData)
                    callback(true)

                }
                else -> callback(false)
            }
        }
    }


    private fun saveProfile(userId: Int, token: String, profileData: Profile) {
        profileRepository.saveProfile(userId, token, profileData) { isSuccess ->
            if (!isSuccess) {
                //Retry request
                profileRepository.saveProfile(userId, token, profileData) {}
            }
        }

        profileData.id = userId

        //local save data
        profileStorage.saveProfile(profileData)
    }


    override fun initRealm() {
        profileStorage.initRealm()
    }


    override fun saveToken(token: String) {
        profileStorage.saveToken(token)
    }


    override fun closeRealm() {
        profileStorage.closeRealm()
    }
}