package bonch.dev.presentation.modules.passanger.signup.view

import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import bonch.dev.MainActivity
import bonch.dev.R
import bonch.dev.domain.entities.passanger.signup.Phone
import bonch.dev.domain.utils.Keyboard
import bonch.dev.presentation.modules.passanger.signup.SignupComponent
import bonch.dev.presentation.modules.passanger.signup.presenter.ContractPresenter
import bonch.dev.presentation.modules.passanger.signup.presenter.RetrySendTimer
import kotlinx.android.synthetic.main.confirm_phone_fragment.*
import javax.inject.Inject


class ConfirmPhoneView : Fragment(), View.OnKeyListener, ContractView.IConfirmView {

    @Inject
    lateinit var confirmPhonePresenter: ContractPresenter.IConfirmPhonePresenter

    init {
        SignupComponent.signupComponent?.inject(this)

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
        code1.requestFocus()

        //set listeners to input code
        code1.setOnKeyListener(this)
        code2.setOnKeyListener(this)
        code3.setOnKeyListener(this)
        code4.setOnKeyListener(this)

        super.onViewCreated(view, savedInstanceState)
    }


    override fun onKey(v: View, keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode != KeyEvent.KEYCODE_BACK && keyCode != KeyEvent.KEYCODE_DEL && v.id == R.id.code1 && event.action == KeyEvent.ACTION_DOWN) {
            code2.requestFocus()
        }

        if (v.id == R.id.code2 && event.action == KeyEvent.ACTION_DOWN) {
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                code1.requestFocus()
            } else {
                code3.requestFocus()
            }
        }

        if (v.id == R.id.code3 && event.action == KeyEvent.ACTION_DOWN) {
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                code2.requestFocus()
            } else {
                code4.requestFocus()
            }
        }

        if (keyCode == KeyEvent.KEYCODE_DEL && v.id == R.id.code4 && event.action == KeyEvent.ACTION_DOWN) {
            code3.requestFocus()
        }

        return false
    }


    override fun setListeners() {
        show_phone.text = getString(R.string.showPhone).plus(" ").plus(Phone.phone)

        retry_send.setOnClickListener {
            confirmPhonePresenter.startTimerRetrySend(activity as MainActivity)
        }

        btn_done.setOnClickListener {
            Keyboard.hideKeyboard(activity as MainActivity, view)
            confirmPhonePresenter.checkCode(Phone.phone, getCode())
        }

        back_btn.setOnClickListener {
            val activity = activity as MainActivity
            confirmPhonePresenter.back(activity)
        }

        code4.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                confirmPhonePresenter.isCodeEnter()

            }
        })
    }


    override fun getCode(): String {
        val code1 = code1.text.toString().trim()
        val code2 = code2.text.toString().trim()
        val code3 = code3.text.toString().trim()
        val code4 = code4.text.toString().trim()
        return code1 + code2 + code3 + code4
    }


    override fun requestFocus() {
        if (code4.text.toString().trim().isNotEmpty()) {
            code4.requestFocus()
        }
    }


    override fun showError() {
        error_code.visibility = View.VISIBLE
    }


    override fun hideError() {
        error_code.visibility = View.INVISIBLE
    }


    override fun changeBtnEnable(enable: Boolean) {
        if (enable) {
            btn_done.setBackgroundResource(R.drawable.bg_btn_blue)
        } else {
            btn_done.setBackgroundResource(R.drawable.bg_btn_gray)
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


    override fun finishTimer() {
        retry_send?.text = getString(R.string.retrySms)
    }


    override fun onDestroy() {
        confirmPhonePresenter.instance().detachView()
        super.onDestroy()
    }
}
