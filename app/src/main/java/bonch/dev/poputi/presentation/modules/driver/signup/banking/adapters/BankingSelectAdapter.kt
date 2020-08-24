package bonch.dev.poputi.presentation.modules.driver.signup.banking.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import bonch.dev.poputi.R
import bonch.dev.poputi.domain.entities.common.banking.BankCard
import bonch.dev.poputi.presentation.modules.driver.signup.banking.presenter.IBankingSelectPresenter
import javax.inject.Inject

class BankingSelectAdapter @Inject constructor(private val presenter: IBankingSelectPresenter) :
    RecyclerView.Adapter<BankingSelectAdapter.ItemPostHolder>() {

    var list: ArrayList<BankCard> = arrayListOf()


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemPostHolder {
        return ItemPostHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.bank_card_select_item, parent, false)
        )
    }


    override fun getItemCount() = list.size


    override fun onBindViewHolder(holder: ItemPostHolder, position: Int) {
        val card = list[position]
        holder.bind(card)

        holder.itemView.setOnClickListener {
            removeTickSelected()

            setTickSelected(holder.itemView)
            card.isSelect = true
            presenter.selectBankCard(card)
        }
    }


    private fun setTickSelected(itemView: View) {
        val tick = itemView.findViewById<ImageView>(R.id.tick)
        tick.setImageResource(R.drawable.ic_tick_selected)
    }


    private fun removeTickSelected() {
        presenter.removeTickSelected()
        list.forEach {
            it.isSelect = false
        }
    }


    class ItemPostHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val numberCard = itemView.findViewById<TextView>(R.id.number_card)
        private val paymentImg = itemView.findViewById<ImageView>(R.id.payment_method_img)

        fun bind(post: BankCard) {
            val img = post.img

            if (img == R.drawable.ic_visa || img == R.drawable.ic_mastercard || img == R.drawable.ic_pay_world) {
                paymentImg.setImageResource(img)
                numberCard.text = post.numberCard
            }
        }
    }
}