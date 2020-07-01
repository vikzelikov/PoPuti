package bonch.dev.presentation.modules.passenger.getdriver.adapters


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import bonch.dev.R
import bonch.dev.domain.entities.common.ride.Offer
import bonch.dev.presentation.modules.passenger.getdriver.presenter.ContractPresenter
import bonch.dev.presentation.modules.passenger.getdriver.presenter.OfferItemTimer
import bonch.dev.presentation.modules.passenger.getdriver.presenter.OffersMainTimer
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.driver_item.view.*
import javax.inject.Inject


class OffersAdapter @Inject constructor(val getDriverPresenter: ContractPresenter.IGetDriverPresenter) :
    RecyclerView.Adapter<OffersAdapter.ItemPostHolder>() {

    var list: ArrayList<Offer> = arrayListOf()

    init {
        if (OffersMainTimer.getInstance() == null)
            OffersMainTimer.getInstance(this)?.start()
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
            if (OffersMainTimer.DEFAULT_WIDTH == 0) {
                OffersMainTimer.DEFAULT_WIDTH = timeLine.measuredWidth - 50
            }

            if (OffersMainTimer.ratio == 100) {
                //calc ration
                while ((OffersMainTimer.DEFAULT_WIDTH / (OffersMainTimer.TIME_EXPIRED_ITEM.toInt() * OffersMainTimer.ratio)) == 0) {
                    OffersMainTimer.ratio--
                }
            }

            holder.offerItemTimer?.cancel()
            holder.offerItemTimer = OfferItemTimer(startTime.toLong() * 1000, 10, timeLine)
            holder.offerItemTimer?.start()

        })


        holder.itemView.accept_driver.setOnClickListener {
            getDriverPresenter.setOffer(offer)
            getDriverPresenter.instance().getView()?.getConfirmAccept(offer)
        }
    }


    fun setNewOffer(offer: Offer) {
        list.add(0, offer)
        notifyItemInserted(0)
        notifyItemChanged(0)

        if (list.isNotEmpty()) {
            getDriverPresenter.instance().getView()?.checkoutBackground(false)
        }
    }


    fun deleteOffer(offerId: Int) {
        var offer: Offer? = null
        list.forEach {
            offer = if (offerId == it.offerId) it
            else null
        }

        offer?.let {
            val position = list.indexOf(it)
            rejectOffer(position, false)
        }
    }


    fun rejectOffer(position: Int?, isUserAction: Boolean) {
        val recyclerView = getDriverPresenter.instance().getView()?.getRecyclerView()

        recyclerView?.post {
            try {
                if (isUserAction && position != null) {
                    list[position].offerId?.let {
                        //delete offer with SERVER
                        getDriverPresenter.deleteOffer(it)
                    }

                    //delete offer on view
                    list.removeAt(position)
                    notifyItemRemoved(position)
                }
            } catch (ex: IndexOutOfBoundsException) {
            }

            try {
                if (!isUserAction) {
                    val offer = getDriverPresenter.getOffer()
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
        var offerItemTimer: OfferItemTimer? = null

        fun bind(offer: Offer) {
            itemView.driver_name.text = offer.driver?.firstName
            itemView.car_name.text =
                offer.driver?.car?.name?.plus(" ")?.plus(offer.driver?.car?.model)
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
                offerItemTimer?.cancel()
                rejectOffer(adapterPosition, true)
            }
        }
    }
}
