package bonch.dev.poputi.presentation.modules.passenger.signup.view

import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.InputType
import android.text.SpannableString
import android.text.TextWatcher
import android.text.method.DigitsKeyListener
import android.text.method.LinkMovementMethod
import android.text.style.URLSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import bonch.dev.poputi.App
import bonch.dev.poputi.MainActivity
import bonch.dev.poputi.R
import bonch.dev.poputi.di.component.passenger.DaggerPassengerSignupComponent
import bonch.dev.poputi.di.module.passenger.PassengerSignupModule
import bonch.dev.poputi.domain.utils.Keyboard
import bonch.dev.poputi.presentation.modules.passenger.signup.SignupComponent
import bonch.dev.poputi.presentation.modules.passenger.signup.presenter.ContractPresenter
import com.redmadrobot.inputmask.MaskedTextChangedListener.Companion.installOn
import com.redmadrobot.inputmask.MaskedTextChangedListener.ValueListener
import kotlinx.android.synthetic.main.phone_signup_fragment.*
import kotlinx.android.synthetic.main.phone_signup_fragment.view.*
import javax.inject.Inject


class PhoneView : Fragment(), ContractView.IPhoneView {

    @Inject
    lateinit var phonePresenter: ContractPresenter.IPhonePresenter

    init {
        initDI()

        SignupComponent.passengerSignupComponent?.inject(this)

        phonePresenter.instance().attachView(this)
    }


    //first build DI component
    private fun initDI() {
        if (SignupComponent.passengerSignupComponent == null) {
            SignupComponent.passengerSignupComponent = DaggerPassengerSignupComponent
                .builder()
                .passengerSignupModule(PassengerSignupModule())
                .appComponent(App.appComponent)
                .build()
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.phone_signup_fragment, container, false)

        //active links
        val terms = root?.terms
        terms?.movementMethod = LinkMovementMethod.getInstance()
        terms?.let { removeUnderline(terms) }

        return root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setListeners()

        (activity as? MainActivity)?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        setPhoneMask()

        super.onViewCreated(view, null)
    }


    //remove underline of links in TextView terms
    private fun removeUnderline(textView: TextView) {
        val s = SpannableString(textView.text)
        val spans = s.getSpans(0, s.length, URLSpan::class.java)
        for (span in spans) {
            val start = s.getSpanStart(span)
            val end = s.getSpanEnd(span)
            val newSpan =
                URLNoUnderline(
                    span.url
                )
            s.removeSpan(span)
            s.setSpan(newSpan, start, end, 0)
        }
        textView.text = s
    }


    override fun setListeners() {
        btn_done.setOnClickListener {
            val phone = phone_number?.text?.toString()?.trim()

            hideKeyboard()

            Handler().postDelayed({
                //wait for keyboard hide
                phone?.let { phonePresenter.getCode(phone) }
            }, 300)
        }

        phone_number.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val phone = s.toString().trim()
                phonePresenter.isPhoneEntered(phone)
            }
        })
    }


    override fun changeBtnEnable(enable: Boolean) {
        if (enable) {
            btn_done?.setBackgroundResource(R.drawable.bg_btn_blue)
        } else {
            btn_done?.setBackgroundResource(R.drawable.bg_btn_gray)
        }
    }


    override fun setPhoneMask() {
        phone_number?.inputType = InputType.TYPE_CLASS_NUMBER
        phone_number?.keyListener = DigitsKeyListener.getInstance("1234567890+-() ")

        val listener =
            installOn(
                phone_number,
                "+7 ([000]) [000]-[00]-[00]",
                object : ValueListener {
                    override fun onTextChanged(
                        maskFilled: Boolean,
                        extractedValue: String,
                        formattedValue: String
                    ) {

                    }
                }
            )


        phone_number?.hint = listener.placeholder()
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


    override fun showLoading() {
        (activity as? MainActivity)?.showLoading()
    }


    override fun hideLoading() {
        (activity as? MainActivity)?.hideLoading()
    }


    override fun getNavHost(): NavController? {
        return (activity as? MainActivity)?.navController
    }


    override fun onDestroy() {
        phonePresenter.instance().detachView()
        super.onDestroy()
    }
}


