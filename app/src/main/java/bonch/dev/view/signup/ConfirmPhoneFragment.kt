package bonch.dev.view.signup

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import bonch.dev.Constant.Companion.FULL_NAME_VIEW
import bonch.dev.Coordinator
import bonch.dev.MainActivity
import bonch.dev.MainActivity.Companion.hideKeyboard
import bonch.dev.MainActivity.Companion.showKeyboard
import bonch.dev.R
import bonch.dev.view.EditTextCode


class ConfirmPhoneFragment(var startHeight: Int = 0, var screenHeight: Int = 0) : Fragment(),
    View.OnKeyListener {

    private var coordinator: Coordinator? = null

    private lateinit var code1EditText: EditTextCode
    private lateinit var code2EditText: EditTextCode
    private lateinit var code3EditText: EditTextCode
    private lateinit var code4EditText: EditTextCode
    private lateinit var retrySend: TextView
    private lateinit var backBtn: ImageButton
    private lateinit var nextBtn: Button



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root =
            inflater.inflate(R.layout.confirm_phone_fragment, container, false)

        initViews(root)

        setListeners(root)

        //set listener to move button in relation to keyboard on/off
        setMovingButtonListener(root)

        //set listeners to input code
        code1EditText.setOnKeyListener(this)
        code2EditText.setOnKeyListener(this)
        code3EditText.setOnKeyListener(this)
        code4EditText.setOnKeyListener(this)


        //display keyboard and set focus
        code1EditText.requestFocus()
        showKeyboard(activity!!)

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


    override fun onKey(v: View, keyCode: Int, event: KeyEvent): Boolean {

        if (keyCode != KeyEvent.KEYCODE_DEL && v.id == R.id.code1 && event.action == KeyEvent.ACTION_DOWN) {
            code2EditText.requestFocus()
        }

        if (v.id == R.id.code2 && event.action == KeyEvent.ACTION_DOWN) {
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                code1EditText.requestFocus()
            } else {
                code3EditText.requestFocus()
            }
        }

        if (v.id == R.id.code3 && event.action == KeyEvent.ACTION_DOWN) {
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                code2EditText.requestFocus()
            } else {
                code4EditText.requestFocus()
            }
        }

        if (keyCode == KeyEvent.KEYCODE_DEL && v.id == R.id.code4 && event.action == KeyEvent.ACTION_DOWN) {
            code3EditText.requestFocus()
        }

        return false
    }


    private fun setListeners(root: View) {
        retrySend.setOnClickListener {
            Toast.makeText(context, "Retry send", Toast.LENGTH_LONG).show()
        }

        nextBtn.setOnClickListener {
            hideKeyboard(activity!!, root)

            coordinator!!.replaceFragment(FULL_NAME_VIEW, startHeight, screenHeight)
        }

        backBtn.setOnClickListener {
            hideKeyboard(activity!!, root)

            (activity as MainActivity).supportFragmentManager.popBackStack()
        }
    }


    private fun initViews(root: View) {
        code1EditText = root.findViewById(R.id.code1)
        code2EditText = root.findViewById(R.id.code2)
        code3EditText = root.findViewById(R.id.code3)
        code4EditText = root.findViewById(R.id.code4)
        retrySend = root.findViewById(R.id.retry_send)
        backBtn = root.findViewById(R.id.back_btn)
        nextBtn = root.findViewById(R.id.next)
    }


    init {
        if(coordinator == null){
            coordinator = (activity as MainActivity).coordinator
        }
    }


}