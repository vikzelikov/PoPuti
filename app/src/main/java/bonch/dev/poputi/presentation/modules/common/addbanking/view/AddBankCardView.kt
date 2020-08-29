package bonch.dev.poputi.presentation.modules.common.addbanking.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import bonch.dev.poputi.R
import bonch.dev.poputi.domain.entities.common.banking.BankCard
import bonch.dev.poputi.domain.utils.Keyboard
import bonch.dev.poputi.domain.utils.Keyboard.hideKeyboard
import bonch.dev.poputi.domain.utils.Keyboard.showKeyboard
import bonch.dev.poputi.presentation.modules.passenger.getdriver.GetDriverComponent
import bonch.dev.poputi.presentation.modules.common.addbanking.presenter.IAddBankCardPresenter
import kotlinx.android.synthetic.main.add_bank_card_activity.*
import javax.inject.Inject


class AddBankCardView : AppCompatActivity(),
    IAddBankCardView {

    @Inject
    lateinit var addBankCardPresenter: IAddBankCardPresenter

    private var handlerAnimation: Handler? = null

    private val ADD_BANK_CARD = 2

    private val VISA = 4
    private val MC = 5
    private val RUS_WORLD = 2


    init {
        GetDriverComponent.getDriverComponent?.inject(this)

        addBankCardPresenter.instance().attachView(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_bank_card_activity)

        Keyboard.setMovingButtonListener(this.rootLinearLayout, false)

        card_number.requestFocus()
        showKeyboard(this)

        setHintListener()

        setListeners()
    }


    override fun setListeners() {
        btn_done.setOnClickListener {
            addBankCardPresenter.addCardBank(card_number.text.toString())
        }

        back_btn.setOnClickListener {
            hideKeyboard()
            finish()
        }

        card_number.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                addBankCardPresenter.maskCardNumber(s)
                isValidCard()

                s?.let {
                    if (it.toString().length == 19) {
                        valid_until?.requestFocus()
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })

        valid_until.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                addBankCardPresenter.maskValidUntil(s)
                isValidCard()

                s?.let {
                    if (it.toString().length == 5) {
                        cvc?.requestFocus()
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })

        cvc.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                isValidCard()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })

        cvc.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                addBankCardPresenter.addCardBank(card_number.text.toString())
                return@OnEditorActionListener true
            }
            false
        })
    }


    override fun addBankCardDone(numberCard: String) {
        val intent = Intent()

        val bankCard = BankCard()
        bankCard.numberCard = numberCard
        bankCard.validUntil = valid_until.text.toString()
        bankCard.cvc = cvc.text.toString()

        intent.putExtra(ADD_BANK_CARD.toString(), bankCard)
        setResult(RESULT_OK, intent)

        hideKeyboard()
        finish()
    }


    override fun isValidCard(): Boolean {
        var isValid = false

        if (isValidNumberCard() && cvc.text.length == 3 && isValidUntil()) {
            btn_done.setBackgroundResource(R.drawable.bg_btn_blue)
            isValid = true
        } else {
            btn_done.setBackgroundResource(R.drawable.bg_btn_gray)
        }

        return isValid
    }


    private fun isValidNumberCard(): Boolean {
        var isVaild = false
        try {
            val firstDigit = card_number.text.toString()[0] - '0'
            isVaild = ((firstDigit == VISA || firstDigit == RUS_WORLD || firstDigit == MC)
                    && card_number.text.length == 19)
        } catch (ex: StringIndexOutOfBoundsException) {

        } catch (ex: NumberFormatException) {

        } catch (ex: Exception) {

        }

        return isVaild

    }


    private fun isValidUntil(): Boolean {
        var isVaild = false

        try {
            if (valid_until.text.substring(0, 2).toInt() in 1..12
                && valid_until.text.substring(3, 5).toInt() >= 20
            ) {

                isVaild = true

            }
        } catch (ex: StringIndexOutOfBoundsException) {

        } catch (ex: NumberFormatException) {

        } catch (ex: Exception) {

        }

        return isVaild
    }


    override fun setHintListener() {
        card_number.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            card_number_layout.isHintEnabled = hasFocus
        }

        valid_until.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            valid_until_layout.isHintEnabled = hasFocus
        }

        cvc.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            cvc_layout.isHintEnabled = hasFocus
        }
    }


    override fun showNotification(text: String) {
        val view = general_notification

        view.text = text
        handlerAnimation?.removeCallbacksAndMessages(null)
        handlerAnimation = Handler()
        view.translationY = 0.0f
        view.alpha = 0.0f

        view.animate()
            .setDuration(500L)
            .translationY(100f)
            .alpha(1.0f)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    handlerAnimation?.postDelayed({ hideNotifications() }, 2000)
                }
            })
    }


    private fun hideNotifications() {
        val view = general_notification

        view.animate()
            .setDuration(500L)
            .translationY(-100f)
            .alpha(0.0f)
    }


    override fun hideKeyboard() {
        hideKeyboard(this, this.rootLinearLayout)
    }


    override fun showLoading() {}


    override fun hideLoading() {}


    override fun getNavHost(): NavController? {
        return null
    }


    override fun onPause() {
        hideKeyboard()
        super.onPause()
    }


    override fun onDestroy() {
        addBankCardPresenter.instance().detachView()
        super.onDestroy()
    }
}
