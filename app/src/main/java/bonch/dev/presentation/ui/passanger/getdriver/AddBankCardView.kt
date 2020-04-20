package bonch.dev.presentation.ui.passanger.getdriver

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import bonch.dev.R
import bonch.dev.presentation.presenter.passanger.getdriver.AddBankCardPresenter
import bonch.dev.utils.Constants.CARD_IMG
import bonch.dev.utils.Constants.CARD_NUMBER
import bonch.dev.utils.Constants.CVC
import bonch.dev.utils.Constants.VALID_UNTIL
import bonch.dev.utils.Keyboard
import bonch.dev.utils.Keyboard.hideKeyboard
import bonch.dev.utils.Keyboard.showKeyboard
import kotlinx.android.synthetic.main.add_bank_card_activity.view.*


class AddBankCardView : AppCompatActivity() {

    private var addBankCardPresenter: AddBankCardPresenter? = null

    init {
        addBankCardPresenter = AddBankCardPresenter(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_bank_card_activity)

        val root = findViewById<View>(R.id.rootLinearLayout)

        setListener(root)

        addBankCardPresenter?.setHintListener(root)

        Keyboard.setMovingButtonListener(root)

        root.card_number.requestFocus()
        showKeyboard(this)
    }


    private fun setListener(root: View) {
        val addCardBtn = root.btn_done
        val backBtn = root.back_btn
        val cardBankNumber = root.card_number
        val validUntil = root.valid_until
        val cvc = root.cvc

        addCardBtn.setOnClickListener {
            addBankCardPresenter?.addCardBank(root)
        }

        backBtn.setOnClickListener {
            hideKeyboard(this, root)
            finish()
        }

        cardBankNumber.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                addBankCardPresenter?.maskCardNumber(s)
                addBankCardPresenter?.isValidCard(root)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })

        validUntil.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                addBankCardPresenter?.maskValidUntil(s)
                addBankCardPresenter?.isValidCard(root)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })

        cvc.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                addBankCardPresenter?.isValidCard(root)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })
    }


    fun addBankCardDone(root: View, imgCard: Int?, cardNumber: String){
        val validUntil = root.valid_until
        val cvc = root.cvc
        val intent = Intent()

        intent.putExtra(CARD_NUMBER, cardNumber)
        intent.putExtra(VALID_UNTIL, validUntil.text.toString())
        intent.putExtra(CVC, cvc.text.toString())
        intent.putExtra(CARD_IMG, imgCard)
        setResult(RESULT_OK, intent)

        hideKeyboard(this, root)
        finish()
    }


    override fun onPause() {
        super.onPause()
        hideKeyboard(this, findViewById<LinearLayout>(R.id.rootLinearLayout))
    }
}
