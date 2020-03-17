package bonch.dev.view.getdriver

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import bonch.dev.R
import bonch.dev.presenter.getdriver.OfferPricePresenter
import bonch.dev.utils.Constants.OFFER_PRICE
import bonch.dev.utils.Keyboard.hideKeyboard
import bonch.dev.utils.Keyboard.showKeyboard
import kotlinx.android.synthetic.main.offer_price_activity.view.*

class OfferPriceView : AppCompatActivity() {

    private var offerPricePresenter: OfferPricePresenter? = null

    init {
        offerPricePresenter = OfferPricePresenter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.offer_price_activity)

        val root = findViewById<View>(R.id.rootLinearLayout)

        setListeners(root)

        //set listener to move button in relation to keyboard on/off
        offerPricePresenter?.setMovingButtonListener(root)


        //display keyboard and set focus
        root.price.requestFocus()
        showKeyboard(this)

    }


    private fun setListeners(root: View) {
        val priceEditText = root.price
        val offerBtn = root.offer
        val backBtn = root.back_btn


        backBtn.setOnClickListener {
            hideKeyboard(this, root)
            finish()
        }

        offerBtn.setOnClickListener {
            val intent = Intent()
            val price = priceEditText.text.toString()
            if (price.length < 7 && price.isNotEmpty()) {
                hideKeyboard(this, root)
                intent.putExtra(OFFER_PRICE, price.substring(0, price.length - 2))
                setResult(RESULT_OK, intent)
                finish()
            }

        }

        priceEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                offerPricePresenter?.maskListener(root, s)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })

        priceEditText.setOnClickListener {
            offerPricePresenter?.touchPriceEditText(root)
        }
    }


    override fun onPause() {
        super.onPause()
        hideKeyboard(this, findViewById<LinearLayout>(R.id.rootLinearLayout))
    }
}
