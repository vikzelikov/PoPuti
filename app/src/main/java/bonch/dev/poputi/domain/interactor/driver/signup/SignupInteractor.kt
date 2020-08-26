package bonch.dev.poputi.domain.interactor.driver.signup

import bonch.dev.poputi.data.repository.common.media.IMediaRepository
import bonch.dev.poputi.data.repository.common.profile.IProfileRepository
import bonch.dev.poputi.data.repository.driver.signup.ISignupRepository
import bonch.dev.poputi.data.storage.common.profile.IProfileStorage
import bonch.dev.poputi.domain.entities.common.banking.BankCard
import bonch.dev.poputi.domain.entities.common.profile.Profile
import bonch.dev.poputi.domain.entities.common.profile.ProfilePhoto
import bonch.dev.poputi.domain.entities.driver.signup.DriverData
import bonch.dev.poputi.domain.entities.driver.signup.NewPhoto
import bonch.dev.poputi.domain.entities.driver.signup.SignupMainData
import bonch.dev.poputi.presentation.interfaces.DataHandler
import bonch.dev.poputi.presentation.interfaces.SuccessHandler
import bonch.dev.poputi.presentation.modules.driver.signup.SignupComponent
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
    lateinit var profileStorage: IProfileStorage


    init {
        SignupComponent.driverSignupComponent?.inject(this)
    }


    override fun loadPhoto(image: File, id: Int, callback: SuccessHandler) {
        val token = profileStorage.getToken()

        if (token != null) {
            mediaRepository.loadPhoto(token, image) { media, _ ->
                if (media != null) {
                    //ok
                    try {
                        val imageId = media.id

                        if (id != -1) {
                            SignupMainData.listDocs[id].imgId = imageId
                            SignupMainData.listDocs[id].id = imageId
                        } else loadProfilePhoto(imageId)

                        callback(true)
                    } catch (ex: IndexOutOfBoundsException) {
                        callback(false)
                    }
                } else callback(false)
            }
        } else {
            //error
            callback(false)
        }
    }


    private fun loadProfilePhoto(imageId: Int) {
        val token = profileStorage.getToken()
        val userId = profileStorage.getUserId()
        val profilePhoto = ProfilePhoto(intArrayOf(imageId))

        if (token != null && userId != -1) {
            profileRepository.savePhoto(userId, token, profilePhoto) { isSuccess ->
                if (!isSuccess) {
                    profileRepository.savePhoto(userId, token, profilePhoto) {}
                }
            }
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
                    signupRepository.putNewPhoto(photo, token, driverId, callback)
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


    override fun saveBankCard(bankCard: BankCard) {
        profileStorage.saveBankCard(bankCard)
    }


    override fun getBankCards(): ArrayList<BankCard> {
        profileStorage.initRealm()

        val list = profileStorage.getBankCards()
        val result = arrayListOf<BankCard>()
        list.forEach {
            val tempCard = BankCard()

            tempCard.apply {
                id = it.id
                numberCard = it.numberCard
                validUntil = it.validUntil
                cvc = it.cvc
                isSelect = it.isSelect
            }

            result.add(tempCard)
        }

        return result
    }

}