package bonch.dev.poputi.presentation.modules.common.profile.passenger.story.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import bonch.dev.poputi.R
import bonch.dev.poputi.domain.entities.common.ride.RideInfo
import bonch.dev.poputi.presentation.modules.common.profile.ContractPresenter
import kotlinx.android.synthetic.main.story_ride_item.view.*
import javax.inject.Inject

class StoryAdapter @Inject constructor(private val presenter: ContractPresenter.IStoryPresenter) :
    RecyclerView.Adapter<StoryAdapter.ItemPostHolder>() {


    var list: ArrayList<RideInfo> = arrayListOf()


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemPostHolder {
        return ItemPostHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.story_ride_item, parent, false)
        )
    }


    override fun getItemCount() = list.size


    override fun onBindViewHolder(holder: ItemPostHolder, position: Int) {
        val ride = list[position]
        holder.bind(ride)

        holder.itemView.setOnClickListener {
            presenter.onClickItem(ride)
        }
    }


    inner class ItemPostHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(ride: RideInfo) {
            itemView.from.text = ride.position
            itemView.to.text = ride.destination
            itemView.price.text = ride.price?.toString()?.plus(" ₽")
            itemView.date.text = "12 мая, в 19:01"
        }
    }
}