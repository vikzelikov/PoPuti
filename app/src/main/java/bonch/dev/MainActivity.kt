package bonch.dev

import android.content.pm.PackageManager
import android.os.Bundle
import android.text.TextUtils.replace
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import bonch.dev.model.getdriver.pojo.Driver
import bonch.dev.model.getdriver.pojo.DriverObject.driver
import bonch.dev.utils.Constants
import bonch.dev.utils.Constants.CONFIRM_PHONE_VIEW
import bonch.dev.utils.Constants.CREATE_RIDE_VIEW
import bonch.dev.utils.Constants.FULL_NAME_VIEW
import bonch.dev.utils.Constants.GET_DRIVER_VIEW
import bonch.dev.utils.Constants.LOCATION_PERMISSION_NAME
import bonch.dev.utils.Constants.LOCATION_PERMISSION_REQUEST
import bonch.dev.utils.Constants.MAIN_FRAGMENT
import bonch.dev.utils.Constants.PHONE_VIEW
import bonch.dev.utils.Coordinator.addFragment
import bonch.dev.utils.Coordinator.replaceFragment
import bonch.dev.utils.Keyboard.hideKeyboard
import bonch.dev.view.getdriver.CreateRideView
import bonch.dev.view.getdriver.GetDriverView
import bonch.dev.view.signup.ConfirmPhoneFragment
import bonch.dev.view.signup.FullNameFragment
import bonch.dev.view.signup.PhoneFragment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        accessUserGeo()

        val accessToken = getToken()

        if (accessToken == null) {
            //to signup
            addFragment(PHONE_VIEW, supportFragmentManager)
        } else {
            //redirect to full app
            driver = getDriverData()

            if(driver != null){
                //ride already created
                replaceFragment(GET_DRIVER_VIEW, null, supportFragmentManager)
            }else{
                //not created
                addFragment(MAIN_FRAGMENT, supportFragmentManager)
            }
        }
    }


    private fun getToken(): String? {
        val caseType = object : TypeToken<String>() {}.type
        val pref = getPreferences(MODE_PRIVATE)

        var accessToken: String? = null
        if (pref.contains(Constants.ACCESS_TOKEN)) {
            val json: String? = pref.getString(Constants.ACCESS_TOKEN, "")
            accessToken = Gson().fromJson(json, caseType)
        }

        return accessToken
    }


    private fun getDriverData(): Driver?{
        val caseType = object : TypeToken<String>() {}.type
        val pref = getPreferences(MODE_PRIVATE)

        var driver: Driver? = null

        if (pref.contains(Constants.NAME_DRIVER)) {
            driver = Driver()

            val nameDriver: String? = pref.getString(Constants.NAME_DRIVER, "")
            val carName: String? = pref.getString(Constants.CAR_NAME, "")
            val carNumber: String? = pref.getString(Constants.CAR_NUMBER, "")
            val price: Int? = pref.getInt(Constants.PRICE_DRIVER, 0)
            val imgDriver: Int? = pref.getInt(Constants.IMG_DRIVER, 0)
            val isDriverArrived: Boolean = pref.getBoolean(Constants.IS_DRIVER_ARRIVED, false)

            driver.nameDriver = Gson().fromJson(nameDriver, caseType)
            driver.carName = Gson().fromJson(carName, caseType)
            driver.carNumber = Gson().fromJson(carNumber, caseType)
            driver.price = price
            driver.imgDriver = imgDriver
            driver.isArrived = isDriverArrived
        }

        return driver
    }


    private fun accessUserGeo() {
        if (ContextCompat.checkSelfPermission(this, LOCATION_PERMISSION_NAME)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(LOCATION_PERMISSION_NAME),
                LOCATION_PERMISSION_REQUEST
            )
        }
    }


    override fun onPause() {
        super.onPause()
        hideKeyboard(this, findViewById<LinearLayout>(R.id.fragment_container))
    }


    override fun onBackPressed() {
        val createRideView =
            supportFragmentManager.findFragmentByTag(CREATE_RIDE_VIEW.toString()) as CreateRideView?
        val getDriverView =
            supportFragmentManager.findFragmentByTag(GET_DRIVER_VIEW.toString()) as GetDriverView?
        val phoneFragment =
            supportFragmentManager.findFragmentByTag(PHONE_VIEW.toString()) as PhoneFragment?
        val confirmPhoneFragment =
            supportFragmentManager.findFragmentByTag(CONFIRM_PHONE_VIEW.toString()) as ConfirmPhoneFragment?
        val fullNameFragment =
            supportFragmentManager.findFragmentByTag(FULL_NAME_VIEW.toString()) as FullNameFragment?


        if (createRideView?.view != null && createRideView.backPressed()) {
            super.onBackPressed()
        }

        if (phoneFragment?.view != null) {
            super.onBackPressed()
        }

        if (confirmPhoneFragment?.view != null) {
            super.onBackPressed()
        }

        if (fullNameFragment?.view != null) {
            super.onBackPressed()
        }

        if (getDriverView?.view != null && getDriverView.backPressed()) {
            super.onBackPressed()
        }

    }
}
