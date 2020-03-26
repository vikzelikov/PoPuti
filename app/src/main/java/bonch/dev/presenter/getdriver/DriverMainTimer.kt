package bonch.dev.presenter.getdriver

import android.os.CountDownTimer
import bonch.dev.model.getdriver.pojo.Driver
import bonch.dev.presenter.getdriver.adapters.DriversListAdapter
import bonch.dev.utils.Constants.MAX_TIME_GET_DRIVER


object DriverMainTimer {

    private var testTimer: DriverTimer? = null

    fun getInstance(list: ArrayList<Driver>, adapter: DriversListAdapter): DriverTimer? {
        if (testTimer == null) {
            //interval 18 ~ 30 sec
            testTimer = DriverTimer(60000 * MAX_TIME_GET_DRIVER, 18, list, adapter)
        }

        return testTimer
    }


    fun getInstance(): DriverTimer? {
        return testTimer
    }


    class DriverTimer(
        startTime: Long,
        interval: Long,
        val list: ArrayList<Driver>,
        val adapter: DriversListAdapter
    ) :
        CountDownTimer(startTime, interval) {

        override fun onFinish() {
            adapter.getDriverPresenter.getExpiredTimeConfirm()
        }

        override fun onTick(millisUntilFinished: Long) {
            try {
                for (i in 0 until list.size) {
                    list[i].timeLine?.let {
                        list[i].timeLine = it.dec()

                        //if timeLine too small, remove item
                        if (it < 50) {
                            adapter.rejectDriver(null, false)
                        }
                    }
                }
            } catch (ex: IndexOutOfBoundsException) {
                println(ex.message)
            }
        }
    }
}
