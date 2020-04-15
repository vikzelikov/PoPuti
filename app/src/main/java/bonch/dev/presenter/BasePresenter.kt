package bonch.dev.presenter

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.os.Handler
import bonch.dev.MainActivity
import bonch.dev.model.passanger.BaseModel
import bonch.dev.model.passanger.getdriver.DriverInfoModel
import bonch.dev.model.passanger.getdriver.pojo.Driver
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
}