package bonch.dev.view.driver.signup

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import bonch.dev.R
import bonch.dev.presenter.driver.signup.DriverSignupPresenter
import bonch.dev.utils.Constants.DRIVER_SIGNUP_COMPLETE
import bonch.dev.utils.Constants.DRIVER_SIGNUP_PROCESS
import bonch.dev.utils.Constants.DRIVER_SIGNUP_START
import kotlinx.android.synthetic.main.driver_signup_activity.*

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

            }

            DRIVER_SIGNUP_COMPLETE -> {
                //go to driver interface
            }
        }
    }


    private fun getStatusSignup(): Int {
        return 0
    }
}
