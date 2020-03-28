package bonch.dev.view.signup

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import bonch.dev.MainActivity
import bonch.dev.R
import bonch.dev.presenter.signup.SignupPresenter
import bonch.dev.utils.Constants.CONFIRM_PHONE_VIEW
import bonch.dev.utils.Constants.PHONE_NUMBER
import bonch.dev.utils.Keyboard.hideKeyboard
import bonch.dev.utils.Keyboard.showKeyboard
import kotlinx.android.synthetic.main.confirm_phone_fragment.*
import kotlinx.android.synthetic.main.confirm_phone_fragment.view.*


class ConfirmPhoneFragment(startHeight: Int = 0, screenHeight: Int = 0) : Fragment(),
    View.OnKeyListener {

    private var signupPresenter: SignupPresenter? = null

    init {
        if (signupPresenter == null) {
            signupPresenter = SignupPresenter(this)

            signupPresenter?.startHeight = startHeight
            signupPresenter?.screenHeight = screenHeight
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.confirm_phone_fragment, container, false)

        signupPresenter?.attachView(root)

        setListeners(root)

        //set listener to move button in relation to keyboard on/off
        signupPresenter?.setMovingButtonListener(CONFIRM_PHONE_VIEW)

        //set listeners to input code
        root.code1.setOnKeyListener(this)
        root.code2.setOnKeyListener(this)
        root.code3.setOnKeyListener(this)
        root.code4.setOnKeyListener(this)


        //display keyboard and set focus
        root.code1.requestFocus()
        showKeyboard(activity!!)

        signupPresenter?.startTimerRetrySend(this)

        return root
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


    private fun setListeners(root: View) {
        val nextBtn = root.next_btn_code
        val retryBtn = root.retry_send
        val backBtn = root.back_btn

        val phone = arguments?.getString(PHONE_NUMBER)
        root.show_phone.text = getString(R.string.showPhone).plus(" ").plus(phone)

        retryBtn.setOnClickListener {
            signupPresenter?.startTimerRetrySend(this)
        }

        nextBtn.setOnClickListener {
            val code1 = root.code1.text.toString().trim()
            val code2 = root.code2.text.toString().trim()
            val code3 = root.code3.text.toString().trim()
            val code4 = root.code4.text.toString().trim()
            val code = code1 + code2 + code3 + code4

            signupPresenter?.checkCode(phone!!, code)
        }

        backBtn.setOnClickListener {
            hideKeyboard(activity!!, root)

            val fm = (activity as MainActivity).supportFragmentManager
            signupPresenter?.backBtn(fm)
        }

        root.code4.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                signupPresenter?.isCodeEnter()

            }
        })
    }
}