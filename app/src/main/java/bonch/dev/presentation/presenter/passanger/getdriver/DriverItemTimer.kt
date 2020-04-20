package bonch.dev.presentation.presenter.passanger.getdriver

import android.os.CountDownTimer
import android.view.View
import android.view.ViewGroup
import bonch.dev.utils.Constants.TIMER_USER_GET_DRIVER

class DriverItemTimer(
    startTime: Long,
    interval: Long,
    private val timeLine: View
) :
    CountDownTimer(startTime, interval) {


    override fun onFinish() {
        //finish timer
    }

    override fun onTick(millisUntilFinished: Long) {
        val layoutParams: ViewGroup.LayoutParams = timeLine.layoutParams
        layoutParams.width = millisUntilFinished.toInt() / TIMER_USER_GET_DRIVER
        timeLine.layoutParams = layoutParams
    }

}