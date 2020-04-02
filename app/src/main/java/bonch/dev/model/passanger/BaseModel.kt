package bonch.dev.model.passanger

import android.content.Context
import androidx.preference.PreferenceManager
import bonch.dev.model.passanger.getdriver.pojo.Driver
import bonch.dev.utils.Constants

class BaseModel {

     fun getToken(context: Context): String? {
        val pref = PreferenceManager.getDefaultSharedPreferences(context)
        return pref.getString(Constants.ACCESS_TOKEN, null)
    }


     fun getDriverData(context: Context): Driver? {
        val pref = PreferenceManager.getDefaultSharedPreferences(context)

        var driver: Driver? = null

        if (pref.contains(Constants.NAME_DRIVER)) {
            driver = Driver()

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
        }

        return driver
    }
}