package bonch.dev.view.getdriver


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import bonch.dev.R
import bonch.dev.model.getdriver.pojo.PaymentCard


class PaymentsListAdapter(
    val detailRideView: DetailRideView,
    var list: ArrayList<PaymentCard>,
    val context: Context,
    val root: View
) : RecyclerView.Adapter<PaymentsListAdapter.ItemPostHolder>() {

    private var imm: InputMethodManager? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemPostHolder {

        imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

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

        }


        holder.itemView.setOnTouchListener { v, event ->
            imm!!.hideSoftInputFromWindow(root.windowToken, 0)

            false
        }

    }


    class ItemPostHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val numberCard = itemView.findViewById<TextView>(R.id.number_card)
        fun bind(post: PaymentCard) {
            numberCard.text = post.numberCard
        }

    }


}

