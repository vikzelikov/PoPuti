package bonch.dev.poputi.presentation.modules.driver.signup.banking.presenter

import android.content.Intent
import androidx.fragment.app.Fragment
import bonch.dev.poputi.domain.entities.common.banking.BankCard

interface IBankingSelectPresenter {

    fun next()

    fun getBankCards(): ArrayList<BankCard>

    fun addCard(fragment: Fragment)

    fun addBankCardDone(data: Intent?)

    fun removeTickSelected()

    fun selectBankCard(card: BankCard)

    fun instance(): BankingSelectPresenter

}