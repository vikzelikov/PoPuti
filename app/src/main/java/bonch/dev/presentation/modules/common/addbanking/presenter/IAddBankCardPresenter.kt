package bonch.dev.presentation.modules.common.addbanking.presenter

import android.text.Editable
import bonch.dev.presentation.modules.common.addbanking.presenter.AddBankCardPresenter

interface IAddBankCardPresenter {

    fun instance(): AddBankCardPresenter

    fun maskCardNumber(s: Editable?)

    fun maskValidUntil(s: Editable?)

    fun addCardBank(card: String)

}