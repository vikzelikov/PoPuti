package bonch.dev.domain.interactor.driver.signup

import bonch.dev.domain.entities.common.profile.Profile
import bonch.dev.domain.entities.driver.signup.DriverData
import bonch.dev.domain.entities.driver.signup.NewPhoto
import bonch.dev.presentation.interfaces.DataHandler
import bonch.dev.presentation.interfaces.SuccessHandler
import java.io.File


interface ISignupInteractor {

    fun loadDocs(image: File, id: Int, callback: SuccessHandler)

    fun createDriver(driver: DriverData, callback: SuccessHandler)

    fun getDriver(callback: DataHandler<DriverData?>)

    fun getUser(callback: DataHandler<Profile?>)

    fun saveDriverAccess()

    fun saveCheckoutDriver(isDriver: Boolean)

    fun isCheckoutDriver(): Boolean

    fun deletePhoto(imageId: Int, callback: SuccessHandler)

    fun putNewPhoto(photo: NewPhoto, callback: SuccessHandler)

    fun saveDriverID(driverId: Int)

}