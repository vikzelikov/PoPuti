package bonch.dev.presenter

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.os.Handler
import bonch.dev.MainActivity
import bonch.dev.model.passanger.BaseModel
import bonch.dev.model.passanger.getdriver.DriverInfoModel
import bonch.dev.model.passanger.getdriver.pojo.Driver
import bonch.dev.utils.Constants
import bonch.dev.view.passanger.getdriver.CreateRideView
import bonch.dev.view.passanger.getdriver.GetDriverView
import bonch.dev.view.passanger.signup.ConfirmPhoneView
import bonch.dev.view.passanger.signup.FullNameView
import bonch.dev.view.passanger.signup.PhoneView
import kotlinx.android.synthetic.main.activity_main.*

class BasePresenter(private val mainActivity: MainActivity) {

    private var baseModel: BaseModel? = null
    private var driverInfoModel: DriverInfoModel? = null
    private var handlerAnimation: Handler? = null


    init {
        baseModel = BaseModel()
        driverInfoModel = DriverInfoModel()
    }

    fun getToken(context: Context): String? {
        return baseModel?.getToken(context)
    }


    fun getDriverData(context: Context): Driver? {
        driverInfoModel?.initSP(context)
        return driverInfoModel?.getDriverData()
    }


    fun showNotification(text: String) {
        val view = mainActivity.general_notification

        view.text = text
        handlerAnimation?.removeCallbacksAndMessages(null)
        handlerAnimation = Handler()
        view.translationY = 0.0f
        view.alpha = 0.0f

        view.animate()
            .setDuration(500L)
            .translationY(100f)
            .alpha(1.0f)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    handlerAnimation?.postDelayed({ hideNotifications() }, 2000)
                }
            })
    }


    private fun hideNotifications() {
        val view = mainActivity.general_notification

        view.animate()
            .setDuration(500L)
            .translationY(-100f)
            .alpha(0.0f)
    }


    fun onBackPressed(){
        val fm = mainActivity.supportFragmentManager
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


        if (createRideView?.view != null && createRideView.onBackPressed()) {
            mainActivity.pressBack()
        }

        if (phoneFragment?.view != null) {
            mainActivity.pressBack()
        }

        if (confirmPhoneFragment?.view != null) {
            mainActivity.pressBack()
        }

        if (fullNameFragment?.view != null) {
            mainActivity.pressBack()
        }

        if (getDriverView?.view != null && getDriverView.onBackPressed()) {
            mainActivity.pressBack()
        }
    }
}