package bonch.dev.presenter.signup

import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import bonch.dev.MainActivity
import bonch.dev.R
import bonch.dev.model.signup.SignupModel
import bonch.dev.utils.Constants.CONFIRM_PHONE_VIEW
import bonch.dev.utils.Constants.FULL_NAME_VIEW
import bonch.dev.utils.Constants.MAIN_FRAGMENT
import bonch.dev.utils.Constants.PHONE_NUMBER
import bonch.dev.utils.Constants.PHONE_VIEW
import bonch.dev.utils.Constants.SIGNUP_INTERVAL_SMS
import bonch.dev.utils.Constants.SIGNUP_MAX_INTERVAL_SMS
import bonch.dev.utils.Coordinator.previousFragment
import bonch.dev.utils.Coordinator.replaceFragment
import bonch.dev.utils.Keyboard.hideKeyboard
import bonch.dev.view.signup.ConfirmPhoneFragment
import kotlinx.android.synthetic.main.confirm_phone_fragment.view.*
import kotlinx.android.synthetic.main.full_name_signup_fragment.view.*
import kotlinx.android.synthetic.main.phone_signup_fragment.view.*


class SignupPresenter(val fragment: Fragment) {

    private var signupModel: SignupModel? = null
    private var root: View? = null

    var startHeight: Int = 0
    var screenHeight: Int = 0


    init {
        if (signupModel == null) {
            signupModel = SignupModel(this)
        }
    }


    fun attachView(root: View) {
        this.root = root
    }


    fun nextBtn(id: Int, fm: FragmentManager) {
        when (id) {
            FULL_NAME_VIEW -> {
                replaceFragment(MAIN_FRAGMENT, null, fm)
            }
        }
    }


    fun nextBtn(
        id: Int,
        fm: FragmentManager,
        bundle: Bundle
    ) {
        when (id) {
            PHONE_VIEW -> {
                replaceFragment(CONFIRM_PHONE_VIEW, fm, startHeight, screenHeight, bundle)
            }

            CONFIRM_PHONE_VIEW -> {
                replaceFragment(FULL_NAME_VIEW, bundle, fm)
            }
        }
    }


    fun backBtn(fm: FragmentManager) {
        previousFragment(fm)
        //retrySendTimer?.cancel()
    }


    fun isCodeEnter(): Boolean {
        var result = false

        if (root!!.code4.text.toString().trim().isNotEmpty()) {
            root!!.code4.requestFocus()
        }

        if (root != null) {
            val nextBtn = root!!.next_btn_code
            val code1 = root!!.code1.text.toString().trim()
            val code2 = root!!.code2.text.toString().trim()
            val code3 = root!!.code3.text.toString().trim()
            val code4 = root!!.code4.text.toString().trim()

            if (code1.isNotEmpty() && code2.isNotEmpty() && code3.isNotEmpty() && code4.isNotEmpty()) {
                changeBtnEnable(true, nextBtn)
                result = true
            } else {
                changeBtnEnable(false, nextBtn)
            }
        }

        return result
    }


    fun isPhoneEntered(phone: String): Boolean {
        var result = false

        if (root != null) {
            val nextBtn = root!!.next_btn_phone

            if (phone.length > 15) {
                changeBtnEnable(true, nextBtn)
                result = true
            } else {
                changeBtnEnable(false, nextBtn)
            }
        }

        return result
    }


    fun isNameEntered(): Boolean {
        var result = false

        if (root != null) {
            val nextBtn = root!!.next_btn_name
            val firstName = root!!.first_name
            val lastName = root!!.last_name

            if (firstName.text.toString().trim().isNotEmpty() && lastName.text.toString().trim()
                    .isNotEmpty()
            ) {
                changeBtnEnable(true, nextBtn)
                result = true
            } else {
                changeBtnEnable(false, nextBtn)
            }
        }

        return result
    }


