package bonch.dev.presentation.modules.passanger.signup.view

import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.text.style.URLSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import bonch.dev.App
import bonch.dev.MainActivity
import bonch.dev.R
import bonch.dev.di.component.passanger.DaggerSignupComponent
import bonch.dev.di.module.passanger.SignupModule
import bonch.dev.presentation.modules.passanger.signup.SignupComponent
import bonch.dev.presentation.modules.passanger.signup.presenter.ContractPresenter
import kotlinx.android.synthetic.main.phone_signup_fragment.*
import kotlinx.android.synthetic.main.phone_signup_fragment.view.*
import javax.inject.Inject

class PhoneView : Fragment(), ContractView.IPhoneView {

    @Inject
    lateinit var phonePresenter: ContractPresenter.IPhonePresenter

    init {
        //first build DI component
        if (SignupComponent.signupComponent == null) {
            SignupComponent.signupComponent = DaggerSignupComponent
                .builder()
                .signupModule(SignupModule())
                .appComponent(App.appComponent)
                .build()
        }

        SignupComponent.signupComponent?.inject(this)

        phonePresenter.instance().attachView(this)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.phone_signup_fragment, container, false)

        //active links
        val terms = root.terms
        terms.movementMethod = LinkMovementMethod.getInstance()
        removeUnderline(terms)

        return root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setListeners()

        (activity as MainActivity).window
            .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        super.onViewCreated(view, savedInstanceState)
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
            val phone = phone_number.text.toString().trim()

            phonePresenter.hideKeyboard(activity as MainActivity)

            Handler().postDelayed({
                //wait for keyboard hide
                phonePresenter.getCode(phone, view)
            },300)
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
            btn_done.setBackgroundResource(R.drawable.bg_btn_blue)
        } else {
            btn_done.setBackgroundResource(R.drawable.bg_btn_gray)
        }
    }


    override fun test(): FragmentManager? {
        return activity?.supportFragmentManager
    }


    override fun showError(text: String) {
        (activity as MainActivity).showNotification(text)
    }


    override fun onDestroy() {
        phonePresenter.instance().detachView()
        super.onDestroy()
    }
}


