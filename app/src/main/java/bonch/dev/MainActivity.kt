package bonch.dev

import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import bonch.dev.utils.Constants
import bonch.dev.utils.Constants.CONFIRM_PHONE_VIEW
import bonch.dev.utils.Constants.FULL_NAME_VIEW
import bonch.dev.utils.Constants.GET_DRIVER_VIEW
import bonch.dev.utils.Constants.LOCATION_PERMISSION_NAME
import bonch.dev.utils.Constants.LOCATION_PERMISSION_REQUEST
import bonch.dev.utils.Constants.PHONE_VIEW
import bonch.dev.utils.Keyboard.hideKeyboard
import bonch.dev.view.MainFragment
import bonch.dev.view.getdriver.GetDriverView
import bonch.dev.view.signup.ConfirmPhoneFragment
import bonch.dev.view.signup.FullNameFragment
import bonch.dev.view.signup.PhoneFragment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class MainActivity : AppCompatActivity() {

    private val caseType = object : TypeToken<String>() {}.type
    private lateinit var gson: Gson
    private lateinit var pref: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        accessUserGeo()

        val accessToken = getToken()
        val fragment: Fragment?

        fragment = if (accessToken == null) {
            PhoneFragment()
        } else {
            MainFragment()
        }

        supportFragmentManager.beginTransaction()
            .add(R.id.fragment_container, fragment, PHONE_VIEW.toString())
            .commit()

    }


    private fun getToken(): String? {
        pref = getPreferences(MODE_PRIVATE)
        gson = Gson()

        var accessToken: String? = null
        if (pref.contains(Constants.ACCESS_TOKEN)) {
            val json: String? = pref.getString(Constants.ACCESS_TOKEN, "")
            accessToken = gson.fromJson(json, caseType)
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
