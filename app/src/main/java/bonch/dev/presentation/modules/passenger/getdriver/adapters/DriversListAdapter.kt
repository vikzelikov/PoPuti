package bonch.dev.presentation.modules.passenger.getdriver.adapters


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import bonch.dev.R
import bonch.dev.domain.entities.common.ride.Offer
import bonch.dev.presentation.modules.passenger.getdriver.presenter.ContractPresenter
import bonch.dev.presentation.modules.passenger.getdriver.presenter.DriverItemTimer
import bonch.dev.presentation.modules.passenger.getdriver.presenter.DriverMainTimer
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.driver_item.view.*
import javax.inject.Inject


class DriversListAdapter @Inject constructor(val getDriverPresenter: ContractPresenter.IGetDriverPresenter) :
    RecyclerView.Adapter<DriversListAdapter.ItemPostHolder>() {

    var list: ArrayList<Offer> = arrayListOf()

    init {
        DriverMainTimer.getInstance(this)?.start()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemPostHolder {
        return ItemPostHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.driver_item, parent, false)
        )
    }


    override fun getItemCount(): Int = list.size


    override fun onBindViewHolder(holder: ItemPostHolder, position: Int) {
        val offer = list[position]
        val timeLine = holder.itemView.timer_driver_offer
        val startTime = offer.timeLine

        holder.bind(offer)

        timeLine.post({
            if (DriverMainTimer.DEFAULT_WIDTH == 0) {
                DriverMainTimer.DEFAULT_WIDTH = timeLine.measuredWidth
            }

            if (DriverMainTimer.ratio == 100) {
                //calc ration
                while ((DriverMainTimer.DEFAULT_WIDTH / (DriverMainTimer.TIME_EXPIRED_ITEM.toInt() * DriverMainTimer.ratio)) == 0) {
                    DriverMainTimer.ratio--
                }
            }

            holder.driverItemTimer?.cancel()
            holder.driverItemTimer = DriverItemTimer(startTime.toLong() * 1000, 10, timeLine)
            holder.driverItemTimer?.start()

        })


        holder.itemView.accept_driver.setOnClickListener {
            getDriverPresenter.instance().offer = offer
            getDriverPresenter.instance().getView()?.getConfirmAccept(offer)
        }
    }


    fun setNewDriver(offer: Offer) {
        list.add(0, offer)
        notifyItemInserted(0)
        notifyItemChanged(0)

        if (list.isNotEmpty()) {
            getDriverPresenter.instance().getView()?.checkoutBackground(false)
        }
    }


    fun rejectDriver(position: Int?, isUserAction: Boolean) {
        val recyclerView = getDriverPresenter.instance().getView()?.getRecyclerView()

        recyclerView?.post {
            try {
                if (isUserAction && position != null) {
                    list.removeAt(position)
                    notifyItemRemoved(position)
                }
            } catch (ex: IndexOutOfBoundsException) {
            }

            try {
                if (!isUserAction) {
                    val offer = getDriverPresenter.instance().offer
                    if (offer?.offerId == list[list.lastIndex].offerId) {
                        getDriverPresenter.instance().getView()?.hideConfirmAccept()
                    }

                    list.removeAt(list.lastIndex)
                    notifyItemRemoved(list.size)
                }
            } catch (ex: IndexOutOfBoundsException) {
            }

            if (list.isEmpty()) {
                getDriverPresenter.instance().getView()?.checkoutBackground(true)
            }
        }
    }


    inner class ItemPostHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var driverItemTimer: DriverItemTimer? = null

        fun bind(offer: Offer) {
            itemView.driver_name.text = offer.driver?.firstName
            itemView.car_name.text = offer.driver?.car?.name
            itemView.price.text = offer.price.toString().plus(" ₽")

            itemView.driver_rating.text = if (offer.driver?.rating == null) {
                "0.0"
            } else {
                offer.driver?.rating.toString()
            }

            offer.driver?.photos?.sortBy { it.id }
            var photo: Any? = offer.driver?.photos?.lastOrNull()?.imgUrl
            if (photo == null) {
                photo = R.drawable.ic_default_ava
            }
            Glide.with(itemView.context).load(photo)
                .apply(RequestOptions().centerCrop().circleCrop())
                .error(R.drawable.ic_default_ava)
                .into(itemView.img_driver)

            itemView.reject_driver.setOnClickListener {
                driverItemTimer?.cancel()
                rejectDriver(adapterPosition, true)
            }
        }
    }
}
