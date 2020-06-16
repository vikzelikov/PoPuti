package bonch.dev.presentation.modules.passenger.getdriver.presenter

import android.os.CountDownTimer
import android.view.View
import android.view.ViewGroup
import bonch.dev.presentation.modules.passenger.getdriver.presenter.DriverMainTimer.DEFAULT_WIDTH

class DriverItemTimer(
    startTime: Long,
    interval: Long,
    private val timeLine: View
) : CountDownTimer(startTime, interval) {

    override fun onFinish() {
        //finish timer
    }

    override fun onTick(millisUntilFinished: Long) {
        //divide 50 for more smooth animation
        val ratio = DriverMainTimer.ratio
        val partWidth = DEFAULT_WIDTH / (DriverMainTimer.TIME_EXPIRED_ITEM * ratio)
        val partTime = (millisUntilFinished / (1000 / ratio))

        val width = partWidth * partTime
        val layoutParams: ViewGroup.LayoutParams = timeLine.layoutParams
        layoutParams.width = width.toInt()
        timeLine.layoutParams = layoutParams
    }

}