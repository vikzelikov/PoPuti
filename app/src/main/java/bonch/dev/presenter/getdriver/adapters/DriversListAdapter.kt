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
import bonch.dev.model.getdriver.pojo.Driver
import bonch.dev.model.getdriver.pojo.PaymentCard
import bonch.dev.view.getdriver.DetailRideView
import bonch.dev.view.getdriver.GetDriverView


class DriversListAdapter(
    var list: ArrayList<Driver>,
    val context: Context,
    val getDriverView: GetDriverView
) : RecyclerView.Adapter<DriversListAdapter.ItemPostHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemPostHolder {
        return ItemPostHolder(
            LayoutInflater.from(context)
                .inflate(R.layout.driver_item, parent, false)
        )
    }


    override fun getItemCount(): Int {
        return list.size
    }


    override fun onBindViewHolder(holder: ItemPostHolder, position: Int) {
        val post = list[position]
        holder.bind(post)

        holder.itemView.setOnClickListener {
            //dynamic replace
        }
    }


    class ItemPostHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val numberCard = itemView.findViewById<TextView>(R.id.number_card)
        private val paymentImg = itemView.findViewById<ImageView>(R.id.payment_method_img)

        fun bind(post: Driver) {
//            numberCard.text = post.numberCard
//
//            if (post.img != null) {
//                val img = post.img!!
//
//                if (img == R.drawable.ic_visa || img == R.drawable.ic_mastercard || img == R.drawable.ic_pay_world || img == R.drawable.ic_google_pay) {
//                    paymentImg.setImageResource(post.img!!)
//                }
//            }
        }

    }


}

