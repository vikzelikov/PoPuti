package bonch.dev.poputi.presentation.modules.passenger.getdriver.adapters


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import bonch.dev.poputi.R
import bonch.dev.poputi.domain.entities.common.banking.BankCard
import bonch.dev.poputi.presentation.modules.passenger.getdriver.presenter.ContractPresenter
import javax.inject.Inject


class PaymentsAdapter @Inject constructor(private val detailRidePresenter: ContractPresenter.IDetailRidePresenter) :
    RecyclerView.Adapter<PaymentsAdapter.ItemPostHolder>() {

    var list: ArrayList<BankCard> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemPostHolder {
        return ItemPostHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.bank_card_select_item, parent, false)
        )
    }


    override fun getItemCount() = list.size


    override fun onBindViewHolder(holder: ItemPostHolder, position: Int) {
        val post = list[position]
        holder.bind(post)

        holder.itemView.setOnClickListener {
            removeTickSelected()

            setTickSelected(holder.itemView)
            post.isSelect = true
            detailRidePresenter.setSelectedBankCard(post)
        }

        if (post.isSelect) {
            removeTickSelected()

            setTickSelected(holder.itemView)

            detailRidePresenter.instance().getView()?.setSelectedBankCard(post)
        }
    }


    private fun setTickSelected(itemView: View) {
        val tick = itemView.findViewById<ImageView>(R.id.tick)
        tick.setImageResource(R.drawable.ic_tick_selected)
    }


    private fun removeTickSelected() {
        detailRidePresenter.removeTickSelected()
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

            if (img == R.drawable.ic_google_pay) {
                paymentImg.setImageResource(img)
                numberCard.text = ""
            }
        }
    }
}

