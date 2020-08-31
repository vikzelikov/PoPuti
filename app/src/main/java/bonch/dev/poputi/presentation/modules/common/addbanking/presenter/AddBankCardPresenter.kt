package bonch.dev.poputi.presentation.modules.common.addbanking.presenter

import android.text.Editable
import bonch.dev.poputi.App
import bonch.dev.poputi.R
import bonch.dev.poputi.presentation.base.BasePresenter
import bonch.dev.poputi.presentation.modules.common.addbanking.view.IAddBankCardView

class AddBankCardPresenter : BasePresenter<IAddBankCardView>(),
    IAddBankCardPresenter {

    private var lock = false


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


    override fun addCardBank(card: String?) {
        getView()?.let {
            if (it.isValidCard()) {

                card?.let { getView()?.addBankCardDone(card) }

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