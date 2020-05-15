package bonch.dev.presentation.modules.common.orfferprice.view

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import bonch.dev.App
import bonch.dev.R
import bonch.dev.di.component.common.DaggerCommonComponent
import bonch.dev.di.module.common.CommonModule
import bonch.dev.domain.utils.Keyboard
import bonch.dev.domain.utils.Keyboard.hideKeyboard
import bonch.dev.domain.utils.Keyboard.showKeyboard
import bonch.dev.presentation.modules.common.CommonComponent
import bonch.dev.presentation.modules.passanger.getdriver.GetDriverComponent
import bonch.dev.presentation.modules.common.orfferprice.presenter.IOfferPricePresenter
import kotlinx.android.synthetic.main.offer_price_activity.*
import java.lang.NumberFormatException
import javax.inject.Inject

class OfferPriceView : AppCompatActivity(), IOfferPriceView {

    @Inject
    lateinit var offerPricePresenter: IOfferPricePresenter

    private val OFFER_PRICE = 1
    private val AVERAGE_PRICE = "AVERAGE_PRICE"

    init {
        initDI()

        CommonComponent.commonComponent?.inject(this)

        offerPricePresenter.instance().attachView(this)
    }


    //first build DI component
    private fun initDI() {
        if (CommonComponent.commonComponent == null) {
            CommonComponent.commonComponent = DaggerCommonComponent
                .builder()
                .commonModule(CommonModule())
                .appComponent(App.appComponent)
                .build()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.offer_price_activity)

        //set listener to move button in relation to keyboard on/off
        Keyboard.setMovingButtonListener(this.rootLinearLayout, false)

        val averagePrice = offerPricePresenter.getAveragePrice()
        setAveragePrice(averagePrice)

        //display keyboard and set focus
        price.requestFocus()
        showKeyboard(this)

        setListeners()
    }


    override fun setListeners() {
        val priceEditText = price
        val offerBtn = btn_done
        val backBtn = back_btn


        backBtn.setOnClickListener {
            hideKeyboard()
            finish()
        }

        offerBtn.setOnClickListener {
            val intent = Intent()
            val price = priceEditText.text.toString().split('₽')[0].trim()
            val averagePrice = offerPricePresenter.getAveragePrice()
            try {
                if (price.length < 7 && price.isNotEmpty() && price.toInt() > 0) {
                    hideKeyboard()
                    intent.putExtra(OFFER_PRICE.toString(), price.toInt())
                    intent.putExtra(AVERAGE_PRICE, averagePrice)
                    setResult(RESULT_OK, intent)
                    finish()
                }
            }catch (ex: NumberFormatException){
            }
        }

        priceEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                s?.let { maskListener(s) }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })

        priceEditText.setOnClickListener {
            touchPriceEditText()
        }
    }


    override fun setAveragePrice(averagePrice: Int?) {
        if (averagePrice == null) {
            average_price.visibility = View.GONE
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            average_price.text = Html.fromHtml(
                resources.getString(R.string.offer_price_average_price)
                    .plus(" <b>$averagePrice ${resources.getString(R.string.rub)}</b>"),
                Html.FROM_HTML_MODE_COMPACT
            )

        } else {
            average_price.text = Html.fromHtml(
                resources.getString(R.string.offer_price_average_price)
                    .plus(" <b>$averagePrice ${resources.getString(R.string.rub)}</b>")
            )
        }
    }


    override fun maskListener(s: Editable) {
        if (s.isNotEmpty() && s[s.length - 1] != '₽') {
            val filters = s.filters
            s.filters = arrayOf()
            s.append(" ₽")
            s.filters = filters
        }

        val str = s.toString().split('₽')

        if (str.size > 1 && str[1].isNotEmpty()) {
            price.setText(str[0].substring(0, str[0].length - 1))
        }

        if (str[0].trim().isEmpty()) {
            s.clear()
        }

        if (s.length > 1) {
            price.setSelection(price.text.length - 2)
        }

        if (s.isNotEmpty() && str[0].trim().toInt() > 0) {
            btn_done.setBackgroundResource(R.drawable.bg_btn_blue)
        } else {
            btn_done.setBackgroundResource(R.drawable.bg_btn_gray)
        }
    }


    override fun touchPriceEditText() {
        try {
            price.setSelection(price.text.length - 2)
        } catch (ex: IndexOutOfBoundsException) {
            ex.printStackTrace()
        }
    }


    override fun hideKeyboard() {
        hideKeyboard(this, this.rootLinearLayout)
    }


    override fun getNavHost(): NavController? {
        return null
    }


    override fun onPause() {
        hideKeyboard()
        super.onPause()
    }


    override fun onDestroy() {
        offerPricePresenter.instance().detachView()
        CommonComponent.commonComponent = null
        super.onDestroy()
    }
}
