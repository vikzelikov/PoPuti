package bonch.dev.data.repository.passanger.getdriver

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import bonch.dev.data.repository.passanger.getdriver.pojo.Driver
import bonch.dev.utils.Constants

class DriverInfoModel() {

    private lateinit var pref: SharedPreferences

    fun initSP(context: Context) {
        pref = PreferenceManager.getDefaultSharedPreferences(context)
    }


    fun saveDataDriver(driver: Driver) {
        val editor = pref.edit()

        editor.putString(Constants.NAME_DRIVER, driver.nameDriver)
        editor.putString(Constants.CAR_NAME, driver.carName)
        editor.putString(Constants.CAR_NUMBER, driver.carNumber)
        editor.putInt(Constants.PRICE_DRIVER, driver.price!!)
        editor.putInt(Constants.IMG_DRIVER, driver.imgDriver!!)
        editor.putBoolean(Constants.IS_DRIVER_ARRIVED, driver.isArrived)
        editor.apply()
    }


    fun getDriverData(): Driver? {
        if (pref.contains(Constants.NAME_DRIVER)) {
            val driver = Driver()

            val nameDriver: String? = pref.getString(Constants.NAME_DRIVER, null)
            val carName: String? = pref.getString(Constants.CAR_NAME, null)
            val carNumber: String? = pref.getString(Constants.CAR_NUMBER, null)
            val price: Int? = pref.getInt(Constants.PRICE_DRIVER, 0)
            val imgDriver: Int? = pref.getInt(Constants.IMG_DRIVER, 0)
            val isDriverArrived: Boolean = pref.getBoolean(Constants.IS_DRIVER_ARRIVED, false)

            driver.nameDriver = nameDriver
            driver.carName = carName
            driver.carNumber = carNumber
            driver.price = price
            driver.imgDriver = imgDriver
            driver.isArrived = isDriverArrived

            return driver
        } else {
            return null
        }
    }


    fun removeDataDriver() {
        val editor = pref.edit()
        editor.remove(Constants.NAME_DRIVER)
        editor.remove(Constants.CAR_NAME)
        editor.remove(Constants.CAR_NUMBER)
        editor.remove(Constants.PRICE_DRIVER)
        editor.remove(Constants.IMG_DRIVER)
        editor.remove(Constants.IS_DRIVER_ARRIVED)
        editor.apply()
    }

}