package bonch.dev.poputi.presentation.modules.common.addbanking.view

import bonch.dev.poputi.presentation.interfaces.IBaseView

interface IAddBankCardView : IBaseView {

    fun isValidCard(): Boolean

    fun addBankCardDone(imgCard: Int?, numberCard: String)

    fun setHintListener()

}