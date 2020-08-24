package bonch.dev.poputi.domain.interactor.driver.signup

import bonch.dev.poputi.domain.entities.common.banking.BankCard
import bonch.dev.poputi.domain.entities.common.profile.Profile
import bonch.dev.poputi.domain.entities.driver.signup.DriverData
import bonch.dev.poputi.domain.entities.driver.signup.NewPhoto
import bonch.dev.poputi.presentation.interfaces.DataHandler
import bonch.dev.poputi.presentation.interfaces.SuccessHandler
import java.io.File


interface ISignupInteractor {

    fun loadPhoto(image: File, id: Int, callback: SuccessHandler)

    fun createDriver(driver: DriverData, callback: SuccessHandler)

    fun getDriver(callback: DataHandler<DriverData?>)

    fun getUser(callback: DataHandler<Profile?>)

    fun saveDriverAccess()

    fun saveCheckoutDriver(isDriver: Boolean)

    fun isCheckoutDriver(): Boolean

    fun saveBankCard(bankCard: BankCard)

    fun getBankCards(): ArrayList<BankCard>

    fun deletePhoto(imageId: Int, callback: SuccessHandler)

    fun putNewPhoto(photo: NewPhoto, callback: SuccessHandler)

    fun saveDriverID(driverId: Int)

}