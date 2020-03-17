package bonch.dev.presenter.getdriver

import android.graphics.Rect
import android.os.Handler
import android.text.Editable
import android.util.Log
import android.view.View
import bonch.dev.R
import bonch.dev.utils.Constants.OFFER_PRICE
import kotlinx.android.synthetic.main.offer_price_activity.view.*
import java.lang.IndexOutOfBoundsException
import java.util.*

class OfferPricePresenter {

    private var startHeight: Int = 0
    private var screenHeight: Int = 0

    fun setMovingButtonListener(root: View) {
        val offerBtn = root.offer
        var heightDiff = 0
        var btnDefaultPosition = 0.0f
        val rect = Rect()


        root.viewTreeObserver
            .addOnGlobalLayoutListener {

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

                println("$heightDiff $startHeight")
                if (heightDiff > startHeight) {
                    //move UP
//                    Handler().postDelayed({
//                        //doSomethingHere()
//                    }, 1000)
                    offerBtn.y = btnDefaultPosition - heightDiff + startHeight
                } else {
                    //move DOWN
                    offerBtn.y = btnDefaultPosition
                }

            }
    }


    fun maskListener(root: View, s: Editable?) {
        val priceEditText = root.price
        val offerBtn = root.offer

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


    fun touchPriceEditText(root: View) {
        val price = root.price

        try {
            price.setSelection(price.text.length - 2)
        } catch (ex: IndexOutOfBoundsException) {
            ex.printStackTrace()
        }
    }
}


