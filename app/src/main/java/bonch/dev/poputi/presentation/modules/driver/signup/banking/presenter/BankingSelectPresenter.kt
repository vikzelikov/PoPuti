package bonch.dev.poputi.presentation.modules.driver.signup.banking.presenter

import android.content.Intent
import androidx.fragment.app.Fragment
import bonch.dev.poputi.R
import bonch.dev.poputi.domain.entities.common.banking.BankCard
import bonch.dev.poputi.domain.interactor.driver.signup.ISignupInteractor
import bonch.dev.poputi.presentation.base.BasePresenter
import bonch.dev.poputi.presentation.modules.common.addbanking.view.AddBankCardView
import bonch.dev.poputi.presentation.modules.driver.signup.SignupComponent
import bonch.dev.poputi.presentation.modules.driver.signup.banking.view.IBankingSelectView
import bonch.dev.poputi.route.MainRouter
import javax.inject.Inject

class BankingSelectPresenter : BasePresenter<IBankingSelectView>(), IBankingSelectPresenter {

    @Inject
    lateinit var signupInteractor: ISignupInteractor

    val ADD_BANK_CARD = 2

    private var selectBankCard: BankCard? = null

    init {
        SignupComponent.driverSignupComponent?.inject(this)
    }


    override fun getBankCards(): ArrayList<BankCard> {
        return signupInteractor.getBankCards()
    }


    override fun addCard(fragment: Fragment) {
        val intent = Intent(fragment.context, AddBankCardView::class.java)
        fragment.startActivityForResult(intent, ADD_BANK_CARD)
    }


    override fun next() {
        selectBankCard?.let {
            //todo save card or send to server
            MainRouter.showView(R.id.show_signup_step_view, getView()?.getNavHost(), null)
        }
    }


    override fun selectBankCard(card: BankCard) {
        selectBankCard = card

        getView()?.showNextBtn()
    }


    override fun addBankCardDone(data: Intent?) {
        val bankCard = data?.getParcelableExtra<BankCard>(ADD_BANK_CARD.toString())

        val paymentCard = BankCard(
            1,
            bankCard?.numberCard,
            bankCard?.validUntil,
            bankCard?.cvc
        )

        val adapter = getView()?.getAdapter()
        adapter?.list?.let {
            if (it.isNotEmpty()) {
                paymentCard.id = it.last().id + 1
            }
        }
        adapter?.list?.add(paymentCard)
        adapter?.notifyDataSetChanged()

        signupInteractor.saveBankCard(paymentCard)
    }


    override fun removeTickSelected() {
        getView()?.removeTickSelected()
    }


    override fun instance() = this

}