package bonch.dev.view.getdriver

import android.app.Activity
import android.content.Intent
import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import bonch.dev.MainActivity
import bonch.dev.MainActivity.Companion.hideKeyboard
import bonch.dev.MainActivity.Companion.showKeyboard
import bonch.dev.R

class OfferPriceView : AppCompatActivity() {

    private val OFFER_PRICE = "OFFER_PRICE"
    private var startHeight: Int = 0
    private var screenHeight: Int = 0
    private lateinit var offerBtn: Button
    private lateinit var backBtn: ImageButton
    private lateinit var priceEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.offer_price_activity)

        val root = findViewById<View>(R.id.rootLinearLayout)

        initViews()

        setListeners(root)

        //set listener to move button in relation to keyboard on/off
        setMovingButtonListener(root)


        //display keyboard and set focus
        priceEditText.requestFocus()
        showKeyboard(this)

    }


    private fun setListeners(root: View) {
        backBtn.setOnClickListener {
            hideKeyboard(this, root)

            finish()
        }

        offerBtn.setOnClickListener {
            hideKeyboard(this, root)

            val intent = Intent()
            val price = priceEditText.text.toString()
            intent.putExtra(OFFER_PRICE, price.substring(0, price.length - 2))
            setResult(RESULT_OK, intent)

            finish()
        }

        priceEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s!!.isNotEmpty() && s[s.length - 1] != '₽') {
                    val filters = s.filters
                    s.filters = arrayOf()
                    s.append(" ₽")
                    s.filters = filters
                }

                val str = s.toString().split('₽')

                if (str.size > 1 && str[1].isNotEmpty()) {
                    priceEditText.setText(str[0].substring(0, str[0].length - 1))
                }

                if (str[0].trim().isEmpty()) {
                    s.clear()
                }


                if (s.length > 1) {
                    priceEditText.setSelection(priceEditText.text.length - 2)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })

        priceEditText.setOnClickListener {
            priceEditText.setSelection(priceEditText.text.length - 2)
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
                    btnDefaultPosition = offerBtn.y
                }

                if (startHeight == 0) {
                    startHeight = screenHeight - (rect.bottom - rect.top)
                }


                if (heightDiff > startHeight) {
                    //move UP
                    offerBtn.y = btnDefaultPosition - heightDiff + startHeight
                } else {
                    //move DOWN
                    offerBtn.y = btnDefaultPosition
                }
            }
    }


    private fun initViews() {
        priceEditText = findViewById(R.id.price)
        backBtn = findViewById(R.id.back_btn)
        offerBtn = findViewById(R.id.offer)
    }
}
