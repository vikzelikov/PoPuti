package bonch.dev.presenter.passanger.signup

import android.os.CountDownTimer
import android.widget.TextView
import androidx.fragment.app.Fragment
import bonch.dev.R
import bonch.dev.utils.Constants.SIGNUP_INTERVAL_SMS
import bonch.dev.view.passanger.signup.ConfirmPhoneView
import kotlinx.android.synthetic.main.confirm_phone_fragment.view.*

object RetrySendTimer {

    var startTime = SIGNUP_INTERVAL_SMS
    private const val interval = 1000L
    private var sendTimer: SendTimer? = null
    var seconds: Long? = null

    fun getInstance(view: ConfirmPhoneView): SendTimer? {
        if (sendTimer == null) {
            sendTimer = SendTimer(startTime, interval, view)
        }
        return sendTimer
    }


    fun increaseStartTime(interval: Long, view: ConfirmPhoneView){
        startTime += interval
        sendTimer = SendTimer(startTime, RetrySendTimer.interval, view)
    }


    class SendTimer(startTime: Long, interval: Long, val fragment: Fragment) :
        CountDownTimer(startTime, interval) {

        private var retryBtn: TextView? = null


        override fun onFinish() {
            seconds = 0
            retryBtn = fragment.view?.retry_send
            retryBtn?.text = fragment.getString(R.string.retrySms)
        }

        override fun onTick(millisUntilFinished: Long) {
            retryBtn = fragment.view?.retry_send
            seconds = millisUntilFinished / 1000

            when {
                seconds == 0L -> {
                    retryBtn?.text =
                        fragment.getString(R.string.retrySms)
                }
                seconds == 1L -> {
                    retryBtn?.text =
                        fragment.getString(R.string.retrySmsIn).plus(" $seconds секунду")
                }
                seconds!! < 5 -> {
                    retryBtn?.text =
                        fragment.getString(R.string.retrySmsIn).plus(" $seconds секунды")
                }
                else -> {
                    retryBtn?.text =
                        fragment.getString(R.string.retrySmsIn).plus(" $seconds секунд")
                }
            }
        }

    }

}