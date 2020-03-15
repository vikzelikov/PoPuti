package bonch.dev

import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import bonch.dev.utils.Constants
import bonch.dev.utils.Constants.CONFIRM_PHONE_VIEW
import bonch.dev.utils.Constants.FULL_NAME_VIEW
import bonch.dev.utils.Constants.GET_DRIVER_VIEW
import bonch.dev.utils.Constants.LOCATION_PERMISSION_NAME
import bonch.dev.utils.Constants.LOCATION_PERMISSION_REQUEST
import bonch.dev.utils.Constants.MAIN_FRAGMENT
import bonch.dev.utils.Constants.PHONE_VIEW
import bonch.dev.utils.Coordinator.addFragment
import bonch.dev.utils.Keyboard.hideKeyboard
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
            addFragment(MAIN_FRAGMENT, supportFragmentManager)
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
        val getDriverView =
            supportFragmentManager.findFragmentByTag(GET_DRIVER_VIEW.toString()) as GetDriverView?
        val phoneFragment =
            supportFragmentManager.findFragmentByTag(PHONE_VIEW.toString()) as PhoneFragment?
        val confirmPhoneFragment =
            supportFragmentManager.findFragmentByTag(CONFIRM_PHONE_VIEW.toString()) as ConfirmPhoneFragment?
        val fullNameFragment =
            supportFragmentManager.findFragmentByTag(FULL_NAME_VIEW.toString()) as FullNameFragment?


        if (getDriverView?.view != null && getDriverView.backPressed()) {
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

    }
}
