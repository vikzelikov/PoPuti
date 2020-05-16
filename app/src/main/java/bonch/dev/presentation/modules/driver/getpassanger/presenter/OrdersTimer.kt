package bonch.dev.presentation.modules.driver.getpassanger.presenter

import bonch.dev.presentation.modules.driver.getpassanger.adapters.OrdersAdapter
import java.util.*


object OrdersTimer {

    const val TIME_EXPIRED_ITEM = 30 //sec

    private var timer: Timer? = null
    private var mTimerTask: TimerTask? = null


    fun startTimer(adapter: OrdersAdapter) {
        if (timer == null) {
            timer = Timer()
            mTimerTask = OrdersTimerTask(adapter)
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
    private class OrdersTimerTask(val adapter: OrdersAdapter) : TimerTask() {
        override fun run() {
            try {
                val list = adapter.list

                for (i in 0 until list.size) {
                    list[i].time.let {
                        list[i].time -= 1

                        //if timeLine too small, remove item
                        if (it < 1) {
                            adapter.cancel()
                        }
                    }
                }
            } catch (ex: IndexOutOfBoundsException) {
                println(ex.message)
            }
        }
    }
}
