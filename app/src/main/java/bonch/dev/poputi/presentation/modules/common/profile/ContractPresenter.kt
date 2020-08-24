package bonch.dev.poputi.presentation.modules.common.profile

import android.app.Activity
import android.content.Intent
import bonch.dev.poputi.domain.entities.common.banking.BankCard
import bonch.dev.poputi.presentation.modules.common.profile.banking.BankingPresenter

interface ContractPresenter {

    interface IBankingPresenter {
        fun addCard(activity: Activity)
        fun addBankCardDone(data: Intent?)
        fun instance(): BankingPresenter
        fun getBankCards(): ArrayList<BankCard>
        fun deleteBankCard(card: BankCard)
        fun doneEdit()
        fun editMode()
    }

}