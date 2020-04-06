package bonch.dev

import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import bonch.dev.model.passanger.getdriver.pojo.DriverObject.driver
import bonch.dev.presenter.BasePresenter
import bonch.dev.utils.Constants
import bonch.dev.utils.Constants.GET_DRIVER_VIEW
import bonch.dev.utils.Constants.MAIN_FRAGMENT
import bonch.dev.utils.Constants.PHONE_VIEW
import bonch.dev.utils.Coordinator.addFragment
import bonch.dev.utils.Coordinator.replaceFragment
import bonch.dev.utils.Keyboard.hideKeyboard
import bonch.dev.view.passanger.getdriver.CreateRideView
import bonch.dev.view.passanger.getdriver.GetDriverView
import bonch.dev.view.passanger.signup.ConfirmPhoneView
import bonch.dev.view.passanger.signup.FullNameView
import bonch.dev.view.passanger.signup.PhoneView


class MainActivity : AppCompatActivity() {

    private var basePresenter: BasePresenter? = null


    init {
        if (basePresenter == null) {
            basePresenter = BasePresenter(this)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //check user login
        val accessToken = basePresenter?.getToken(applicationContext)

        if (accessToken == null) {
            //to signup
            addFragment(PHONE_VIEW, supportFragmentManager)
            //addFragment(MAIN_FRAGMENT, supportFragmentManager)
        } else {
            //redirect to full app
            driver = basePresenter?.getDriverData(applicationContext)

            if (driver != null) {
                //ride already created
                replaceFragment(GET_DRIVER_VIEW, null, supportFragmentManager)
            } else {
                //not created
                addFragment(MAIN_FRAGMENT, supportFragmentManager)
            }
        }
    }


    override fun onPause() {
        super.onPause()
        hideKeyboard(this, findViewById<LinearLayout>(R.id.fragment_container))
    }


    override fun onBackPressed() {
        val fm = supportFragmentManager
        val createRideView =
            fm.findFragmentByTag(Constants.CREATE_RIDE_VIEW.toString()) as CreateRideView?
        val getDriverView =
            fm.findFragmentByTag(Constants.GET_DRIVER_VIEW.toString()) as GetDriverView?
        val phoneFragment =
            fm.findFragmentByTag(Constants.PHONE_VIEW.toString()) as PhoneView?
        val confirmPhoneFragment =
            fm.findFragmentByTag(Constants.CONFIRM_PHONE_VIEW.toString()) as ConfirmPhoneView?
        val fullNameFragment =
            fm.findFragmentByTag(Constants.FULL_NAME_VIEW.toString()) as FullNameView?


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
