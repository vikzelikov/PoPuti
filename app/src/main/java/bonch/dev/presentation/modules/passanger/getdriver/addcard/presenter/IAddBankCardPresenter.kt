package bonch.dev.presentation.modules.passanger.getdriver.addcard.presenter

import android.text.Editable

interface IAddBankCardPresenter {

    fun instance(): AddBankCardPresenter

    fun maskCardNumber(s: Editable?)

    fun maskValidUntil(s: Editable?)

    fun addCardBank(card: String)

}