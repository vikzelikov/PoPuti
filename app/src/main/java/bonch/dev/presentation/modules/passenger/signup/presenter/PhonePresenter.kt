package bonch.dev.presentation.modules.passenger.signup.presenter

import android.view.View
import androidx.fragment.app.FragmentActivity
import bonch.dev.R
import bonch.dev.domain.entities.passenger.signup.DataSignup
import bonch.dev.domain.interactor.passenger.signup.ISignupInteractor
import bonch.dev.domain.utils.Keyboard
import bonch.dev.presentation.base.BasePresenter
import bonch.dev.presentation.modules.passenger.signup.SignupComponent
import bonch.dev.presentation.modules.passenger.signup.view.ContractView
import bonch.dev.route.MainRouter
import javax.inject.Inject

class PhonePresenter : BasePresenter<ContractView.IPhoneView>(), ContractPresenter.IPhonePresenter {

    @Inject
    lateinit var signupInteractor: ISignupInteractor

    init {
        SignupComponent.passengerSignupComponent?.inject(this)
    }


    override fun getCode(phone: String, root: View?) {
        if (phone.length > 17) {

            val res = root?.resources

            if (RetrySendTimer.seconds == null || RetrySendTimer.seconds == 0L) {
//                signupInteractor.sendSms(phone,
//                    callback = {
//                        val mainHandler = Handler(Looper.getMainLooper())
//                        val myRunnable = Runnable {
//                            kotlin.run {
//                                val error = res?.getString(R.string.errorSystem).plus("")
//                                getView()?.showError(error)
//                            }
//                        }
//                        mainHandler.post(myRunnable)
//                    })

                MainRouter.showView(R.id.show_confirm_phone_view, getView()?.getNavHost(), null)
            } else {
                val error = "${res?.getString(R.string.waitFor)}" +
                        " ${RetrySendTimer.seconds}" +
                        " ${res?.getString(R.string.sec)}"
                getView()?.showError(error)
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