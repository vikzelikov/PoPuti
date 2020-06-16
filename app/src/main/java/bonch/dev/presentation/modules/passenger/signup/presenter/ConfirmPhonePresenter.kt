package bonch.dev.presentation.modules.passenger.signup.presenter

import android.os.Handler
import android.os.Looper
import androidx.fragment.app.FragmentActivity
import bonch.dev.MainActivity
import bonch.dev.R
import bonch.dev.domain.interactor.passenger.signup.ISignupInteractor
import bonch.dev.domain.utils.Keyboard
import bonch.dev.presentation.base.BasePresenter
import bonch.dev.presentation.modules.passenger.signup.SignupComponent
import bonch.dev.presentation.modules.passenger.signup.view.ContractView
import bonch.dev.route.MainRouter
import javax.inject.Inject

class ConfirmPhonePresenter : BasePresenter<ContractView.IConfirmView>(),
    ContractPresenter.IConfirmPhonePresenter {

    @Inject
    lateinit var signupInteractor: ISignupInteractor


    private val maxInterval = 60000L
    private val interval = 15000L


    init {
        SignupComponent.passengerSignupComponent?.inject(this)
    }


    override fun checkCode(phone: String?, code: String) {
        if (isCodeEnter() && phone != null) {

            getView()?.showLoading()

            signupInteractor.checkCode(phone, code, callback = {
                onResponseCheckCode(it)
            })
        }
    }


    override fun onResponseCheckCode(isCorrect: Boolean) {
        if (isCorrect) {
            MainRouter.showView(R.id.show_full_name_view, getView()?.getNavHost(), null)
        } else {
            //change view from another thread (get Main thread)
            val mainHandler = Handler(Looper.getMainLooper())
            val myRunnable = Runnable {
                kotlin.run {
                    getView()?.showError()
                }
            }

            mainHandler.post(myRunnable)
        }

        getView()?.hideLoading()
    }


    override fun isCodeEnter(): Boolean {
        var result = false

        val code = getView()?.getCode()

        if (code?.length == 4) {
            getView()?.changeBtnEnable(true)
            result = true
        } else {
            getView()?.changeBtnEnable(false)
        }

        return result
    }


    override fun startTimerRetrySend(activity: MainActivity) {
        if (RetrySendTimer.seconds == null) {
            RetrySendTimer.getInstance(this)?.start()

        } else if (RetrySendTimer.startTime < maxInterval) {

            if (RetrySendTimer.seconds == 0L) {
                RetrySendTimer.getInstance(this)?.cancel()
                RetrySendTimer.increaseStartTime(interval, this)
                RetrySendTimer.getInstance(this)?.start()
            }

        } else {
            activity.showNotification(
                activity.resources.getString(
                    R.string.callSupport
                )
            )
        }
    }


    override fun setViewTimer() {
        getView()?.setViewTimer()
    }


    override fun timerFinish() {
        getView()?.finishTimer()
    }


    override fun instance(): ConfirmPhonePresenter {
        return this
    }


    override fun back(activity: FragmentActivity) {
        Keyboard.hideKeyboard(activity, activity.currentFocus)
        getView()?.getNavHost()?.popBackStack()
    }

}