package bonch.dev.presentation.modules.passanger.signup.presenter

import android.os.CountDownTimer
import android.widget.TextView
import androidx.fragment.app.Fragment
import bonch.dev.R
import bonch.dev.domain.utils.Constants.SIGNUP_INTERVAL_SMS
import bonch.dev.presentation.modules.passanger.signup.view.ConfirmPhoneView
import kotlinx.android.synthetic.main.confirm_phone_fragment.view.*

object RetrySendTimer {

    var startTime = SIGNUP_INTERVAL_SMS
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

        private var retryBtn: TextView? = null


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
