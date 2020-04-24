package bonch.dev.presentation.modules.passanger.signup.view

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.Fragment
import bonch.dev.MainActivity
import bonch.dev.R
import bonch.dev.domain.utils.Constants.SIGNUP_INTERVAL_SMS
import bonch.dev.domain.utils.Keyboard
import bonch.dev.presentation.modules.passanger.signup.SignupComponent
import bonch.dev.presentation.modules.passanger.signup.presenter.ContractPresenter
import bonch.dev.presentation.modules.passanger.signup.presenter.RetrySendTimer
import kotlinx.android.synthetic.main.full_name_signup_fragment.*
import javax.inject.Inject

class FullNameView : Fragment(), ContractView.IFullNameView {

    @Inject
    lateinit var fullNamePresenter: ContractPresenter.IFullNamePresenter


    init {
        SignupComponent.signupComponent?.inject(this)

        fullNamePresenter.instance().attachView(this)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.full_name_signup_fragment, container, false)

        //reset so user get this view
        RetrySendTimer.startTime = SIGNUP_INTERVAL_SMS

        return root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setListeners()

        setHintListener()

        Keyboard.setMovingButtonListener(view, true)

        super.onViewCreated(view, savedInstanceState)
    }


    override fun setHintListener() {
        first_name.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            first_name_layout.isHintEnabled = hasFocus
        }

        last_name.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            last_name_layout.isHintEnabled = hasFocus
        }
    }

    override fun getFirstName(): String {
        return first_name.text.toString().trim()
    }

    override fun getLastName(): String {
        return last_name.text.toString().trim()
    }


    override fun setListeners() {
        btn_done.setOnClickListener {
            val activity = activity as MainActivity
            fullNamePresenter.saveProfileData(first_name.text.toString(), last_name.text.toString())

            Keyboard.hideKeyboard(activity, view)

            if (fullNamePresenter.isNameEntered()) {
                //null DI component
                SignupComponent.signupComponent = null
                activity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

                //next TODO
            }
        }

        back_btn.setOnClickListener {
            fullNamePresenter.back(activity as MainActivity)
        }

        first_name.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                fullNamePresenter.isNameEntered()
            }
        })

        last_name.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                fullNamePresenter.isNameEntered()
            }
        })
    }


    override fun changeBtnEnable(enable: Boolean) {
        if (enable) {
            btn_done.setBackgroundResource(R.drawable.bg_btn_blue)
        } else {
            btn_done.setBackgroundResource(R.drawable.bg_btn_gray)
        }
    }


    override fun onDestroy() {
        fullNamePresenter.instance().detachView()
        super.onDestroy()
    }
}
