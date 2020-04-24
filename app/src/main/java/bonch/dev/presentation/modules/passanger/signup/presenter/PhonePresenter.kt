package bonch.dev.presentation.modules.passanger.signup.presenter

import android.app.Activity
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import bonch.dev.App
import bonch.dev.R
import bonch.dev.domain.entities.passanger.signup.Phone
import bonch.dev.domain.interactor.passanger.signup.ISignupInteractor
import bonch.dev.domain.utils.Constants
import bonch.dev.domain.utils.Keyboard
import bonch.dev.presentation.base.BasePresenter
import bonch.dev.presentation.modules.passanger.signup.SignupComponent
import bonch.dev.presentation.modules.passanger.signup.view.ContractView
import bonch.dev.route.Coordinator
import javax.inject.Inject

class PhonePresenter : BasePresenter<ContractView.IPhoneView>(), ContractPresenter.IPhonePresenter {

    @Inject
    lateinit var signupInteractor: ISignupInteractor


    init {
        SignupComponent.signupComponent?.inject(this)
    }


    override fun getCode(phone: String, root: View?) {
        if (phone.length > 15) {
            if (RetrySendTimer.seconds == null || RetrySendTimer.seconds == 0L) {
//                signupInteractor.sendSms(phone,
//                    callback = {
//                        val mainHandler = Handler(Looper.getMainLooper())
//                        val myRunnable = Runnable {
//                            kotlin.run {
//                                getView()?.showError(root?.resources.getString(R.string.errorSystem))
//                            }
//                        }
//                        mainHandler.post(myRunnable)
//                    })


                //TODO
                Coordinator.replaceFragment(Constants.CONFIRM_PHONE_VIEW, null, getView()?.test()!!)
            } else {
                val res = root?.resources
                val error ="${res?.getString(R.string.waitFor)}" +
                        " ${RetrySendTimer.seconds}" +
                        " ${res?.getString(R.string.sec)}"
                getView()?.showError(error)
            }

        }
    }


    override fun isPhoneEntered(phone: String): Boolean {
        var result = false

        if (phone.length > 15) {
            Phone.phone = phone
            getView()?.changeBtnEnable(true)
            result = true
        } else {
            Phone.phone = null
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