package bonch.dev.view.signup

import android.os.Bundle
import android.text.Editable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.text.style.URLSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import bonch.dev.R
import bonch.dev.presenter.signup.SignupPresenter
import bonch.dev.utils.Constants.PHONE_VIEW
import kotlinx.android.synthetic.main.phone_signup_fragment.view.*

class PhoneFragment : Fragment() {

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
        val root = inflater.inflate(R.layout.phone_signup_fragment, container, false)

        signupPresenter?.attachView(root)

        setListener(root)

        //set listener to move button in relation to keyboard on/off
        signupPresenter?.setMovingButtonListener(PHONE_VIEW)

        //active links
        val terms = root.terms
        terms.movementMethod = LinkMovementMethod.getInstance()
        removeUnderline(terms)


        return root
    }


    //remove underline of links in TextView terms
    private fun removeUnderline(textView: TextView) {
        val s = SpannableString(textView.text)
        val spans = s.getSpans(0, s.length, URLSpan::class.java)
        for (span in spans) {
            val start = s.getSpanStart(span)
            val end = s.getSpanEnd(span)
            val newSpan = URLNoUnderline(span.url)
            s.removeSpan(span)
            s.setSpan(newSpan, start, end, 0)
        }
        textView.text = s
    }


    private fun setListener(root: View) {
        val nextBtn = root.next_btn_phone
        val phoneEditText = root.phone_number

        nextBtn.setOnClickListener {
            val phone = phoneEditText.text.toString().trim()
            signupPresenter?.getCode(phone)
        }

        phoneEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val phone = s.toString().trim()
                signupPresenter?.isPhoneEntered(phone)
            }
        })
    }
}


