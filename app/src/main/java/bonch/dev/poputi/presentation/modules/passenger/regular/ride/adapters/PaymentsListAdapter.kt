package bonch.dev.poputi.presentation.modules.passenger.regular.ride.adapters


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import bonch.dev.poputi.R
import bonch.dev.poputi.domain.entities.common.banking.BankCard
import bonch.dev.poputi.presentation.modules.passenger.regular.ride.presenter.ContractPresenter
import kotlinx.android.synthetic.main.bank_card_select_item.view.*
import javax.inject.Inject


class PaymentsListAdapter @Inject constructor(private val createRegularDrivePresenter: ContractPresenter.ICreateRegularDrivePresenter) :
    RecyclerView.Adapter<PaymentsListAdapter.ItemPostHolder>() {

    var list: ArrayList<BankCard> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemPostHolder {
        return ItemPostHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.bank_card_select_item, parent, false)
        )
    }


    override fun getItemCount() = list.size


    override fun onBindViewHolder(holder: ItemPostHolder, position: Int) {
        val bankCard = list[position]
        holder.bind(bankCard)

        holder.itemView.setOnClickListener {
            removeTickSelected()

            setTickSelected(holder.itemView)
            bankCard.isSelect = true
            createRegularDrivePresenter.setSelectedBankCard(bankCard)
        }

        if (bankCard.isSelect) {
            removeTickSelected()

            setTickSelected(holder.itemView)
        }
    }


    fun setBankCard(bankCard: BankCard) {
        list.forEachIndexed { i, card ->
            if (card.numberCard == bankCard.numberCard) {
                try {
                    list[i] = bankCard
                } catch (ex: IndexOutOfBoundsException) {

                }
            }
        }

        notifyDataSetChanged()
    }


    private fun setTickSelected(itemView: View) {
        val tick = itemView.findViewById<ImageView>(R.id.tick)
        tick.setImageResource(R.drawable.ic_tick_selected)
    }


    private fun removeTickSelected() {
        createRegularDrivePresenter.removeTickSelected()
        list.forEach {
            it.isSelect = false
        }
    }


    class ItemPostHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val VISA = 4
        private val MC = 5
        private val RUS_WORLD = 2

        fun bind(post: BankCard) {
            val hideChars = "•••• "
            var imgCard: Int? = null
            var numberCard = post.numberCard

            if (numberCard != null) {
                val firstDigit = numberCard.first().minus('0')

                try {
                    numberCard = when (firstDigit) {
                        VISA -> {
                            imgCard = R.drawable.ic_visa
                            hideChars + numberCard.substring(15, 19)
                        }

                        MC -> {
                            imgCard = R.drawable.ic_mastercard
                            hideChars + numberCard.substring(15, 19)
                        }

                        RUS_WORLD -> {
                            imgCard = R.drawable.ic_pay_world
                            hideChars + numberCard.substring(15, 19)
                        }

                        else -> {
                            imgCard = null
                            hideChars + numberCard.substring(15, 19)
                        }
                    }

                } catch (ex: Exception) {

                }

                imgCard?.let {
                    itemView.payment_method_img?.setImageResource(it)
                }

                itemView.number_card?.text = numberCard

                if (firstDigit == 'g'.minus('0')) {
                    itemView.payment_method_img?.setImageResource(R.drawable.ic_google_pay)
                    itemView.number_card?.text = ""
                }

            }
        }
    }
}

