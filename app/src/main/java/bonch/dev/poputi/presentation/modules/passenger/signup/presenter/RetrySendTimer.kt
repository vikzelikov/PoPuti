package bonch.dev.poputi.presentation.modules.passenger.signup.presenter

import android.os.CountDownTimer

object RetrySendTimer {

    var startTime = 15000L
    private const val interval = 1000L
    private var sendTimer: SendTimer? = null
    var seconds: Long? = null

    fun getInstance(presenter: ContractPresenter.IConfirmPhonePresenter): SendTimer? {
        if (sendTimer == null) {
            sendTimer = SendTimer(startTime, interval, presenter)
        }
        return sendTimer
    }


    fun increaseStartTime(interval: Long, presenter: ContractPresenter.IConfirmPhonePresenter) {
        startTime += interval
        sendTimer = SendTimer(startTime, RetrySendTimer.interval, presenter)
    }


    class SendTimer(
        startTime: Long,
        interval: Long,
        val presenter: ContractPresenter.IConfirmPhonePresenter
    ) :
        CountDownTimer(startTime, interval) {

        override fun onFinish() {
            seconds = 0

            presenter.timerFinish()
        }

        override fun onTick(millisUntilFinished: Long) {
            seconds = millisUntilFinished / 1000

            presenter.setViewTimer()
        }

    }

}
