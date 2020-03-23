package bonch.dev.presenter.getdriver

import android.os.CountDownTimer
import android.view.View
import android.view.ViewGroup
import bonch.dev.presenter.getdriver.adapters.DriversListAdapter

class DriverItemTimer(
    startTime: Long,
    interval: Long,
    private val timeLine: View,
    private val adapter: DriversListAdapter
) :
    CountDownTimer(startTime, interval) {


    override fun onFinish() {
        adapter.rejectDriver(null, false)

        val layoutParams: ViewGroup.LayoutParams = timeLine.layoutParams
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
        timeLine.layoutParams = layoutParams
    }

    override fun onTick(millisUntilFinished: Long) {
        val layoutParams: ViewGroup.LayoutParams = timeLine.layoutParams
        layoutParams.width = millisUntilFinished.toInt() / 20
        timeLine.layoutParams = layoutParams
    }

}