package bonch.dev.poputi.domain.interactor.passenger.signup

import bonch.dev.poputi.data.repository.common.profile.IProfileRepository
import bonch.dev.poputi.data.repository.passenger.signup.ISignupRepository
import bonch.dev.poputi.data.storage.common.profile.IProfileStorage
import bonch.dev.poputi.domain.entities.common.profile.Profile
import bonch.dev.poputi.domain.entities.passenger.signup.DataSignup
import bonch.dev.poputi.presentation.interfaces.SuccessHandler
import bonch.dev.poputi.presentation.modules.passenger.signup.SignupComponent
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
            if (isSuccess) {
                callback(true)
            } else {
                //retry request
                signupRepository.sendSms(phone) { isSucc ->
                    callback(isSucc)
                }
            }
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

                            //get user id with token
                            getUserId(accessToken, callback)
                        } else callback(false)
                    }
                }
                token != null -> {
                    //save token
                    DataSignup.token = token

                    //get user id with token
                    getUserId(token, callback)
                }
                else -> callback(false)
            }
        }
    }


    override fun getUserId(token: String, callback: SuccessHandler) {
        signupRepository.getUserId(token) { id: Int?, error: String? ->
            when {
                error != null -> {
                    //retry request
                    signupRepository.getUserId(token) { userId: Int?, _: String? ->
                        if (userId != null) {
                            DataSignup.userId = userId
                            callback(true)

                        } else callback(false)
                    }
                }
                id != null -> {
                    DataSignup.userId = id
                    callback(true)

                }
                else -> {
                    callback(false)
                }
            }
        }
    }


    override fun checkProfile(callback: SuccessHandler) {
        val token = DataSignup.token
        val userId = DataSignup.userId

        if (token != null && userId != null) {
            profileRepository.getProfile(userId, token) { profile, _ ->
                if (profile?.firstName != null) callback(true)
                else callback(false)
            }
        }
    }


    override fun saveProfile(userId: Int, token: String, profileData: Profile) {
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


    override fun saveUserId() {
        val profileData = Profile()
        DataSignup.userId?.let {
            profileData.id = it
            //local save data
            profileStorage.saveProfile(profileData)
        }
    }


    override fun initRealm() {
        profileStorage.initRealm()
    }


    override fun saveToken(token: String) {
        profileStorage.saveToken(token)
    }


    override fun resetProfile() {
        profileStorage.removeProfile()
        profileStorage.saveCheckoutDriver(false)
    }


    override fun closeRealm() {
        profileStorage.closeRealm()
    }
}