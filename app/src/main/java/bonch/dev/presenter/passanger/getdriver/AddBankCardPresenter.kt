package bonch.dev.presenter.passanger.getdriver

import android.graphics.Rect
import android.text.Editable
import android.view.View
import bonch.dev.R
import bonch.dev.view.passanger.getdriver.AddBankCardView
import kotlinx.android.synthetic.main.add_bank_card_activity.view.*

class AddBankCardPresenter(val addBankCardView: AddBankCardView) {

    private var lock = false
    private var startHeight: Int = 0
    private var screenHeight: Int = 0
    private val VISA = 4
    private val MC = 5
    private val RUS_WORLD = 2


    fun setMovingButtonListener(root: View) {
        val addCardBtn = root.add_card
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


    fun maskCardNumber(s: Editable?) {
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


    fun maskValidUntil(s: Editable?) {
        if (s!!.length == 3 && s.toString()[2] != '/') {
            val filters = s.filters
            s.filters = arrayOf()
            s.insert(2, "/")
            s.filters = filters
        }
    }


    fun setHintListener(root: View) {
        val cardBankNumber = root.card_number
        val validUntil = root.valid_until
        val cvc = root.cvc

        cardBankNumber.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                cardBankNumber.hint = ""
            } else {
                cardBankNumber.hint = addBankCardView.getString(R.string.numberBankCard)
            }
        }

        validUntil.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                validUntil.hint = ""
            } else {
                validUntil.hint = addBankCardView.getString(R.string.valid_until)
            }
        }

        cvc.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                cvc.hint = ""
            } else {
                cvc.hint = addBankCardView.getString(R.string.cvc_cvv)
            }
        }
    }


    fun addCardBank(root: View) {
        if (isValidCard(root)) {
            var cardNumber = root.card_number.text.toString()
            val imgCard: Int?

            when (cardNumber[0].toString().toInt()) {
                VISA -> {
                    imgCard = R.drawable.ic_visa
                    cardNumber = "•••• " + cardNumber.substring(15, 19)
                }

                MC -> {
                    imgCard = R.drawable.ic_mastercard
                    cardNumber = "•••• " + cardNumber.substring(15, 19)
                }

                RUS_WORLD -> {
                    imgCard = R.drawable.ic_pay_world
                    cardNumber = "•••• " + cardNumber.substring(15, 19)
                }

                else -> {
                    imgCard = null
                    cardNumber = "•••• " + cardNumber.substring(15, 19)
                }
            }

            addBankCardView.addBankCardDone(root, imgCard, cardNumber)
        }
    }


    fun isValidCard(root: View): Boolean {
        val addCardBtn = root.add_card
        val cardBankNumber = root.card_number
        val validUntil = root.valid_until
        val cvc = root.cvc
        var isValid = false

        if (cardBankNumber.text.length == 19 && cvc.text.length == 3 && validUntil.text.length == 5) {
            addCardBtn.setBackgroundResource(R.drawable.bg_btn_blue)
            isValid = true
        } else {
            addCardBtn.setBackgroundResource(R.drawable.bg_btn_gray)
        }

        return isValid
    }
}