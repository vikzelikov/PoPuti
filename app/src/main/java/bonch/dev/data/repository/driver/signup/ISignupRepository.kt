package bonch.dev.data.repository.driver.signup

import bonch.dev.domain.entities.driver.signup.DriverData
import bonch.dev.domain.entities.driver.signup.DriverDataDTO
import bonch.dev.domain.entities.driver.signup.NewPhoto
import bonch.dev.domain.interactor.driver.signup.SignupHandler
import bonch.dev.presentation.interfaces.NotificationHandler

interface ISignupRepository {

    fun createDriver(
        driverData: DriverData, token: String, userId: Int,
        callback: SignupHandler<DriverDataDTO?>
    )

    fun getDriver(driverId: Int, token: String, callback: SignupHandler<DriverData?>)

    fun putNewPhoto(
        photo: NewPhoto,
        token: String,
        driverId: Int,
        callback: NotificationHandler
    )

}