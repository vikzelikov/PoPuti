package bonch.dev.presentation.modules.driver.getpassenger.adapters

import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import bonch.dev.App
import bonch.dev.R
import bonch.dev.domain.entities.common.ride.RideInfo
import bonch.dev.presentation.modules.driver.getpassenger.presenter.ContractPresenter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.order_item.view.*
import javax.inject.Inject

class OrdersAdapter @Inject constructor(private val ordersPresenter: ContractPresenter.IOrdersPresenter) :
    RecyclerView.Adapter<OrdersAdapter.ItemPostHolder>() {

    var list = arrayListOf<RideInfo>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ItemPostHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.order_item, parent, false)
    )


    override fun getItemCount() = list.size


    override fun onBindViewHolder(holder: ItemPostHolder, position: Int) {
        holder.bind(list[position])
    }


    fun setNewOrder(order: RideInfo) {
        list.add(0, order)
        notifyItemInserted(0)
        notifyItemChanged(0)
    }


    fun setNewOrder(index: Int, order: RideInfo) {
        list.add(index, order)
        notifyItemInserted(index)
        notifyItemChanged(index)
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


    inner class ItemPostHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(post: RideInfo) {
            itemView.passanger_name.text = post.passenger?.firstName
            itemView.from_order.text = post.position
            itemView.to_order.text = post.destination
            itemView.order_price.text = post.price.toString().plus(" ₽")

            val rating = post.passenger?.rating?.toString()
            if (rating != null) itemView.passanger_rating.text = rating
            else itemView.passanger_rating.text = "0.0"

            var photo: Any? = post.passenger?.photos?.lastOrNull()?.imgUrl
            if (photo == null) {
                photo = R.drawable.ic_default_ava
            }
            Glide.with(itemView.context).load(photo)
                .apply(RequestOptions().centerCrop().circleCrop())
                .into(itemView.img_passanger)

            showDistance(post)

            itemView.setOnClickListener {
                ordersPresenter.onClickItem(list[adapterPosition])
            }
        }


        private fun showDistance(post: RideInfo) {
            val d = post.distance
            if (d != null) {
                itemView.user_distance.text = formatDistance(d)
            } else {
                //hide in case error
                itemView.user_distance.visibility = View.GONE
            }
        }


        private fun formatDistance(dist: Int): String {
            val res = App.appComponent.getContext().resources

            val dDist = (dist.toDouble() / 1000)

            return if (dist > 1000) {
                String.format("%.2f", dDist).plus(" ${res.getString(R.string.km)}")
            } else {
                (dist).toString().plus(" ${res.getString(R.string.metr)}")
            }
        }
    }
}
