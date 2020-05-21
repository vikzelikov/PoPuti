package bonch.dev.domain.interactor.common.profile

import android.util.Log
import bonch.dev.data.repository.common.media.IMediaRepository
import bonch.dev.data.repository.common.profile.IProfileRepository
import bonch.dev.data.storage.common.profile.IProfileStorage
import bonch.dev.domain.entities.common.profile.Profile
import bonch.dev.presentation.interfaces.DataHandler
import bonch.dev.presentation.interfaces.SuccessHandler
import bonch.dev.presentation.modules.common.profile.ProfileComponent
import java.io.File
import javax.inject.Inject

class ProfileInteractor : IProfileInteractor {

    @Inject
    lateinit var profileRepository: IProfileRepository

    @Inject
    lateinit var profileStorage: IProfileStorage

    @Inject
    lateinit var mediaRepository: IMediaRepository


    init {
        ProfileComponent.profileComponent?.inject(this)
    }


    override fun initRealm() {
        profileStorage.initRealm()
    }


    override fun saveProfileData(profile: Profile) {
        profileStorage.saveProfileData(profile)
    }


    override fun removeProfileData() {
        profileStorage.removeProfileData()
    }


    override fun sendProfileData(profile: Profile) {
        val token = profileStorage.getToken()
        val userId = profileStorage.getUserId()

        if (token != null && userId != -1) {
            profileRepository.saveProfile(userId, token, profile) { error ->
                if (error != null) {
                    //Retry request
                    profileRepository.saveProfile(userId, token, profile) {}
                }
            }
        } else {
            Log.d(
                "SEND_PROFILE_DATA",
                "Send to server profile data failed (userId: $userId, token: $token)"
            )
        }
    }


    override fun getProfileRemote(callback: DataHandler<Profile?>) {
        val token = profileStorage.getToken()
        val userId = profileStorage.getUserId()

        if (token != null && userId != -1) {
            profileRepository.getProfile(userId, token) { profile, error ->
                if (error != null) {
                    //retry request
                    profileRepository.getProfile(userId, token) { profileData, _ ->
                        if (profileData != null) {
                            //ok
                            callback(profile, null)
                        } else {
                            //error
                            callback(null, "Error")
                        }
                    }
                } else if (profile != null) {
                    //ok
                    callback(profile, null)
                }
            }
        } else {
            //error
            callback(null, "Error")
        }
    }


    override fun loadPhoto(image: File, profile: Profile, callback: SuccessHandler) {
        val token = profileStorage.getToken()
        val userId = profileStorage.getUserId()

        if (token != null && userId != -1) {
            mediaRepository.loadPhoto(token, image) { media, error ->
                when {
                    error != null -> {
                        //Retry request
                        mediaRepository.loadPhoto(token, image) { mediaObj, _ ->
                            if (mediaObj != null) {
                                profile.imgId = intArrayOf(mediaObj.id)
                                profileRepository.saveProfile(userId, token, profile) { err ->
                                    if (err != null) {
                                        //Retry request
                                        profileRepository.saveProfile(userId, token, profile) {
                                            callback(true)
                                        }
                                    } else callback(true)
                                }
                            } else callback(false)
                        }
                    }
                    media != null -> {
                        profile.imgId = intArrayOf(media.id)
                        profileRepository.saveProfile(userId, token, profile) { err ->
                            if (err != null) {
                                //Retry request
                                profileRepository.saveProfile(userId, token, profile) {
                                    callback(true)
                                }
                            } else callback(true)
                        }
                    } else -> callback(false)
                }
            }
        } else callback(false)
    }


    override fun getProfileLocate(): Profile? {
        return profileStorage.getProfileData()
    }


    override fun getDriverAccess(): Boolean {
        return profileStorage.getDriverAccess()
    }


    override fun saveCheckoutDriver(isDriver: Boolean) {
        profileStorage.saveCheckoutDriver(isDriver)
    }


    override fun isCheckoutDriver(): Boolean {
        return profileStorage.isCheckoutDriver()
    }


    override fun removeToken() {
        profileStorage.removeToken()
    }


    override fun closeRealm() {
        profileStorage.closeRealm()
    }


}