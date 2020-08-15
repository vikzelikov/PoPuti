package bonch.dev.poputi.presentation.modules.driver.getpassenger.adapters

import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import bonch.dev.poputi.App
import bonch.dev.poputi.R
import bonch.dev.poputi.domain.entities.common.ride.RideInfo
import bonch.dev.poputi.presentation.modules.driver.getpassenger.presenter.ContractPresenter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.yandex.mapkit.geometry.Point
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


    fun setNewOrder(index: Int, order: RideInfo) {
        order.isNewOrder = false

        try {
            list.add(index, order)
            notifyItemInserted(index)
            notifyItemChanged(index)
        } catch (ex: java.lang.IndexOutOfBoundsException) {
        }
    }


    fun setNewOrders(orders: ArrayList<RideInfo>) {
        var delay = 500L

        orders.forEachIndexed { i, order ->
            order.isNewOrder = false
            Handler().postDelayed({
                setNewOrder(i, order)
            }, delay)

            delay += 300
        }
    }


    fun cancel() {
        val mainHandler = Handler(Looper.getMainLooper())
        val myRunnable = Runnable {
            kotlin.run {
                try {
                    list.removeAt(list.lastIndex)
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

            //todo TEST
            post.passenger?.photos?.sortBy { it.id }
            var photo: Any? = post.passenger?.photos?.lastOrNull()?.imgUrl
            if (photo == null) photo = R.drawable.ic_default_ava
            Glide.with(itemView.context).load(photo)
                .apply(RequestOptions().centerCrop().circleCrop())
                .error(R.drawable.ic_default_ava)
                .into(itemView.img_passanger)

            showDistance(post)

            updateDistance(post)

            itemView.setOnClickListener {
                ordersPresenter.selectOrder(list[adapterPosition])
            }
        }


        private fun updateDistance(post: RideInfo) {
            ordersPresenter.instance().userPosition?.let {
                val fromLat = post.fromLat
                val fromLng = post.fromLng
                if (fromLat != null && fromLng != null) {
                    val point = Point(fromLat, fromLng)
                    //save distance
                    val distance = ordersPresenter.calcDistance(it, point)
                    post.distance = distance

                    showDistance(post)
                }
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
