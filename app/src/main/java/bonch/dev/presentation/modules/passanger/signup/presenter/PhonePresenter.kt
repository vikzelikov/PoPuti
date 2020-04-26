package bonch.dev.presentation.modules.passanger.signup.presenter

import android.view.View
import androidx.fragment.app.FragmentActivity
import bonch.dev.R
import bonch.dev.domain.entities.passanger.signup.DataSignup
import bonch.dev.domain.interactor.passanger.signup.ISignupInteractor
import bonch.dev.domain.utils.Keyboard
import bonch.dev.presentation.base.BasePresenter
import bonch.dev.presentation.modules.passanger.signup.SignupComponent
import bonch.dev.presentation.modules.passanger.signup.view.ContractView
import bonch.dev.route.passanger.signup.ISignupRouter
import javax.inject.Inject

class PhonePresenter : BasePresenter<ContractView.IPhoneView>(), ContractPresenter.IPhonePresenter {

    @Inject
    lateinit var signupInteractor: ISignupInteractor

    @Inject
    lateinit var router: ISignupRouter

    init {
        SignupComponent.signupComponent?.inject(this)
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

                val nav = getView()?.getNavHost()
                router.showConfirmPhoneView(nav)
            } else {
                val error ="${res?.getString(R.string.waitFor)}" +
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