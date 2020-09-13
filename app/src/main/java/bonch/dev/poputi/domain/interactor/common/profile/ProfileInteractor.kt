package bonch.dev.poputi.domain.interactor.common.profile

import android.util.Log
import bonch.dev.poputi.data.repository.common.media.IMediaRepository
import bonch.dev.poputi.data.repository.common.profile.IProfileRepository
import bonch.dev.poputi.data.repository.common.rate.IRateRideRepository
import bonch.dev.poputi.data.storage.common.profile.IProfileStorage
import bonch.dev.poputi.domain.entities.common.banking.BankCard
import bonch.dev.poputi.domain.entities.common.profile.CacheProfile
import bonch.dev.poputi.domain.entities.common.profile.Profile
import bonch.dev.poputi.domain.entities.common.profile.ProfilePhoto
import bonch.dev.poputi.domain.entities.common.rate.Review
import bonch.dev.poputi.domain.entities.common.ride.Address
import bonch.dev.poputi.domain.entities.common.ride.RideInfo
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

    @Inject
    lateinit var ratingRepository: IRateRideRepository


    init {
        ProfileComponent.profileComponent?.inject(this)
    }


    override fun initRealm() {
        profileStorage.initRealm()
    }


    override fun removeProfileData() {
        profileStorage.removeProfile()
    }


    override fun saveProfile(profile: Profile) {
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


    override fun getProfile(callback: DataHandler<Profile?>) {
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


    override fun uploadPhoto(image: File, isAva: Boolean, callback: SuccessHandler) {
        val token = profileStorage.getToken()
        val userId = profileStorage.getUserId()

        if (token != null && userId != -1) {
            mediaRepository.loadPhoto(token, image) { media, _ ->
                if (media != null) {
                    val imageId = media.id

                    if (isAva) {
                        CacheProfile.profile?.photos?.forEachIndexed { index, photo ->
                            if (photo.imgName == "photo") {
                                //update image id for deleting in the future
                                CacheProfile.profile?.photos?.get(index)?.id = imageId
                            }
                        }
                    }

                    remoteSaveProfile(imageId, token, userId, callback)

                } else callback(false)
            }
        } else callback(false)
    }


    override fun deletePhoto(imageId: Int, callback: SuccessHandler) {
        val token = profileStorage.getToken()

        if (token != null) {
            mediaRepository.deletePhoto(token, imageId, callback)
        } else {
            //error
            callback(false)
        }
    }


    private fun remoteSaveProfile(
        imageId: Int,
        token: String,
        userId: Int,
        callback: SuccessHandler
    ) {
        val photo = ProfilePhoto()
        photo.imgId = intArrayOf(imageId)

        profileRepository.savePhoto(userId, token, photo, callback)
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


    override fun saveCheckoutDriver(isDriver: Boolean) {
        profileStorage.saveCheckoutDriver(isDriver)
    }


    override fun isCheckoutDriver(): Boolean {
        return profileStorage.isCheckoutDriver()
    }


    override fun closeRealm() {
        profileStorage.closeRealm()
    }


    /**
     * STORY RIDES
     * */
    override fun getStoryRidesPassenger(callback: DataHandler<ArrayList<RideInfo>?>) {
        val token = profileStorage.getToken()

        if (token != null) {
            profileRepository.getStoryRidesPassenger(token, callback)
        } else callback(null, "error")
    }


    override fun getStoryRidesDriver(callback: DataHandler<ArrayList<RideInfo>?>) {
        val token = profileStorage.getToken()

        if (token != null) {
            profileRepository.getStoryRidesDriver(token, callback)
        } else callback(null, "error")
    }


    /**
     * RATING PASSENGER
     * */
    override fun getRating(callback: DataHandler<ArrayList<Review>?>) {
        val token = profileStorage.getToken()

        if (token != null) {
            ratingRepository.getRating(token, callback)

        } else callback(null, "error")
    }


    /**
     * CITY DETECT
     * */
    override fun saveMyCity(address: Address) {
        val userId = profileStorage.getUserId()
        val token = profileStorage.getToken()

        profileStorage.saveMyCity(address)

        if (token != null && userId != -1)
            profileRepository.updateCity(address, userId, token)
    }


    override fun getMyCity(): Address? {
        return profileStorage.getMyCity()
    }


    /**
     * ONBOARDING
     * */
    override fun saveOnboardingPassenger() {
        profileStorage.saveOnboardingPassenger()
    }


    override fun getOnboardingPassenger(): Boolean {
        return profileStorage.getOnboardingPassenger()
    }


    override fun saveOnboardingDriver() {
        profileStorage.saveOnboardingDriver()
    }


    override fun getOnboardingDriver(): Boolean {
        return profileStorage.getOnboardingDriver()
    }
}