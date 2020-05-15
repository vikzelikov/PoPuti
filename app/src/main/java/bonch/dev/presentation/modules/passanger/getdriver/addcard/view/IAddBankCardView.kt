package bonch.dev.presentation.modules.passanger.getdriver.addcard.view

import bonch.dev.presentation.interfaces.IBaseView

interface IAddBankCardView : IBaseView {

    fun isValidCard(): Boolean

    fun addBankCardDone(imgCard: Int?, numberCard: String)

    fun setHintListener()

    fun showNotification(text: String)

}