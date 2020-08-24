package bonch.dev.poputi.presentation.modules.common.profile.banking

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import bonch.dev.poputi.R
import bonch.dev.poputi.domain.entities.common.banking.BankCard
import bonch.dev.poputi.presentation.modules.common.profile.ContractPresenter
import kotlinx.android.synthetic.main.bank_card_item.view.*
import javax.inject.Inject

class BankingAdapter @Inject constructor(private val bankingPresenter: ContractPresenter.IBankingPresenter) :
    RecyclerView.Adapter<BankingAdapter.ItemPostHolder>() {

    var list: ArrayList<BankCard> = arrayListOf()

    private var isEditMode = false

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemPostHolder {
        return ItemPostHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.bank_card_item, parent, false)
        )
    }


    override fun getItemCount() = list.size


    override fun onBindViewHolder(holder: ItemPostHolder, position: Int) {
        val card = list[position]
        holder.bind(card)

        val trash = holder.itemView.trash

        if (isEditMode && card.id != 0) {
            trash?.visibility = View.VISIBLE
            trash?.isClickable = true

            setAnimation(trash)

        } else {
            trash?.visibility = View.INVISIBLE
            trash?.isClickable = false
        }

        trash.setOnClickListener {
            bankingPresenter.deleteBankCard(card)

            val pos = list.indexOf(card)

            try {
                list.removeAt(pos)
                notifyItemRemoved(pos)

            } catch (ex: IndexOutOfBoundsException) {
            }
        }
    }


    fun editMode() {
        isEditMode = true
        notifyDataSetChanged()
    }


    fun doneEdit() {
        isEditMode = false
        notifyDataSetChanged()
    }


    private fun setAnimation(viewToAnimate: View) {
        val animation = AnimationUtils.loadAnimation(viewToAnimate.context, R.anim.slide_in_right)
        viewToAnimate.startAnimation(animation)
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