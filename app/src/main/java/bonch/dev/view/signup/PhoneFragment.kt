package bonch.dev.view.signup

import android.graphics.Rect
import android.os.Bundle
import android.text.SpannableString
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
import bonch.dev.Constant
import bonch.dev.Constant.Companion.CONFIRM_PHONE_VIEW
import bonch.dev.Constant.Companion.PHONE_VIEW
import bonch.dev.Coordinator
import bonch.dev.MainActivity
import bonch.dev.MainActivity.Companion.hideKeyboard
import bonch.dev.R
import bonch.dev.presenter.signup.SignupPresenter
import bonch.dev.request.signup.Articles
import bonch.dev.request.signup.News
import bonch.dev.request.signup.RetrofitService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

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
            hideKeyboard(activity!!, root)

            //sendSms()
            if (signupPresenter != null) {
                val fm = (activity as MainActivity).supportFragmentManager
                signupPresenter!!.clickNextBtn(PHONE_VIEW, startHeight, screenHeight, fm)
            }
        }
    }


    private fun sendSms() {
        var response: Response<News>
        val service = makeRetrofitService()
        var list: List<Articles>

        CoroutineScope(Dispatchers.IO).launch {
            //send request and get data object
            response = service.getData("USA")
            try {
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        //get list data and init recycler
                        list = response.body()!!.articles
                        print(list)
                    }
                }
            } catch (err: HttpException) {
                Log.e("Retrofit", "${err.printStackTrace()}")
            }
        }
    }


    companion object {
        private val BASE_URL = "https://newsapi.org"

        private fun makeRetrofitService(): RetrofitService {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retrofit.create(RetrofitService::class.java)
        }
    }

}


