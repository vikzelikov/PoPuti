package bonch.dev.view.signup

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import bonch.dev.Constant.Companion.MAIN_FRAGMENT
import bonch.dev.Coordinator
import bonch.dev.MainActivity
import bonch.dev.MainActivity.Companion.hideKeyboard
import bonch.dev.R

class FullNameFragment(var startHeight: Int = 0, var screenHeight: Int = 0) : Fragment() {

    private var coordinator: Coordinator? = null

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

            coordinator!!.replaceFragment(MAIN_FRAGMENT, Bundle())
        }

        backBtn.setOnClickListener {
            hideKeyboard(activity!!, root)

            (activity as MainActivity).supportFragmentManager.popBackStack()
        }
    }


    private fun initViews(root: View) {
        firstName = root.findViewById(R.id.first_name)
        lastName = root.findViewById(R.id.last_name)
        backBtn = root.findViewById(R.id.back_btn)
        nextBtn = root.findViewById(R.id.next)
    }


    init {
        if (coordinator == null) {
            coordinator = (activity as MainActivity).coordinator
        }
    }
}