package bonch.dev.presenter.getdriver.adapters


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import bonch.dev.R
import bonch.dev.model.getdriver.pojo.PaymentCard
import bonch.dev.view.getdriver.DetailRideView


class PaymentsListAdapter(
    val recyclerView: RecyclerView,
    var list: ArrayList<PaymentCard>,
    val context: Context,
    val detailRideView: DetailRideView
) : RecyclerView.Adapter<PaymentsListAdapter.ItemPostHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemPostHolder {
        return ItemPostHolder(
            LayoutInflater.from(context)
                .inflate(R.layout.payment_item, parent, false)
        )
    }


    override fun getItemCount(): Int {
        return list.size
    }


    override fun onBindViewHolder(holder: ItemPostHolder, position: Int) {
        val post = list[position]
        holder.bind(post)

        holder.itemView.setOnClickListener {
            removeTickSelected()

            setTickSelected(holder.itemView)
            post.isSelect = true
            detailRideView.setSelectedBankCard(post)
        }

        if (position == 0) {
            val lineDialog: View = holder.itemView.findViewById<View>(R.id.line_payment)
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
        val childCount = recyclerView.childCount

        for (i in 0 until childCount) {
            val holder = recyclerView.getChildViewHolder(recyclerView.getChildAt(i))
            val tick = holder.itemView.findViewById<ImageView>(R.id.tick)
            tick.setImageResource(R.drawable.ic_tick)
            list[i].isSelect = false
        }
    }


    class ItemPostHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val numberCard = itemView.findViewById<TextView>(R.id.number_card)
        private val paymentImg = itemView.findViewById<ImageView>(R.id.payment_method_img)

        fun bind(post: PaymentCard) {
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

