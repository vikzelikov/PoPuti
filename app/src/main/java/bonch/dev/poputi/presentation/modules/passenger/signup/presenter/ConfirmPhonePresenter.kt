package bonch.dev.poputi.presentation.modules.passenger.signup.presenter

import android.os.Handler
import android.os.Looper
import androidx.fragment.app.FragmentActivity
import bonch.dev.poputi.App
import bonch.dev.poputi.MainActivity
import bonch.dev.poputi.R
import bonch.dev.poputi.domain.entities.common.banking.BankCard
import bonch.dev.poputi.domain.entities.passenger.signup.DataSignup
import bonch.dev.poputi.domain.interactor.passenger.signup.ISignupInteractor
import bonch.dev.poputi.domain.utils.Keyboard
import bonch.dev.poputi.presentation.base.BasePresenter
import bonch.dev.poputi.presentation.modules.passenger.signup.SignupComponent
import bonch.dev.poputi.presentation.modules.passenger.signup.view.ContractView
import bonch.dev.poputi.route.MainRouter
import javax.inject.Inject

class ConfirmPhonePresenter : BasePresenter<ContractView.IConfirmView>(),
    ContractPresenter.IConfirmPhonePresenter {

    @Inject
    lateinit var signupInteractor: ISignupInteractor


    init {
        SignupComponent.passengerSignupComponent?.inject(this)
    }


    private fun sendCode(phone: String) {
//        signupInteractor.sendSms(phone) { isSuccess ->
//            if (!isSuccess) {
//                val mainHandler = Handler(Looper.getMainLooper())
//                val myRunnable = Runnable {
//                    kotlin.run {
//                        val res = App.appComponent.getContext().resources
//                        getView()?.showNotification(res.getString(R.string.errorSystem))
//                    }
//                }
//                mainHandler.post(myRunnable)
//            }
//        }
    }


    override fun checkCode(phone: String?, code: String?) {
        if (isCodeEnter() && phone != null && code != null) {

            getView()?.showLoading()

            signupInteractor.checkCode(phone, code, callback = {
                //change view from another thread (get Main thread)
                val mainHandler = Handler(Looper.getMainLooper())
                val myRunnable = Runnable {
                    kotlin.run {
                        onResponseCheckCode(it)
                    }
                }

                mainHandler.post(myRunnable)
            })
        }
    }


    override fun onResponseCheckCode(isCorrect: Boolean) {
        if (isCorrect) {
            signupInteractor.checkProfile { isCreated ->
                if (isCreated) {
                    //user already created before
                    doneSignup()

                } else {
                    //this is new sign up
                    MainRouter.showView(R.id.show_full_name_view, getView()?.getNavHost(), null)
                }

                getView()?.hideLoading()
            }
        } else {
            getView()?.showError()

            getView()?.hideLoading()
        }
    }


    private fun doneSignup() {
        signupInteractor.initRealm()

        //todo
        val googlePay = BankCard(
            0,
            "google",
            null,
            null
        )
        signupInteractor.saveGooglePay(googlePay)

        getView()?.setSoftInput()

        //if there is data from old app version
        signupInteractor.resetProfile()

        //save Data:
        //save token
        saveToken()

        signupInteractor.saveUserId()

        //update FB token
        DataSignup.firebaseToken?.let {
            signupInteractor.updateFirebaseToken(it)
        }

        //clear data
        SignupComponent.passengerSignupComponent = null
        DataSignup.phone = null
        DataSignup.token = null
        DataSignup.userId = null

        //next transition
        MainRouter.showView(
            R.id.main_passenger_fragment,
            getView()?.getNavHost(),
            null
        )
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

            DataSignup.phone?.let { sendCode(it) }

            RetrySendTimer.getInstance(this)?.start()

        } else if (RetrySendTimer.startTime < RetrySendTimer.maxInterval) {

            if (RetrySendTimer.seconds == 0L) {

                RetrySendTimer.getInstance(this)?.cancel()
                DataSignup.phone?.let { sendCode(it) }

                if (RetrySendTimer.increaseStartTime(this)) {
                    RetrySendTimer.getInstance(this)?.start()

                } else {
                    RetrySendTimer.startTime += RetrySendTimer.maxInterval
                    getView()?.hideRetryCode()
                }
            }

        } else {
            activity.showNotification(
                activity.resources.getString(
                    R.string.callSupport
                )
            )
        }
    }


    private fun saveToken() {
        val token = DataSignup.token

        if (token != null) {
            signupInteractor.saveToken(token)
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