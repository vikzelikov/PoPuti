package bonch.dev.poputi.presentation.modules.passenger.signup.presenter

import android.os.Handler
import android.os.Looper
import androidx.fragment.app.FragmentActivity
import bonch.dev.poputi.App
import bonch.dev.poputi.R
import bonch.dev.poputi.domain.entities.passenger.signup.DataSignup
import bonch.dev.poputi.domain.interactor.passenger.signup.ISignupInteractor
import bonch.dev.poputi.domain.utils.Keyboard
import bonch.dev.poputi.presentation.base.BasePresenter
import bonch.dev.poputi.presentation.modules.passenger.signup.SignupComponent
import bonch.dev.poputi.presentation.modules.passenger.signup.view.ContractView
import bonch.dev.poputi.route.MainRouter
import javax.inject.Inject

class PhonePresenter : BasePresenter<ContractView.IPhoneView>(), ContractPresenter.IPhonePresenter {

    @Inject
    lateinit var signupInteractor: ISignupInteractor

    init {
        SignupComponent.passengerSignupComponent?.inject(this)
    }


    override fun getCode(phone: String) {
        if (phone.length > 17) {
            val res = App.appComponent.getContext().resources

            if (RetrySendTimer.seconds == null || RetrySendTimer.seconds == 0L) {
                signupInteractor.sendSms(phone) { isSuccess ->
                    if (!isSuccess) {
                        val mainHandler = Handler(Looper.getMainLooper())
                        val myRunnable = Runnable {
                            kotlin.run {
                                getView()?.showNotification(res.getString(R.string.errorSystem))
                            }
                        }
                        mainHandler.post(myRunnable)
                    }
                }

                MainRouter.showView(R.id.show_confirm_phone_view, getView()?.getNavHost(), null)
            } else {
                val error = "${res?.getString(R.string.waitFor)}" +
                        " ${RetrySendTimer.seconds}" +
                        " ${res?.getString(R.string.sec)}"
                getView()?.showNotification(error)
            }

        }
    }


    override fun isPhoneEntered(phone: String): Boolean {
        var result = false

        if (phone.length > 17) {
            DataSignup.phone = phone
            getView()?.changeBtnEnable(true)
            result = true
        } else {
            DataSignup.phone = null
            getView()?.changeBtnEnable(false)
        }

        return result
    }


    override fun hideKeyboard(activity: FragmentActivity) {
        Keyboard.hideKeyboard(activity, activity.currentFocus)
    }


    override fun instance(): PhonePresenter {
        return this
    }

}