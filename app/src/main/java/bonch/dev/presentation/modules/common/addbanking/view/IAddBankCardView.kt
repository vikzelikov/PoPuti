package bonch.dev.presentation.modules.common.addbanking.view

import bonch.dev.presentation.interfaces.IBaseView

interface IAddBankCardView : IBaseView {

    fun isValidCard(): Boolean

    fun addBankCardDone(imgCard: Int?, numberCard: String)

    fun setHintListener()

    fun showNotification(text: String)

}