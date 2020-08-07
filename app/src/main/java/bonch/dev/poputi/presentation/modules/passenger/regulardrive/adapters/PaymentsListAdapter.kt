package bonch.dev.poputi.presentation.modules.passenger.regulardrive.adapters


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import bonch.dev.poputi.R
import bonch.dev.poputi.domain.entities.common.banking.BankCard
import bonch.dev.poputi.presentation.modules.passenger.regulardrive.presenter.ContractPresenter
import javax.inject.Inject


class PaymentsListAdapter @Inject constructor(private val createRegularDrivePresenter: ContractPresenter.ICreateRegularDrivePresenter) :
    RecyclerView.Adapter<PaymentsListAdapter.ItemPostHolder>() {

    var list: ArrayList<BankCard> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemPostHolder {
        return ItemPostHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.payment_item, parent, false)
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
            createRegularDrivePresenter.setSelectedBankCard(post)
        }

        if (position == 0) {
            val lineDialog: View = holder.itemView.findViewById(R.id.line_payment)
            val params: LinearLayout.LayoutParams =
                LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1)
            params.setMargins(30, 0, 30, 0)
            lineDialog.layoutParams = params
        }
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
        private val numberCard = itemView.findViewById<TextView>(R.id.number_card)
        private val paymentImg = itemView.findViewById<ImageView>(R.id.payment_method_img)

        fun bind(post: BankCard) {
            numberCard.text = post.numberCard

            if (post.img != null) {
                val img = post.img!!

                if (img == R.drawable.ic_visa || img == R.drawable.ic_mastercard || img == R.drawable.ic_pay_world || img == R.drawable.ic_google_pay) {
                    paymentImg.setImageResource(post.img!!)
                }
            }
        }
    }
}

