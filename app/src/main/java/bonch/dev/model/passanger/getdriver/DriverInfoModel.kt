package bonch.dev.model.passanger.getdriver

import android.app.Activity
import androidx.preference.PreferenceManager
import bonch.dev.model.passanger.getdriver.pojo.Driver
import bonch.dev.utils.Constants

class DriverInfoModel {


    fun saveDataDriver(activity: Activity, driver: Driver) {
        val pref = PreferenceManager.getDefaultSharedPreferences(activity.applicationContext)
        val editor = pref.edit()

        editor.putString(Constants.NAME_DRIVER, driver.nameDriver)
        editor.putString(Constants.CAR_NAME, driver.carName)
        editor.putString(Constants.CAR_NUMBER, driver.carNumber)
        editor.putInt(Constants.PRICE_DRIVER, driver.price!!)
        editor.putInt(Constants.IMG_DRIVER, driver.imgDriver!!)
        editor.putBoolean(Constants.IS_DRIVER_ARRIVED, driver.isArrived)
        editor.apply()
    }


    fun removeDataDriver(activity: Activity) {
        val pref = PreferenceManager.getDefaultSharedPreferences(activity.applicationContext)

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