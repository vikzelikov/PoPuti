package bonch.dev.poputi.presentation.modules.common.profile.story.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import bonch.dev.poputi.R
import bonch.dev.poputi.domain.entities.common.ride.RideInfo
import bonch.dev.poputi.presentation.modules.common.profile.ContractPresenter
import kotlinx.android.synthetic.main.story_ride_item.view.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
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

            try {
                val time = ride.startAt
                if (time != null) {
                    val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale("ru"))
                    val parseDate = format.parse(time)
                    parseDate?.let {
                        val calendar = Calendar.getInstance(Locale("ru"))
                        calendar.time = it
                        val day = calendar.get(Calendar.DAY_OF_MONTH)
                        val mounth = calendar.getDisplayName(
                            Calendar.MONTH,
                            Calendar.LONG, Locale("ru")
                        )

                        itemView.date.text = "$day"
                            .plus(" $mounth")
                            .plus(", ${itemView.context.getString(R.string.in1)} ")
                            .plus("${calendar.get(Calendar.HOUR_OF_DAY)}:${calendar.get(Calendar.MINUTE)}")

                    }
                }
            } catch (e: ParseException) {
            }
        }
    }
}