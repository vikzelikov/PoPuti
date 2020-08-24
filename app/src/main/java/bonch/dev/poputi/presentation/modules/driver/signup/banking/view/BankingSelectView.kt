package bonch.dev.poputi.presentation.modules.driver.signup.banking.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.recyclerview.widget.LinearLayoutManager
import bonch.dev.poputi.R
import bonch.dev.poputi.presentation.modules.driver.signup.DriverSignupActivity
import bonch.dev.poputi.presentation.modules.driver.signup.SignupComponent
import bonch.dev.poputi.presentation.modules.driver.signup.banking.adapters.BankingSelectAdapter
import bonch.dev.poputi.presentation.modules.driver.signup.banking.presenter.IBankingSelectPresenter
import kotlinx.android.synthetic.main.driver_signup_add_bank.*
import kotlinx.android.synthetic.main.select_bank_card_activity.payments_list
import java.lang.IndexOutOfBoundsException
import javax.inject.Inject

class BankingSelectView : Fragment(), IBankingSelectView {

    @Inject
    lateinit var presenter: IBankingSelectPresenter


    @Inject
    lateinit var bankingAdapter: BankingSelectAdapter


    init {
        SignupComponent.driverSignupComponent?.inject(this)

        presenter.instance().attachView(this)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.driver_signup_add_bank, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setListeners()

        initAdapter()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == presenter.instance().ADD_BANK_CARD && resultCode == Activity.RESULT_OK) {
            presenter.addBankCardDone(data)
            payments_list?.visibility = View.VISIBLE
        }
    }


    private fun initAdapter() {
        payments_list?.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = bankingAdapter
        }

        val cards = presenter.getBankCards()

        var indexGooglePay = -1
        cards.forEachIndexed { i, card ->
            if (card.id == 0) {
                indexGooglePay = i
            }
        }
        try {
            cards.removeAt(indexGooglePay)
        } catch (ex: IndexOutOfBoundsException) {
        }


        if (cards.isNotEmpty()) {
            cards.sortBy {
                it.id
            }

            bankingAdapter.list = cards
            bankingAdapter.notifyDataSetChanged()

            payments_list?.visibility = View.VISIBLE
        }
    }


    override fun setListeners() {
        next_btn.setOnClickListener {
            presenter.next()
        }

        add_card.setOnClickListener {
            presenter.addCard(this)
        }
    }


    override fun getAdapter() = bankingAdapter


    override fun getNavHost(): NavController? {
        return (activity as? DriverSignupActivity)?.navController
    }


    override fun removeTickSelected() {
        val childCount = payments_list?.childCount

        if (childCount != null) {
            for (i in 0 until childCount) {
                val holder = payments_list.getChildViewHolder(payments_list.getChildAt(i))
                val tick = holder.itemView.findViewById<ImageView>(R.id.tick)
                tick.setImageResource(R.drawable.ic_tick)
            }
        }
    }


    override fun showNextBtn() {
        next_btn?.visibility = View.VISIBLE
    }


    override fun hideKeyboard() {}


    override fun showNotification(text: String) {}


    override fun showLoading() {}


    override fun hideLoading() {}

}