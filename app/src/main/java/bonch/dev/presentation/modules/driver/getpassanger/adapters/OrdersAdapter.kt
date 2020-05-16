package bonch.dev.presentation.modules.driver.getpassanger.adapters

import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import bonch.dev.R
import bonch.dev.domain.entities.driver.getpassanger.Order
import bonch.dev.presentation.modules.driver.getpassanger.presenter.ContractPresenter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.order_item.view.*
import javax.inject.Inject

class OrdersAdapter @Inject constructor(private val ordersPresenter: ContractPresenter.IOrdersPresenter) :
    RecyclerView.Adapter<OrdersAdapter.ItemPostHolder>() {

    var list: ArrayList<Order> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ItemPostHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.order_item, parent, false)
    )


    override fun getItemCount() = list.size


    override fun onBindViewHolder(holder: ItemPostHolder, position: Int) {
        holder.bind(list[position])

        holder.itemView.setOnClickListener {
            ordersPresenter.onClickItem(list[position])
        }
    }


    fun setNewOrder(order: Order) {
        list.add(0, order)
        notifyItemInserted(0)
        notifyItemChanged(0)
    }


    fun cancel() {
        val mainHandler = Handler(Looper.getMainLooper())
        val myRunnable = Runnable {
            kotlin.run {
                try {
                    list.removeAt(list.size - 1)
                    notifyItemRemoved(list.size)
                } catch (ex: IndexOutOfBoundsException) {
                }

            }
        }

        mainHandler.post(myRunnable)
    }


    class ItemPostHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(post: Order) {
            itemView.passanger_name.text = post.name
            itemView.passanger_rating.text = post.rating.toString()
            itemView.user_distance.text = post.userDistance.toString().plus(" м")
            itemView.from_order.text = post.from
            itemView.to_order.text = post.to
            itemView.order_price.text = post.price.toString().plus(" ₽")

            Glide.with(itemView.context).load(post.img)
                .apply(RequestOptions().centerCrop().circleCrop())
                .into(itemView.img_passanger)
        }
    }
}
