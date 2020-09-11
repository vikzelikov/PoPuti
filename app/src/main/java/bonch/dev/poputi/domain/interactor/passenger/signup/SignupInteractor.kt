package bonch.dev.poputi.domain.interactor.passenger.signup

import bonch.dev.poputi.data.repository.common.profile.IProfileRepository
import bonch.dev.poputi.data.repository.passenger.signup.ISignupRepository
import bonch.dev.poputi.data.storage.common.profile.IProfileStorage
import bonch.dev.poputi.domain.entities.common.banking.BankCard
import bonch.dev.poputi.domain.entities.common.profile.CacheProfile
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
        signupRepository.getUserId(token) { profile: Profile?, _: String? ->
            if (profile?.id != null) {
                DataSignup.userId = profile.id
                callback(true)
            } else {
                callback(false)

            }
        }
    }


    override fun checkProfile(callback: SuccessHandler) {
        val token = DataSignup.token
        val userId = DataSignup.userId

        if (token != null && userId != null) {
            profileRepository.getProfile(userId, token) { profile, _ ->
                if (profile?.firstName != null){
                    CacheProfile.profile = profile

                    callback(true)
                }
                else callback(false)
            }
        }
    }


    override fun saveProfile(
        userId: Int,
        token: String,
        profileData: Profile,
        callback: SuccessHandler
    ) {
        profileRepository.saveProfile(userId, token, profileData) { isSuccess ->
            if (isSuccess) {
                //local save data
                profileStorage.saveUserId(userId)
                callback(true)

            } else callback(false)
        }
    }


    override fun saveUserId() {
        DataSignup.userId?.let {
            profileStorage.saveUserId(it)
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


    override fun saveGooglePay(bankCard: BankCard) {
        profileStorage.saveBankCard(bankCard)
    }


    override fun updateFirebaseToken(firebaseToken: String) {
        val token = profileStorage.getToken()

        if (token != null) {
            signupRepository.updateFirebaseToken(firebaseToken, token)
        }
    }


    override fun closeRealm() {
        profileStorage.closeRealm()
    }

}