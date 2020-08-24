package bonch.dev.poputi.presentation.modules.common.profile.banking

import android.app.Activity
import android.content.Intent
import android.util.Log
import bonch.dev.poputi.domain.entities.common.banking.BankCard
import bonch.dev.poputi.domain.interactor.common.profile.IProfileInteractor
import bonch.dev.poputi.presentation.base.BasePresenter
import bonch.dev.poputi.presentation.modules.common.addbanking.view.AddBankCardView
import bonch.dev.poputi.presentation.modules.common.profile.ContractPresenter
import bonch.dev.poputi.presentation.modules.common.profile.ContractView
import bonch.dev.presentation.modules.common.profile.ProfileComponent
import javax.inject.Inject

class BankingPresenter : BasePresenter<ContractView.IBankingView>(),
    ContractPresenter.IBankingPresenter {


    @Inject
    lateinit var profileInteractor: IProfileInteractor

    private var listDeleteBankCard = arrayListOf<BankCard>()
    val ADD_BANK_CARD = 2


    init {
        ProfileComponent.profileComponent?.inject(this)
    }


    override fun getBankCards(): ArrayList<BankCard> {
        return profileInteractor.getBankCards()
    }


    override fun addCard(activity: Activity) {
        val intent = Intent(activity, AddBankCardView::class.java)
        activity.startActivityForResult(intent, ADD_BANK_CARD)
    }


    override fun addBankCardDone(data: Intent?) {
        val bankCard = data?.getParcelableExtra<BankCard>(ADD_BANK_CARD.toString())

        val paymentCard = BankCard(
            0,
            bankCard?.numberCard,
            bankCard?.validUntil,
            bankCard?.cvc,
            bankCard?.img
        )

        val adapter = getView()?.getAdapter()
        adapter?.list?.let {
            if (it.isNotEmpty()) {
                paymentCard.id = it.last().id + 1
            }
        }
        adapter?.list?.add(paymentCard)
        adapter?.notifyDataSetChanged()

        profileInteractor.saveBankCard(paymentCard)
    }


    override fun deleteBankCard(card: BankCard) {
        listDeleteBankCard.add(card)
    }


    override fun editMode() {
        listDeleteBankCard = arrayListOf()
        getView()?.getAdapter()?.editMode()
    }


    override fun doneEdit() {
        listDeleteBankCard.forEach {
            profileInteractor.deleteBankCard(it)
        }

        getView()?.getAdapter()?.doneEdit()
    }


    override fun instance() = this

}