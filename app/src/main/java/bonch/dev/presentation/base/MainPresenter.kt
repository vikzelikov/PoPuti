package bonch.dev.presentation.base

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.os.Handler
import bonch.dev.MainActivity
import bonch.dev.data.MainRepository
import bonch.dev.data.repository.passanger.getdriver.DriverInfoModel
import bonch.dev.data.repository.passanger.getdriver.pojo.Driver
import bonch.dev.domain.utils.Constants
import bonch.dev.presentation.modules.passanger.getdriver.ride.view.CreateRideView
import bonch.dev.presentation.modules.passanger.getdriver.ride.view.GetDriverView
import bonch.dev.presentation.modules.passanger.signup.view.ConfirmPhoneView
import bonch.dev.presentation.modules.passanger.signup.view.FullNameView
import bonch.dev.presentation.modules.passanger.signup.view.PhoneView
import kotlinx.android.synthetic.main.activity_main.*

class MainPresenter(private val mainActivity: MainActivity) {

    private var mainRepository: MainRepository? = null
    private var driverInfoModel: DriverInfoModel? = null
    private var handlerAnimation: Handler? = null


    init {
        mainRepository = MainRepository()
        driverInfoModel = DriverInfoModel()
    }

    fun getToken(context: Context): String? {
        return mainRepository?.getToken(context)
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