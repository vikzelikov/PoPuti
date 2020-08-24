package bonch.dev.poputi.presentation.modules.common.profile.banking

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.recyclerview.widget.LinearLayoutManager
import bonch.dev.poputi.R
import bonch.dev.poputi.presentation.modules.common.profile.ContractPresenter
import bonch.dev.poputi.presentation.modules.common.profile.ContractView
import bonch.dev.presentation.modules.common.profile.ProfileComponent
import kotlinx.android.synthetic.main.select_bank_card_activity.*
import javax.inject.Inject

class BankingView : AppCompatActivity(), ContractView.IBankingView {


    @Inject
    lateinit var bankingPresenter: ContractPresenter.IBankingPresenter


    @Inject
    lateinit var bankingAdapter: BankingAdapter


    init {
        ProfileComponent.profileComponent?.inject(this)

        bankingPresenter.instance().attachView(this)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.select_bank_card_activity)

        setListeners()

        initAdapter()
    }


    override fun setListeners() {
        edit.setOnClickListener {
            bankingPresenter.editMode()
            edit.visibility = View.GONE
            doneEdit.visibility = View.VISIBLE
        }

        doneEdit.setOnClickListener {
            bankingPresenter.doneEdit()
            doneEdit.visibility = View.GONE
            edit.visibility = View.VISIBLE

            if (bankingAdapter.list.isEmpty()) {
                edit?.visibility = View.INVISIBLE
                text_empty_cards?.visibility = View.VISIBLE
            }
        }

        add_card.setOnClickListener {
            bankingPresenter.addCard(this)
        }

        back_btn.setOnClickListener {
            finish()
        }

        back_text.setOnClickListener {
            finish()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == bankingPresenter.instance().ADD_BANK_CARD && resultCode == Activity.RESULT_OK) {
            bankingPresenter.addBankCardDone(data)
            payments_list?.visibility = View.VISIBLE
            edit?.visibility = View.VISIBLE
            text_empty_cards?.visibility = View.GONE
        }
    }


    private fun initAdapter() {
        payments_list?.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = bankingAdapter
        }

        val cards = bankingPresenter.getBankCards()
        if (cards.isNotEmpty()) {
            cards.sortBy {
                it.id
            }

            bankingAdapter.list = cards
            bankingAdapter.notifyDataSetChanged()

            payments_list?.visibility = View.VISIBLE
        } else {
            edit?.visibility = View.INVISIBLE
            text_empty_cards?.visibility = View.VISIBLE
        }
    }


    override fun getAdapter() = bankingAdapter


    override fun getNavHost(): NavController? = null


    override fun hideKeyboard() {}


    override fun showNotification(text: String) {}


    override fun showLoading() {}


    override fun hideLoading() {}

}