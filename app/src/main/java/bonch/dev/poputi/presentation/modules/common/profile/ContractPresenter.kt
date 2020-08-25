package bonch.dev.poputi.presentation.modules.common.profile

import android.app.Activity
import android.content.Intent
import androidx.fragment.app.Fragment
import bonch.dev.poputi.domain.entities.common.banking.BankCard
import bonch.dev.poputi.domain.entities.common.profile.verification.VerifyStep
import bonch.dev.poputi.domain.entities.driver.signup.Step
import bonch.dev.poputi.presentation.modules.common.profile.banking.BankingPresenter
import bonch.dev.poputi.presentation.modules.common.profile.driver.profits.ProfitsPresenter
import bonch.dev.poputi.presentation.modules.common.profile.passenger.verification.VerifyPresenter

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


    interface IVerifyPresenter {
        fun sortDocs()
        fun loadPhoto()
        fun checkVerification()
        fun createVerification()
        fun instance(): VerifyPresenter
        fun onActivityResult(activity: Activity, requestCode: Int, resultCode: Int, data: Intent?)
        fun getCamera(activity: Activity)
        fun getByValue(step: Int): VerifyStep?
        fun isBlockBack(): Boolean
        fun resetData()
    }


    interface IProfitsPresenter {
        fun nextList()
        fun backList()
        fun calcDate(isNext: Boolean?)
        fun instance(): ProfitsPresenter
    }

}