    fun getCode(phone: String) {
        if (phone.length > 15) {

            if (RetrySendTimer.seconds == null || RetrySendTimer.seconds == 0L) {
                //signupModel?.sendSms(phone)
                hideKeyboard(fragment.activity!!, root!!)

                val bundle = Bundle()
                val fm = (fragment.activity as MainActivity).supportFragmentManager
                bundle.putString(PHONE_NUMBER, phone)
                nextBtn(PHONE_VIEW, fm, bundle)
            } else {
                Toast.makeText(
                    root?.context,
                    "Ожидайте еще ${RetrySendTimer.seconds} сек.",
                    Toast.LENGTH_SHORT
                ).show()
            }


        }
    }


    fun checkCode(phone: String, code: String) {
        if (root != null && isCodeEnter()) {
            val activity = fragment.activity!!
            hideKeyboard(activity, root!!)
            signupModel!!.checkCode(phone, code)
        }
    }


    fun onResponseCheckCode(isCorrect: Boolean) {
        val errorCode = root!!.error_code

        if (signupModel != null) {
            if (isCorrect) {
                val activity = fragment.activity!!
                val fm = (activity as MainActivity).supportFragmentManager

                nextBtn(
                    CONFIRM_PHONE_VIEW,
                    fm,
                    Bundle()
                )
            } else {
                //change view from another thread (get Main thread)
                val mainHandler = Handler(Looper.getMainLooper())
                val myRunnable = Runnable {
                    kotlin.run {
                        errorCode.visibility = View.VISIBLE
                    }
                }

                mainHandler.post(myRunnable)
            }
        }
    }


    private fun changeBtnEnable(enable: Boolean, nextBtn: Button) {
        val errorCode = root?.error_code

        if (enable) {
            nextBtn.setBackgroundResource(R.drawable.bg_btn_blue)
        } else {
            nextBtn.setBackgroundResource(R.drawable.bg_btn_gray)
        }

        errorCode?.visibility = View.INVISIBLE
    }


    fun setMovingButtonListener(id: Int) {
        var nextBtn: Button? = null
        var retryBtn: TextView? = null
        var heightDiff: Int
        var btnDefaultPosition = 0.0f
        var retrySendDefaultPosition = 0.0f

        when (id) {
            PHONE_VIEW -> {
                nextBtn = root?.next_btn_phone
            }

            CONFIRM_PHONE_VIEW -> {
                nextBtn = root?.next_btn_code
                retryBtn = root?.retry_send
            }

            FULL_NAME_VIEW -> {
                nextBtn = root?.next_btn_name
            }
        }

        root?.viewTreeObserver?.addOnGlobalLayoutListener {
            val rect = Rect()

            root!!.getWindowVisibleDisplayFrame(rect)
            heightDiff = screenHeight - (rect.bottom - rect.top)

            if (screenHeight == 0) {
                screenHeight = root!!.rootView.height
            }

            if (btnDefaultPosition == 0.0f) {
                //init default position of button
                btnDefaultPosition = nextBtn!!.y
            }

            if (retryBtn != null && retrySendDefaultPosition == 0.0f) {
                retrySendDefaultPosition = retryBtn.y
            }

            if (startHeight == 0) {
                startHeight = screenHeight - (rect.bottom - rect.top)
            }

            if (heightDiff > startHeight) {
                //move UP
                nextBtn!!.y = btnDefaultPosition - heightDiff + startHeight

                if (retryBtn != null) {
                    retryBtn.y = retrySendDefaultPosition - heightDiff + startHeight
                }

            } else {
                //move DOWN
                nextBtn!!.y = btnDefaultPosition

                if (retryBtn != null) {
                    retryBtn.y = retrySendDefaultPosition
                }
            }
        }
    }


    fun startTimerRetrySend(fragment: ConfirmPhoneFragment) {
        if (RetrySendTimer.seconds == null) {
            RetrySendTimer.getInstance(fragment)?.start()

        } else if (RetrySendTimer.startTime < SIGNUP_MAX_INTERVAL_SMS) {

            if (RetrySendTimer.seconds == 0L) {
                RetrySendTimer.getInstance(fragment)?.cancel()
                RetrySendTimer.increaseStartTime(SIGNUP_INTERVAL_SMS, fragment)
                RetrySendTimer.getInstance(fragment)?.start()
            }

        } else {
            Toast.makeText(
                fragment.context,
                "Обратитесь в техническую поддержку",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

}