package bonch.dev.poputi.presentation.modules.driver.getpassenger.presenter

import java.util.*

class WaitingTimer {

    companion object {
        const val WAIT_TIMER = 300L
    }

    var waitingTime = WAIT_TIMER
    var isPaidWaiting = false

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
            timer = null
        }
    }

    // Метод для описания того, что будет происходить при работе таймера (задача для таймера):
    inner class OrdersTimerTask(val presenter: ContractPresenter.ITrackRidePresenter) :
        TimerTask() {
        override fun run() {
            try {
                if (isPaidWaiting) waitingTime++ else waitingTime--

                if (waitingTime == 0L) {
                    isPaidWaiting = true
                }

                presenter.tickTimerWaitPassanger(waitingTime, isPaidWaiting)

            } catch (ex: IndexOutOfBoundsException) {
                println(ex.message)
            }
        }
    }
}