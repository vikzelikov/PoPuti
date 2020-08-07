package bonch.dev.poputi.data.repository.driver.signup

import bonch.dev.poputi.domain.entities.driver.signup.DriverData
import bonch.dev.poputi.domain.entities.driver.signup.DriverDataDTO
import bonch.dev.poputi.domain.entities.driver.signup.NewPhoto
import bonch.dev.poputi.presentation.interfaces.DataHandler
import bonch.dev.poputi.presentation.interfaces.SuccessHandler

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