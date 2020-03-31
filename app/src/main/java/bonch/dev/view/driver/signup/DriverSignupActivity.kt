package bonch.dev.view.driver.signup

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager.getDefaultSharedPreferences
import bonch.dev.R
import bonch.dev.model.driver.signup.pojo.SignupStep
import bonch.dev.presenter.driver.signup.DriverSignupPresenter
import bonch.dev.utils.Constants
import bonch.dev.utils.Constants.DRIVER_SIGNUP_COMPLETE
import bonch.dev.utils.Constants.DRIVER_SIGNUP_PROCESS
import bonch.dev.utils.Constants.DRIVER_SIGNUP_START

class DriverSignupActivity : AppCompatActivity() {

    private var driverSignupPresenter: DriverSignupPresenter? = null

    init {
        if (driverSignupPresenter == null) {
            driverSignupPresenter = DriverSignupPresenter()
            driverSignupPresenter!!.activityHost = this
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.driver_signup_activity)

        val status = getStatusSignup()

        when (status) {
            DRIVER_SIGNUP_START -> {
                driverSignupPresenter?.getListDocsView(supportFragmentManager)
            }

            DRIVER_SIGNUP_PROCESS -> {
                driverSignupPresenter?.getTableDocs(supportFragmentManager)
            }

            DRIVER_SIGNUP_COMPLETE -> {
                //go to driver interface
            }
        }
    }


    private fun getStatusSignup(): Int {
        var status = 0

        val pref = getDefaultSharedPreferences(applicationContext)
        val isProcess = pref.getBoolean(DRIVER_SIGNUP_PROCESS.toString(), false)
        val isComplete = pref.getBoolean(DRIVER_SIGNUP_COMPLETE.toString(), false)

        if(isProcess){
            status = DRIVER_SIGNUP_PROCESS
        }

        if(isComplete){
            status = DRIVER_SIGNUP_COMPLETE
        }


        return status
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        driverSignupPresenter?.onActivityResult(resultCode, supportFragmentManager)
    }


    override fun onBackPressed() {
        val checkPhotoView =
            supportFragmentManager.findFragmentByTag(Constants.DRIVER_SIGNUP_CHECK_PHOTO.toString()) as CheckPhotoView?

        if (checkPhotoView?.view != null) {
            if (SignupStep.isTableView) {
                driverSignupPresenter?.getTableDocs(supportFragmentManager)
            } else {
                driverSignupPresenter?.startSettingDocs(supportFragmentManager)
            }
        } else {
            super.onBackPressed()
        }
    }


    override fun onDestroy() {
        driverSignupPresenter?.clearData()
        super.onDestroy()
    }
}
