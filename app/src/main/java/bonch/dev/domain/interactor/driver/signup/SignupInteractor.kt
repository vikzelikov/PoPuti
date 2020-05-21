package bonch.dev.domain.interactor.driver.signup

import android.util.Log
import bonch.dev.data.repository.common.media.IMediaRepository
import bonch.dev.data.repository.common.profile.IProfileRepository
import bonch.dev.data.repository.driver.signup.ISignupRepository
import bonch.dev.data.storage.common.profile.IProfileStorage
import bonch.dev.data.storage.driver.signup.ISignupStorage
import bonch.dev.domain.entities.common.profile.Profile
import bonch.dev.domain.entities.driver.signup.DriverData
import bonch.dev.domain.entities.driver.signup.NewPhoto
import bonch.dev.domain.entities.driver.signup.SignupMainData
import bonch.dev.presentation.interfaces.DataHandler
import bonch.dev.presentation.interfaces.SuccessHandler
import bonch.dev.presentation.modules.driver.signup.SignupComponent
import java.io.File
import javax.inject.Inject

class SignupInteractor : ISignupInteractor {

    @Inject
    lateinit var signupRepository: ISignupRepository

    @Inject
    lateinit var mediaRepository: IMediaRepository

    @Inject
    lateinit var profileRepository: IProfileRepository

    @Inject
    lateinit var signupStorage: ISignupStorage

    @Inject
    lateinit var profileStorage: IProfileStorage


    init {
        SignupComponent.driverSignupComponent?.inject(this)
    }


    override fun loadDocs(image: File, id: Int, callback: SuccessHandler) {
        val token = profileStorage.getToken()

        if (token != null) {
            mediaRepository.loadPhoto(token, image) { media, error ->
                if (error != null) {
                    //Retry request
                    mediaRepository.loadPhoto(token, image) { mediaObj, _ ->
                        if (mediaObj != null) {
                            //ok
                            val imageId = mediaObj.id
                            SignupMainData.listDocs[id].id = imageId
                            callback(true)
                        } else {
                            //show error notification
                            SignupMainData.listDocs[id].id = -1
                            callback(false)
                        }
                    }
                } else if (media != null) {
                    //ok
                    callback(true)
                    val imageId = media.id
                    SignupMainData.listDocs[id].id = imageId
                }
            }
        } else {
            //error
            callback(false)
            Log.d(
                "LOAD_PHOTO",
                "Load photo to server failed (token: $token)"
            )
        }
    }


    override fun createDriver(driver: DriverData, callback: SuccessHandler) {
        val token = profileStorage.getToken()
        val userId = profileStorage.getUserId()

        if (token != null && userId != -1) {
            signupRepository.createDriver(driver, token, userId) { driverData, error ->
                if (error != null) {
                    //retry request
                    signupRepository.createDriver(driver, token, userId) { driver, _ ->
                        if (driver?.driver != null) {
                            //ok
                            val driverId = driver.driver?.driverId
                            driverId?.let { profileStorage.saveDriverId(it) }
                            callback(true)
                        } else {
                            //show error notification
                            callback(false)
                        }
                    }
                } else if (driverData?.driver != null) {
                    val driverId = driverData.driver?.driverId
                    driverId?.let { profileStorage.saveDriverId(it) }
                    //ok
                    callback(true)
                }
            }
        } else {
            //error
            callback(false)
        }
    }


    override fun getDriver(callback: DataHandler<DriverData?>) {
        val token = profileStorage.getToken()
        val driverId = profileStorage.getDriverId()

        if (token != null && driverId != -1) {
            signupRepository.getDriver(driverId, token) { driverData, error ->
                if (error != null) {
                    //retry request
                    signupRepository.getDriver(driverId, token) { driver, _ ->
                        if (driver != null) {
                            //ok
                            callback(driver, null)
                        } else {
                            //error
                            callback(null, "Error")
                        }
                    }
                } else if (driverData != null) {
                    //ok
                    callback(driverData, null)
                }
            }
        } else {
            //error
            callback(null, "Error")
        }
    }


    override fun saveDriverAccess() {
        profileStorage.saveDriverAccess()
    }


    override fun isCheckoutDriver(): Boolean {
        return profileStorage.isCheckoutDriver()
    }


    override fun saveCheckoutDriver(isDriver: Boolean) {
        profileStorage.saveCheckoutDriver(isDriver)
    }


    override fun getUser(callback: DataHandler<Profile?>) {
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


    override fun putNewPhoto(photo: NewPhoto, callback: SuccessHandler) {
        val token = profileStorage.getToken()
        val driverId = profileStorage.getDriverId()

        if (token != null && driverId != -1) {
            signupRepository.putNewPhoto(photo, token, driverId) {
                if (it) {
                    //ok
                    callback(true)
                } else {
                    //retry request
                    signupRepository.putNewPhoto(photo, token, driverId) { isSuccess ->
                        if (isSuccess) {
                            //ok
                            callback(true)
                        } else {
                            //error
                            callback(false)
                        }
                    }
                }
            }
        } else {
            //error
            callback(false)
        }
    }


    override fun deletePhoto(imageId: Int, callback: SuccessHandler) {
        val token = profileStorage.getToken()

        if (token != null) {
            mediaRepository.deletePhoto(token, imageId) { isSuccess ->
                if (!isSuccess) {
                    mediaRepository.deletePhoto(token, imageId) {
                        if (it) {
                            //ok
                            callback(true)
                        } else {
                            //error
                            callback(false)
                        }
                    }
                } else {
                    //ok
                    callback(true)
                }
            }
        } else {
            //error
            callback(false)
        }
    }


    override fun saveDriverID(driverId: Int) {
        profileStorage.saveDriverId(driverId)
    }

}