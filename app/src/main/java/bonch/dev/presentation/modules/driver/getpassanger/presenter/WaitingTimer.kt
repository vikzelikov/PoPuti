package bonch.dev.presentation.modules.driver.getpassanger.presenter

import java.util.*

class WaitingTimer {

    private var freeWating = 60
    private var isPaidWating = false
    private var timer: Timer? = null
    private var mTimerTask: TimerTask? = null

    fun startTimer(presenter: ContractPresenter.ITrackRidePresenter) {
        if (timer == null) {
            timer = Timer()
            mTimerTask = OrdersTimerTask(presenter)
            timer?.schedule(mTimerTask, 0, 1000)
        }
    }


    fun cancelTimer() {
        if (timer != null) {
            timer?.cancel()
            timer = null;
        }
    }

    // Метод для описания того, что будет происходить при работе таймера (задача для таймера):
    inner class OrdersTimerTask(val presenter: ContractPresenter.ITrackRidePresenter) :
        TimerTask() {
        override fun run() {
            try {
                if (isPaidWating) freeWating++ else freeWating--

                if (freeWating == 0) {
                    isPaidWating = true
                }

                presenter.tickTimerWaitPassanger(freeWating, isPaidWating)

            } catch (ex: IndexOutOfBoundsException) {
                println(ex.message)
            }
        }
    }
}