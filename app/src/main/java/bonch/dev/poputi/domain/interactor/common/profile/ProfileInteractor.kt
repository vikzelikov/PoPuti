package bonch.dev.poputi.domain.interactor.common.profile

import android.util.Log
import bonch.dev.poputi.data.repository.common.media.IMediaRepository
import bonch.dev.poputi.data.repository.common.profile.IProfileRepository
import bonch.dev.poputi.data.storage.common.profile.IProfileStorage
import bonch.dev.poputi.domain.entities.common.banking.BankCard
import bonch.dev.poputi.domain.entities.common.media.MediaObject
import bonch.dev.poputi.domain.entities.common.profile.Profile
import bonch.dev.poputi.domain.entities.common.profile.ProfilePhoto
import bonch.dev.poputi.presentation.interfaces.DataHandler
import bonch.dev.poputi.presentation.interfaces.SuccessHandler
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


    override fun localSaveProfile(profile: Profile) {
        profileStorage.saveProfile(profile)
    }


    override fun removeProfileData() {
        profileStorage.removeProfile()
    }


    override fun remoteSaveProfile(profile: Profile) {
        val token = profileStorage.getToken()
        val userId = profileStorage.getUserId()

        if (token != null && userId != -1) {
            profileRepository.saveProfile(userId, token, profile) { isSuccess ->
                if (!isSuccess) {
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
                                remoteSaveProfile(mediaObj, token, userId, callback)
                            } else callback(false)
                        }
                    }
                    media != null -> {
                        remoteSaveProfile(media, token, userId, callback)
                    }
                    else -> callback(false)
                }
            }
        } else callback(false)
    }


    private fun remoteSaveProfile(
        media: MediaObject,
        token: String,
        userId: Int,
        callback: SuccessHandler
    ) {
        val photo = ProfilePhoto()
        photo.apply {
            imgId = intArrayOf(media.id)
        }
        profileRepository.savePhoto(userId, token, photo) { isSuccess ->
            if (!isSuccess) {
                //Retry request
                profileRepository.savePhoto(userId, token, photo) {
                    callback(true)
                }
            } else callback(true)
        }
    }


    override fun saveBankCard(card: BankCard) {
        profileStorage.saveBankCard(card)
    }


    override fun getBankCards(): ArrayList<BankCard> {
        initRealm()

        return profileStorage.getBankCards()
    }


    override fun deleteBankCard(card: BankCard) {
        profileStorage.deleteBankCard(card)
    }


    override fun getProfileLocal(): Profile? {
        return profileStorage.getProfile()
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


    override fun closeRealm() {
        profileStorage.closeRealm()
    }


}