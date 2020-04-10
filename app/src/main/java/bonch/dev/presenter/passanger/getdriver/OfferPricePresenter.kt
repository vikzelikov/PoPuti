package bonch.dev.presenter.passanger.getdriver

import android.graphics.Rect
import android.text.Editable
import android.view.View
import bonch.dev.R
import bonch.dev.model.passanger.getdriver.OfferPriceModel
import bonch.dev.view.passanger.getdriver.OfferPriceView
import kotlinx.android.synthetic.main.offer_price_activity.view.*


class OfferPricePresenter(val offerPriceView: OfferPriceView) {

    var offerPriceModel: OfferPriceModel? = null

    init {
        if (offerPriceModel == null) {
            offerPriceModel = OfferPriceModel(this)
        }
    }


    fun maskListener(root: View, s: Editable?) {
        val priceEditText = root.price
        val offerBtn = root.btn_done

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

        if (s.isNotEmpty()) {
            offerBtn.setBackgroundResource(R.drawable.bg_btn_blue)
        } else {
            offerBtn.setBackgroundResource(R.drawable.bg_btn_gray)
        }
    }


    fun getAveragePrice(): Int? {
        return offerPriceModel?.getAveragePrice()
    }


    fun touchPriceEditText(root: View) {
        val price = root.price

        try {
            price.setSelection(price.text.length - 2)
        } catch (ex: IndexOutOfBoundsException) {
            ex.printStackTrace()
        }
    }
}


