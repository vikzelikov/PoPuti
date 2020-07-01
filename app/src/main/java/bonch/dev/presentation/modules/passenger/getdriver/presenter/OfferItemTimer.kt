package bonch.dev.presentation.modules.passenger.getdriver.presenter

import android.os.CountDownTimer
import android.view.View
import android.view.ViewGroup
import bonch.dev.presentation.modules.passenger.getdriver.presenter.OffersMainTimer.DEFAULT_WIDTH

class OfferItemTimer(
    startTime: Long,
    interval: Long,
    private val timeLine: View
) : CountDownTimer(startTime, interval) {

    override fun onFinish() {
        //finish timer
    }

    override fun onTick(millisUntilFinished: Long) {
        //divide 50 for more smooth animation
        val ratio = OffersMainTimer.ratio
        val partWidth = DEFAULT_WIDTH / (OffersMainTimer.TIME_EXPIRED_ITEM * ratio)
        val partTime = (millisUntilFinished / (1000 / ratio))

        val width = partWidth * partTime
        val layoutParams: ViewGroup.LayoutParams = timeLine.layoutParams
        layoutParams.width = width.toInt()
        timeLine.layoutParams = layoutParams
    }

}