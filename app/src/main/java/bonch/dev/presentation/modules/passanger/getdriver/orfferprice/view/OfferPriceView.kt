package bonch.dev.presentation.modules.passanger.getdriver.orfferprice.view

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import bonch.dev.R
import bonch.dev.presentation.modules.passanger.getdriver.orfferprice.presenter.OfferPricePresenter
import bonch.dev.domain.utils.Constants.AVERAGE_PRICE
import bonch.dev.domain.utils.Constants.OFFER_PRICE
import bonch.dev.domain.utils.Keyboard
import bonch.dev.domain.utils.Keyboard.hideKeyboard
import bonch.dev.domain.utils.Keyboard.showKeyboard
import kotlinx.android.synthetic.main.offer_price_activity.view.*

class OfferPriceView : AppCompatActivity() {

    private var offerPricePresenter: OfferPricePresenter? = null

    init {
        offerPricePresenter =
            OfferPricePresenter(
                this
            )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.offer_price_activity)

        val root = findViewById<View>(R.id.rootLinearLayout)

        setListeners(root)

        //set listener to move button in relation to keyboard on/off
        Keyboard.setMovingButtonListener(root, false)

        val averagePrice = offerPricePresenter?.getAveragePrice()
        setAveragePrice(root, averagePrice)

        //display keyboard and set focus
        root.price.requestFocus()
        showKeyboard(this)

    }


    private fun setListeners(root: View) {
        val priceEditText = root.price
        val offerBtn = root.btn_done
        val backBtn = root.back_btn


        backBtn.setOnClickListener {
            hideKeyboard(this, root)
            finish()
        }

        offerBtn.setOnClickListener {
            val intent = Intent()
            val price = priceEditText.text.toString().split('₽')[0].trim()
            val averagePrice = offerPricePresenter?.getAveragePrice()
            if (price.length < 7 && price.isNotEmpty() && price.toInt() > 1) {
                hideKeyboard(this, root)
                intent.putExtra(OFFER_PRICE, price)
                intent.putExtra(AVERAGE_PRICE, averagePrice)
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


    private fun setAveragePrice(root: View, averagePrice: Int?) {
        val averageTextView = root.average_price

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            averageTextView.text = Html.fromHtml(
                resources.getString(R.string.offer_price_average_price)
                    .plus(" <b>$averagePrice рублей</b>"), Html.FROM_HTML_MODE_COMPACT
            )

        } else {
            averageTextView.text = Html.fromHtml(
                resources.getString(R.string.offer_price_average_price)
                    .plus(" <b>$averagePrice рублей</b>")
            )
        }
    }


    override fun onPause() {
        super.onPause()
        hideKeyboard(this, findViewById<LinearLayout>(R.id.rootLinearLayout))
    }
}
