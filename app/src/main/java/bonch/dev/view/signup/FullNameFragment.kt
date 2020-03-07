package bonch.dev.view.signup

import android.graphics.Rect
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import bonch.dev.Constant.Companion.FULL_NAME_VIEW
import bonch.dev.MainActivity
import bonch.dev.MainActivity.Companion.hideKeyboard
import bonch.dev.R
import bonch.dev.presenter.signup.SignupPresenter

class FullNameFragment(var startHeight: Int = 0, var screenHeight: Int = 0) : Fragment() {

    private var signupPresenter: SignupPresenter? = null

    private lateinit var firstName: EditText
    private lateinit var lastName: EditText
    private lateinit var backBtn: ImageButton
    private lateinit var nextBtn: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.full_name_signup_fragment, container, false)

        initViews(root)

        setListeners(root)

        //set listener to move button in relation to keyboard on/off
        setMovingButtonListener(root)

        setHintListener()

        if (signupPresenter == null) {
            signupPresenter = SignupPresenter()
        }

        return root
    }


    private fun setHintListener() {
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


    private fun setMovingButtonListener(root: View) {
        var heightDiff: Int
        var btnDefaultPosition = 0.0f

        root.viewTreeObserver
            .addOnGlobalLayoutListener {
                val rect = Rect()

                root.getWindowVisibleDisplayFrame(rect)
                heightDiff = screenHeight - (rect.bottom - rect.top)

                if (screenHeight == 0) {
                    screenHeight = root.rootView.height
                }

                if (btnDefaultPosition == 0.0f) {
                    //init default position of button
                    btnDefaultPosition = nextBtn.y
                }

                if (startHeight == 0) {
                    startHeight = screenHeight - (rect.bottom - rect.top)
                }


                if (heightDiff > startHeight) {
                    //move UP
                    nextBtn.y = btnDefaultPosition - heightDiff + startHeight
                } else {
                    //move DOWN
                    nextBtn.y = btnDefaultPosition
                }
            }
    }


    private fun setListeners(root: View) {

        nextBtn.setOnClickListener {
            hideKeyboard(activity!!, root)

            if (signupPresenter != null && isValidName()) {
                val fm = (activity as MainActivity).supportFragmentManager
                signupPresenter!!.clickNextBtn(FULL_NAME_VIEW, fm)
            }
        }

        backBtn.setOnClickListener {
            hideKeyboard(activity!!, root)

            if (signupPresenter != null) {
                val fm = (activity as MainActivity).supportFragmentManager
                signupPresenter!!.clickBackBtn(fm)
            }
        }

        firstName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if(isValidName()){
                    nextBtn.setBackgroundResource(R.drawable.bg_btn_blue)
                }else{
                    nextBtn.setBackgroundResource(R.drawable.bg_btn_gray)
                }
            }
        })

        lastName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if(isValidName()){
                    nextBtn.setBackgroundResource(R.drawable.bg_btn_blue)
                }else{
                    nextBtn.setBackgroundResource(R.drawable.bg_btn_gray)
                }
            }
        })
    }


    private fun initViews(root: View) {
        firstName = root.findViewById(R.id.first_name)
        lastName = root.findViewById(R.id.last_name)
        backBtn = root.findViewById(R.id.back_btn)
        nextBtn = root.findViewById(R.id.next)
    }


    private fun isValidName(): Boolean {
        var result = false

        if (firstName.text.toString().trim().isNotEmpty() && lastName.text.toString().trim().isNotEmpty()) {
            result = true
        }

        return result
    }


}