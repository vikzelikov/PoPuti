package bonch.dev.poputi.presentation.modules.common.addbanking.presenter

import android.text.Editable
import bonch.dev.poputi.App
import bonch.dev.poputi.R
import bonch.dev.poputi.presentation.base.BasePresenter
import bonch.dev.poputi.presentation.modules.common.addbanking.view.IAddBankCardView
import java.lang.Exception
import java.lang.IndexOutOfBoundsException

class AddBankCardPresenter : BasePresenter<IAddBankCardView>(),
    IAddBankCardPresenter {

    private var lock = false
    private val VISA = 4
    private val MC = 5
    private val RUS_WORLD = 2


    override fun maskCardNumber(s: Editable?) {
        if (s != null && (lock || s.length > 16)) {
            return
        }
        lock = true

        var i = 4
        while (s != null && i < s.length) {
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


    override fun maskValidUntil(s: Editable?) {
        if (s != null && s.length == 3 && s.toString()[2] != '/') {
            val filters = s.filters
            s.filters = arrayOf()
            s.insert(2, "/")
            s.filters = filters
        }
    }


    override fun addCardBank(card: String) {
        getView()?.let {
            if (it.isValidCard()) {
                var cardNumber = card
                val imgCard: Int?
                val hideChars = "•••• "

                try {
                    when (cardNumber[0].toString().toInt()) {
                        VISA -> {
                            imgCard = R.drawable.ic_visa
                            cardNumber = hideChars + cardNumber.substring(15, 19)
                        }

                        MC -> {
                            imgCard = R.drawable.ic_mastercard
                            cardNumber = hideChars + cardNumber.substring(15, 19)
                        }

                        RUS_WORLD -> {
                            imgCard = R.drawable.ic_pay_world
                            cardNumber = hideChars + cardNumber.substring(15, 19)
                        }

                        else -> {
                            imgCard = null
                            cardNumber = hideChars + cardNumber.substring(15, 19)
                        }
                    }

                    getView()?.addBankCardDone(imgCard, cardNumber)

                } catch (ex: StringIndexOutOfBoundsException) {

                } catch (ex: IndexOutOfBoundsException) {

                } catch (ex: Exception) {

                }

            } else {
                val context = App.appComponent.getContext()
                getView()?.showNotification(context.getString(R.string.bankCardValidData))
            }
        }
    }


    override fun instance(): AddBankCardPresenter {
        return this
    }
}