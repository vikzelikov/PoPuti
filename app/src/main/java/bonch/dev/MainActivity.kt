package bonch.dev

import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager.getDefaultSharedPreferences
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
import bonch.dev.utils.Constants.WRITE_EXTERNAL_STORAGE
import bonch.dev.utils.Constants.WRITE_EXTERNAL_STORAGE_REQUEST
import bonch.dev.utils.Coordinator.addFragment
import bonch.dev.utils.Coordinator.replaceFragment
import bonch.dev.utils.Keyboard.hideKeyboard
import bonch.dev.view.getdriver.CreateRideView
import bonch.dev.view.getdriver.GetDriverView
import bonch.dev.view.signup.ConfirmPhoneView
import bonch.dev.view.signup.FullNameView
import bonch.dev.view.signup.PhoneView


class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        accessPermission()

        val accessToken = getToken()

        if (accessToken == null) {
            //to signup
            //addFragment(PHONE_VIEW, supportFragmentManager)
            addFragment(MAIN_FRAGMENT, supportFragmentManager)

        } else {
            //redirect to full app
            driver = getDriverData()

            if (driver != null) {
                //ride already created
                replaceFragment(GET_DRIVER_VIEW, null, supportFragmentManager)
            } else {
                //not created
                addFragment(MAIN_FRAGMENT, supportFragmentManager)

//                supportFragmentManager.beginTransaction()
//                    .replace(
//                        R.id.fragment_container,
//                        RegularDriveView(),
//                        CREATE_RIDE_VIEW.toString()
//                    )
//                    .commit()
            }
        }
    }


    private fun getToken(): String? {
        val pref = getDefaultSharedPreferences(applicationContext)
        return pref.getString(Constants.ACCESS_TOKEN, null)
    }


    private fun getDriverData(): Driver? {
        val pref = getDefaultSharedPreferences(applicationContext)

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


    private fun accessPermission() {
        accessUserGeo()
        //accessWriteStorage()
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

        while (true){
            //wait to consider of user
            val permission = ContextCompat.checkSelfPermission(this, LOCATION_PERMISSION_NAME)

            if(permission == PackageManager.PERMISSION_GRANTED){
                break
            }
        }
    }


    private fun accessWriteStorage() {
        if (ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(WRITE_EXTERNAL_STORAGE),
                WRITE_EXTERNAL_STORAGE_REQUEST
            )
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        accessUserGeo()
        accessWriteStorage()
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
            supportFragmentManager.findFragmentByTag(PHONE_VIEW.toString()) as PhoneView?
        val confirmPhoneFragment =
            supportFragmentManager.findFragmentByTag(CONFIRM_PHONE_VIEW.toString()) as ConfirmPhoneView?
        val fullNameFragment =
            supportFragmentManager.findFragmentByTag(FULL_NAME_VIEW.toString()) as FullNameView?


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
