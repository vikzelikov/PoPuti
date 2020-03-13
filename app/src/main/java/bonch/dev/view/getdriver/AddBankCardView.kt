package bonch.dev.view.getdriver

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import bonch.dev.R
import bonch.dev.utils.Keyboard.hideKeyboard
import bonch.dev.utils.Keyboard.showKeyboard


class AddBankCardView : AppCompatActivity() {

    private val CARD_NUMBER = "CARD_NUMBER"
    private val VALID_UNTIL = "VALID_UNTIL"
    private val CVC = "CVC"
    private val BANK_IMG = "BANK_IMG"
    private val VISA = 4
    private val MC = 5
    private val RUS_WORLD = 2

    private lateinit var addCardBtn: Button
    private lateinit var backBtn: ImageButton
    private lateinit var cardBankNumber: EditText
    private lateinit var validUntil: EditText
    private lateinit var cvc: EditText
    private var startHeight: Int = 0
    private var screenHeight: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_bank_card_activity)

        val root = findViewById<View>(R.id.rootLinearLayout)

        initViews()

        setListener(root)

        setHintListener()

        setMovingButtonListener(root)

        cardBankNumber.requestFocus()
        showKeyboard(this)
    }


    private fun setListener(root: View) {
        var lock = false


        addCardBtn.setOnClickListener {
            if (isValidCard()) {
                var img: Int? = null
                val intent = Intent()
                var cardNumber = cardBankNumber.text.toString()

                when (cardNumber[0].toString().toInt()) {
                    VISA -> {
                        img = R.drawable.ic_visa
                        cardNumber = "•••• " + cardNumber.substring(15, 19)
                    }

                    MC -> {
                        img = R.drawable.ic_mastercard
                        cardNumber = "•••• " + cardNumber.substring(15, 19)
                    }

                    RUS_WORLD -> {
                        img = R.drawable.ic_pay_world
                        cardNumber = "•••• " + cardNumber.substring(15, 19)
                    }

                    else -> {
                        img = null
                        cardNumber = "•••• " + cardNumber.substring(15, 19)
                    }
                }


                intent.putExtra(CARD_NUMBER, cardNumber)
                intent.putExtra(VALID_UNTIL, validUntil.text.toString())
                intent.putExtra(CVC, cvc.text.toString())
                intent.putExtra(BANK_IMG, img)
                setResult(RESULT_OK, intent)

                hideKeyboard(this, root)

                finish()
            }

        }

        backBtn.setOnClickListener {
            hideKeyboard(this, root)

            finish()
        }

        cardBankNumber.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (lock || s!!.length > 16) {
                    return
                }
                lock = true

                var i = 4
                while (i < s.length) {
                    if (s.toString()[i] != ' ') {
                        val filters = s.filters
                        s.filters = arrayOf()
                        s.insert(i, " ")
                        s.filters = filters
                    }
                    i += 5
                }

                lock = false
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })

        validUntil.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s!!.length == 3 && s.toString()[2] != '/') {
                    val filters = s.filters
                    s.filters = arrayOf()
                    s.insert(2, "/")
                    s.filters = filters
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })
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
                    btnDefaultPosition = addCardBtn.y
                }

                if (startHeight == 0) {
                    startHeight = screenHeight - (rect.bottom - rect.top)
                }


                if (heightDiff > startHeight) {
                    //move UP
                    addCardBtn.y = btnDefaultPosition - heightDiff + startHeight
                } else {
                    //move DOWN
                    addCardBtn.y = btnDefaultPosition
                }
            }
    }


    private fun isValidCard(): Boolean {
        var isValid = false

        if (cardBankNumber.text.length == 19 && cvc.text.length == 3 && validUntil.text.length == 5) {
            isValid = true
        }

        return isValid
    }


    private fun setHintListener() {
        cardBankNumber.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                cardBankNumber.hint = ""
            }else{
                cardBankNumber.hint = getString(R.string.numberBankCard)
            }
        }

        validUntil.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                validUntil.hint = ""
            }else{
                validUntil.hint = getString(R.string.valid_until)
            }
        }

        cvc.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                cvc.hint = ""
            }else{
                cvc.hint = getString(R.string.cvc_cvv)
            }
        }
    }


    private fun initViews() {
        addCardBtn = findViewById(R.id.add_card)
        backBtn = findViewById(R.id.back_btn)
        cardBankNumber = findViewById(R.id.card_number)
        validUntil = findViewById(R.id.valid_until)
        cvc = findViewById(R.id.cvc)
    }
}
