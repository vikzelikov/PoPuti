package bonch.dev.presentation.modules.passanger.signup.view

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import bonch.dev.MainActivity
import bonch.dev.R
import bonch.dev.data.repository.passanger.profile.pojo.Profile
import bonch.dev.domain.entities.passanger.signup.DataSignup
import bonch.dev.domain.utils.Keyboard
import bonch.dev.presentation.modules.passanger.signup.SignupComponent
import bonch.dev.presentation.modules.passanger.signup.presenter.ContractPresenter
import bonch.dev.presentation.modules.passanger.signup.presenter.RetrySendTimer
import kotlinx.android.synthetic.main.full_name_signup_fragment.*
import javax.inject.Inject

class FullNameView : Fragment(), ContractView.IFullNameView {

    @Inject
    lateinit var fullNamePresenter: ContractPresenter.IFullNamePresenter
    private val startTime = 15000L


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
        RetrySendTimer.startTime = startTime

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
            Keyboard.hideKeyboard(activity, view)

            if (fullNamePresenter.isNameEntered()) {
                //end signup as passanger
                activity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
                fullNamePresenter.doneSignup()
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


    override fun getProfileData(): Profile {
        val profileData = Profile()
        profileData.apply {
            firstName = first_name.text.toString().trim()
            lastName = last_name.text.toString().trim()
        }

        return profileData
    }


    override fun changeBtnEnable(enable: Boolean) {
        if (enable) {
            btn_done.setBackgroundResource(R.drawable.bg_btn_blue)
        } else {
            btn_done.setBackgroundResource(R.drawable.bg_btn_gray)
        }
    }


    override fun getNavHost(): NavController {
        return (activity as MainActivity).navController
    }


    override fun onDestroy() {
        fullNamePresenter.instance().detachView()
        super.onDestroy()
    }
}
