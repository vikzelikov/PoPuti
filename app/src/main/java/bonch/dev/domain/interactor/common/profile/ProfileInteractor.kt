package bonch.dev.domain.interactor.common.profile

import android.util.Log
import bonch.dev.data.repository.common.media.IMediaRepository
import bonch.dev.data.repository.common.profile.IProfileRepository
import bonch.dev.data.storage.common.profile.IProfileStorage
import bonch.dev.domain.entities.common.profile.Profile
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


    override fun loadPhoto(image: File) {
        val token = profileStorage.getToken()
        val userId = profileStorage.getUserId()

        if (token != null && userId != -1) {
            mediaRepository.loadPhoto(token, image) { imageId, error ->
                if (error != null) {
                    //Retry request
                    //retryLoadPhoto(token, image)
                } else if (imageId != null) {
                    val profile = profileStorage.getProfileData()
                    //
                    println("AAA ${imageId.id}")
                    //TODO fix server
                    if (profile != null) {
                        //profile.imgId = intArrayOf(imageId)
                        profileRepository.saveProfile(userId, token, profile) {}
                    }
                }
            }
        } else {
            Log.d(
                "LOAD_PHOTO",
                "Load photo to server failed (token: $token)"
            )
        }
    }



    override fun getProfileData(): Profile? {
        return profileStorage.getProfileData()
    }


    override fun getDriverAccess(): Boolean {
        return profileStorage.getDriverAccess()
    }


    override fun removeToken() {
        profileStorage.removeToken()
    }


    override fun closeRealm() {
        profileStorage.closeRealm()
    }


}