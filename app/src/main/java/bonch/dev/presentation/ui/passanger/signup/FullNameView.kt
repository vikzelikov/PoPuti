package bonch.dev.presentation.ui.passanger.signup

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import bonch.dev.MainActivity
import bonch.dev.R
import bonch.dev.presentation.presenter.passanger.signup.RetrySendTimer
import bonch.dev.presentation.presenter.passanger.signup.SignupPresenter
import bonch.dev.utils.Constants.FULL_NAME_VIEW
import bonch.dev.utils.Constants.SIGNUP_INTERVAL_SMS
import bonch.dev.utils.Keyboard.hideKeyboard
import kotlinx.android.synthetic.main.full_name_signup_fragment.view.*

class FullNameView : Fragment() {

    private var signupPresenter: SignupPresenter? = null

    init {
        if (signupPresenter == null) {
            signupPresenter = SignupPresenter(this)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.full_name_signup_fragment, container, false)

        signupPresenter?.attachView(root)

        setListeners(root)

        //set listener to move button in relation to keyboard on/off
        signupPresenter?.setMovingButtonListener(FULL_NAME_VIEW)

        setHintListener(root)

        //reset so user get this view
        RetrySendTimer.startTime = SIGNUP_INTERVAL_SMS

        return root
    }


    private fun setHintListener(root: View) {
        val firstName = root.first_name
        val lastName = root.last_name

        firstName.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                firstName.hint = ""
            } else {
                firstName.hint = getString(R.string.your_name)
            }
        }

        lastName.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                lastName.hint = ""
            } else {
                lastName.hint = getString(R.string.your_family)
            }
        }
    }


    private fun setListeners(root: View) {
        val nextBtn = root.next_btn_name
        val backBtn = root.back_btn
        val firstName = root.first_name
        val lastName = root.last_name

        nextBtn.setOnClickListener {
            val activity = activity as MainActivity
            signupPresenter?.saveProfileData(firstName.text.toString(), lastName.text.toString())

            hideKeyboard(activity, root)

            if (signupPresenter != null && signupPresenter!!.isNameEntered()) {
                val fm = activity.supportFragmentManager
                signupPresenter?.nextBtn(FULL_NAME_VIEW, fm,null)
            }
        }

        backBtn.setOnClickListener {
            val activity = activity as MainActivity

            hideKeyboard(activity, root)

            val fm = activity.supportFragmentManager
            signupPresenter?.backBtn(fm)
        }

        firstName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                signupPresenter?.isNameEntered()
            }
        })

        lastName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                signupPresenter?.isNameEntered()
            }
        })
    }

}
