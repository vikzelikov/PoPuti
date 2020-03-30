package bonch.dev.presenter.driver.signup

import androidx.fragment.app.FragmentManager
import bonch.dev.utils.Constants.DRIVER_SIGNUP_DOCS_VIEW
import bonch.dev.utils.Constants.DRIVER_SIGNUP_STEP_VIEW
import bonch.dev.utils.Coordinator.replaceFragment
import bonch.dev.view.driver.signup.DriverSignupActivity

class DriverSignupPresenter {

    var activityHost: DriverSignupActivity? = null


    fun getListDocsView(fm: FragmentManager){
        replaceFragment(DRIVER_SIGNUP_DOCS_VIEW, null, fm)
    }


    fun startDriverSignup(fm: FragmentManager){
        replaceFragment(DRIVER_SIGNUP_STEP_VIEW, null, fm)
    }


    fun finish(activity: DriverSignupActivity){
        activity.finish()
    }
}