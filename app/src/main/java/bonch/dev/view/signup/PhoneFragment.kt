package bonch.dev.view.signup

import android.graphics.Rect
import android.os.Bundle
import android.text.Editable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.text.style.URLSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import bonch.dev.MainActivity
import bonch.dev.MainActivity.Companion.hideKeyboard
import bonch.dev.R
import bonch.dev.presenter.signup.SignupPresenter
import bonch.dev.network.signup.RetrofitService
import bonch.dev.utils.Constants.PHONE_VIEW
import bonch.dev.utils.NetworkUtil.makeRetrofitService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Response

class PhoneFragment : Fragment() {

    private var signupPresenter: SignupPresenter? = null

    private lateinit var nextBtn: Button
    private lateinit var termsTextView: TextView
    private lateinit var phoneEditText: EditText
    private var startHeight = 0
    private var screenHeight = 0


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.phone_signup_fragment, container, false)

        initViews(root)

        setListener(root)

        //set listener to move button in relation to keyboard on/off
        setMovingButtonListener(root)

        //active links
        termsTextView.movementMethod = LinkMovementMethod.getInstance()
        removeUnderline(termsTextView)

        if (signupPresenter == null) {
            signupPresenter = SignupPresenter()
        }

        return root
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


    private fun initViews(root: View) {
        nextBtn = root.findViewById(R.id.next)
        termsTextView = root.findViewById(R.id.terms)
        phoneEditText = root.findViewById(R.id.phone_number)
    }


    private fun setListener(root: View) {

        nextBtn.setOnClickListener {
            val phone = phoneEditText.text.toString().trim()

            if (signupPresenter != null && phone.length > 15) {
                sendSms(phone)

                hideKeyboard(activity!!, root)

                val fm = (activity as MainActivity).supportFragmentManager
                signupPresenter!!.clickNextBtn(PHONE_VIEW, startHeight, screenHeight, fm)
            }
        }

        phoneEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val phone = s.toString().trim()
                if(phone.length > 15){
                    nextBtn.setBackgroundResource(R.drawable.bg_btn_blue)
                }else{
                    nextBtn.setBackgroundResource(R.drawable.bg_btn_gray)
                }
            }
        })
    }


    private fun sendSms(phone: String) {
        var service: RetrofitService? = null
        var response: Response<*>

        if(service == null){
            service = makeRetrofitService()
        }

        CoroutineScope(Dispatchers.IO).launch {
            response = service.sendPhone(phone)
            println(response)
            try {
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        println(response.message())
                    } else {
                        println(response.code())
                    }
                }
            } catch (err: HttpException) {
                Log.e("Retrofit", "${err.printStackTrace()}")
            }
        }
    }


}


