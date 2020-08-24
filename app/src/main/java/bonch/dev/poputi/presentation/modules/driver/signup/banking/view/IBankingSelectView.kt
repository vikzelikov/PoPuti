package bonch.dev.poputi.presentation.modules.driver.signup.banking.view

import bonch.dev.poputi.presentation.interfaces.IBaseView
import bonch.dev.poputi.presentation.modules.driver.signup.banking.adapters.BankingSelectAdapter

interface IBankingSelectView : IBaseView{

    fun getAdapter():BankingSelectAdapter

    fun removeTickSelected()

    fun showNextBtn()

}