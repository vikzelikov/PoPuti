package bonch.dev.data.repository.driver.signup

import bonch.dev.domain.entities.driver.signup.DriverData
import bonch.dev.domain.entities.driver.signup.DriverDataDTO
import bonch.dev.domain.entities.driver.signup.NewPhoto
import bonch.dev.presentation.interfaces.DataHandler
import bonch.dev.presentation.interfaces.SuccessHandler

interface ISignupRepository {

    fun createDriver(
        driverData: DriverData, token: String, userId: Int,
        callback: DataHandler<DriverDataDTO?>
    )

    fun getDriver(driverId: Int, token: String, callback: DataHandler<DriverData?>)

    fun putNewPhoto(
        photo: NewPhoto,
        token: String,
        driverId: Int,
        callback: SuccessHandler
    )

}