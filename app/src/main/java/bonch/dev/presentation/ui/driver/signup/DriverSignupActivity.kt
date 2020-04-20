package bonch.dev.presentation.driver.signup

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import bonch.dev.R
import bonch.dev.data.driver.signup.SignupMainData
import bonch.dev.presentation.presenter.driver.signup.DriverSignupPresenter
import bonch.dev.utils.Constants

class DriverSignupActivity : AppCompatActivity() {

    private var driverSignupPresenter: DriverSignupPresenter? = null

    init {
        if (driverSignupPresenter == null) {
            driverSignupPresenter = DriverSignupPresenter(this)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.driver_signup_activity)

        driverSignupPresenter?.getStatusSignup()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        driverSignupPresenter?.onActivityResult(
            requestCode,
            resultCode,
            data,
            supportFragmentManager
        )
    }


    override fun onBackPressed() {
        val checkPhotoView =
            supportFragmentManager.findFragmentByTag(Constants.DRIVER_SIGNUP_CHECK_PHOTO.toString()) as CheckPhotoView?

        if (checkPhotoView?.view != null) {
            if (SignupMainData.isTableView) {
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
