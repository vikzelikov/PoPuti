package bonch.dev.poputi.presentation.modules.passenger.regular.ride.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import bonch.dev.poputi.App
import bonch.dev.poputi.R
import bonch.dev.poputi.domain.entities.common.ride.RideInfo
import bonch.dev.poputi.domain.entities.passenger.regular.ride.DateInfo
import bonch.dev.poputi.presentation.modules.passenger.regular.ride.presenter.ContractPresenter
import kotlinx.android.synthetic.main.regular_ride_item.view.*
import javax.inject.Inject

class ActiveRidesAdapter @Inject constructor(private val activeRidesPresenter: ContractPresenter.IActiveRidesPresenter) :
    RecyclerView.Adapter<ActiveRidesAdapter.ItemPostHolder>() {

    var list: ArrayList<RideInfo> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemPostHolder {
        return ItemPostHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.regular_ride_item, parent, false)
        )
    }


    override fun getItemCount(): Int = list.size


    fun setRide(index: Int, ride: RideInfo) {
        try {
            list.add(index, ride)
            notifyItemInserted(index)
            notifyItemChanged(index)
        } catch (ex: IndexOutOfBoundsException) {
        }
    }


    override fun onBindViewHolder(holder: ItemPostHolder, position: Int) {
        val ride = list[position]
        holder.bind(ride)


        holder.itemView.setOnClickListener {
//            activeRidesPresenter.onClickItem(list[position])
        }
    }


    class ItemPostHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(ride: RideInfo) {
            itemView.from.text = ride.position
            itemView.to.text = ride.destination
            itemView.price.text = ride.price.toString().plus(" ₽")
            itemView.date.text = getDays(ride.dateInfo)

            val time = ride.dateInfo?.time?.split(":")
            try {
                if (time != null)
                    itemView.time.text = time[0].plus(":").plus(time[1])
            } catch (ex: IndexOutOfBoundsException) {
            }
        }


        private fun getDays(dateInfo: DateInfo?): String {
            var days = ""

            val res = App.appComponent.getContext().resources
            if (dateInfo != null) {
                val arr = booleanArrayOf(
                    dateInfo.monday,
                    dateInfo.tuesday,
                    dateInfo.wednesday,
                    dateInfo.thursday,
                    dateInfo.friday,
                    dateInfo.saturday,
                    dateInfo.sunday
                )

                arr.forEachIndexed { i, day ->
                    if (i == 0 && day) days += (res.getString(R.string.mondayC)).plus(" ")
                    if (i == 1 && day) days += (res.getString(R.string.tuesdayC)).plus(" ")
                    if (i == 2 && day) days += (res.getString(R.string.wednesdayC)).plus(" ")
                    if (i == 3 && day) days += (res.getString(R.string.thursdayC)).plus(" ")
                    if (i == 4 && day) days += (res.getString(R.string.fridayC)).plus(" ")
                    if (i == 5 && day) days += (res.getString(R.string.saturdayC)).plus(" ")
                    if (i == 6 && day) days += (res.getString(R.string.sundayC)).plus(" ")
                }
            }

            return days
        }
    }
}