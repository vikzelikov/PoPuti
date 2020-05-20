package bonch.dev.domain.interactor.driver.signup

import bonch.dev.domain.entities.common.profile.Profile
import bonch.dev.domain.entities.driver.signup.DriverData
import bonch.dev.domain.entities.driver.signup.NewPhoto
import bonch.dev.presentation.interfaces.NotificationHandler
import java.io.File

typealias SignupHandler<T> = (data: T, error: String?) -> Unit

interface ISignupInteractor {

    fun loadDocs(image: File, id: Int, callback: NotificationHandler)

    fun createDriver(driver: DriverData, callback: NotificationHandler)

    fun getDriver(callback: SignupHandler<DriverData?>)

    fun getUser(callback: SignupHandler<Profile?>)

    fun saveDriverAccess()

    fun saveCheckoutDriver(isDriver: Boolean)

    fun isCheckoutDriver(): Boolean

    fun deletePhoto(imageId: Int, callback: NotificationHandler)

    fun putNewPhoto(photo: NewPhoto, callback: NotificationHandler)

    fun saveDriverID(driverId: Int)

}