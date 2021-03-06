package bonch.dev.poputi.presentation.modules.passenger.signup.view

import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import bonch.dev.poputi.MainActivity
import bonch.dev.poputi.R
import bonch.dev.poputi.domain.entities.passenger.signup.DataSignup
import bonch.dev.poputi.domain.utils.Keyboard
import bonch.dev.poputi.presentation.modules.passenger.signup.SignupComponent
import bonch.dev.poputi.presentation.modules.passenger.signup.presenter.ContractPresenter
import bonch.dev.poputi.presentation.modules.passenger.signup.presenter.RetrySendTimer
import kotlinx.android.synthetic.main.confirm_phone_fragment.*
import javax.inject.Inject


class ConfirmPhoneView : Fragment(), ContractView.IConfirmView {

    @Inject
    lateinit var confirmPhonePresenter: ContractPresenter.IConfirmPhonePresenter


    init {
        SignupComponent.passengerSignupComponent?.inject(this)

        confirmPhonePresenter.instance().attachView(this)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.confirm_phone_fragment, container, false)

        confirmPhonePresenter.startTimerRetrySend(activity as MainActivity)

        return root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setListeners()

        Keyboard.showKeyboard(activity as MainActivity)
        Handler().postDelayed({
            code_edit_text?.requestFocus()
        }, 300)

        super.onViewCreated(view, null)
    }


    override fun setListeners() {
        val activity = activity as? MainActivity

        show_phone?.text = getString(R.string.showPhone).plus(" ").plus(DataSignup.phone)

        retry_send.setOnClickListener {
            activity?.let {
                confirmPhonePresenter.startTimerRetrySend(it)
            }
        }

        btn_done.setOnClickListener {
            hideKeyboard()
            confirmPhonePresenter.checkCode(DataSignup.phone, getCode())
        }

        back_btn.setOnClickListener {
            activity?.let {
                confirmPhonePresenter.back(it)
            }
        }


        code_edit_text.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                confirmPhonePresenter.isCodeEnter()
            }
        })
    }


    override fun getCode(): String? {
        return code_edit_text?.text?.toString()?.trim()
    }


    override fun showError() {
        error_code?.visibility = View.VISIBLE
    }


    override fun hideError() {
        error_code?.visibility = View.INVISIBLE
    }


    override fun changeBtnEnable(enable: Boolean) {
        if (enable) {
            btn_done?.setBackgroundResource(R.drawable.bg_btn_blue)
        } else {
            btn_done?.setBackgroundResource(R.drawable.bg_btn_gray)
        }

        hideError()
    }


    override fun setViewTimer() {
        when {
            RetrySendTimer.seconds == 0L -> {
                retry_send?.text = getString(R.string.retrySms)
            }
            RetrySendTimer.seconds == 1L -> {
                retry_send?.text =
                    getString(R.string.retrySmsIn)
                        .plus(" ${RetrySendTimer.seconds}")
                        .plus(" ${getString(R.string.second)}")
            }
            RetrySendTimer.seconds!! < 5 -> {
                retry_send?.text =
                    getString(R.string.retrySmsIn)
                        .plus(
                            " ${RetrySendTimer.seconds}"
                                .plus(" ${getString(R.string.seconds1)}")
                        )
            }
            else -> {
                retry_send?.text =
                    getString(R.string.retrySmsIn)
                        .plus(" ${RetrySendTimer.seconds}")
                        .plus(" ${getString(R.string.seconds)}")
            }
        }
    }


    override fun hideKeyboard() {
        val activity = activity as? MainActivity
        activity?.let {
            Keyboard.hideKeyboard(activity, view)
        }
    }


    override fun showNotification(text: String) {
        (activity as? MainActivity)?.showNotification(text)
    }


    override fun getNavHost(): NavController? {
        return (activity as? MainActivity)?.navController
    }


    override fun showLoading() {
        (activity as? MainActivity)?.showLoading()
    }


    override fun hideLoading() {
        (activity as? MainActivity)?.hideLoading()
    }


    override fun finishTimer() {
        retry_send?.text = getString(R.string.retrySms)
    }


    override fun setSoftInput() {
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
    }


    override fun hideRetryCode() {
        retry_send?.visibility = View.GONE
    }


    override fun onDestroy() {
        confirmPhonePresenter.instance().detachView()
        super.onDestroy()
    }
}
