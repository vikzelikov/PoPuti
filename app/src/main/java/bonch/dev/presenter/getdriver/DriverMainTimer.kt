package bonch.dev.presenter.getdriver

import android.os.CountDownTimer
import bonch.dev.model.getdriver.pojo.Driver

object DriverMainTimer {

    private var testTimer: DriverTimer? = null

    fun getInstance(list: ArrayList<Driver>): DriverTimer? {
        if (testTimer == null) {
            testTimer = DriverTimer(60000 * 3, 10, list)
        }
        return testTimer
    }


    class DriverTimer(startTime: Long, interval: Long, val list: ArrayList<Driver>) :
        CountDownTimer(startTime, interval) {

        override fun onFinish() {
            //TODO
            //show alert
            println("Пользователь не смог выбрать водителя для поездки")
        }

        override fun onTick(millisUntilFinished: Long) {
            try {
                for (i in 0 until list.size){
                    if(list[i].timeLine != null){
                        list[i].timeLine = list[i].timeLine!!.dec()
                    }
                }
            } catch (ex: IndexOutOfBoundsException) {
                println(ex.message)
            }
        }
    }
}
