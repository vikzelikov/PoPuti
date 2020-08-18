package bonch.dev.poputi.presentation.modules.passenger.regular.ride.adapters

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
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class ArchiveRidesAdapter @Inject constructor(private val archiveRidesPresenter: ContractPresenter.IArchiveRidesPresenter) :
    RecyclerView.Adapter<ArchiveRidesAdapter.ItemPostHolder>() {

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


    fun removeRide(rideId: Int) {
        var ride: RideInfo? = null
        list.forEach {
            if (rideId == it.rideId) ride = it
        }

        ride?.let {
            val position = list.indexOf(it)

            try {
                list.removeAt(position)
                notifyItemRemoved(position)

            } catch (ex: IndexOutOfBoundsException) {
            }
        }
    }


    override fun onBindViewHolder(holder: ItemPostHolder, position: Int) {
        val ride = list[position]
        holder.bind(ride)


        holder.itemView.setOnClickListener {
            archiveRidesPresenter.onClickItem(ride)
        }
    }


    class ItemPostHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(ride: RideInfo) {
            itemView.from.text = ride.position
            itemView.to.text = ride.destination
            itemView.price.text = ride.price.toString().plus(" ₽")
            itemView.date.text = getDays(ride.dateInfo)

            val time = ride.dateInfo?.time
            itemView.time.text = parseTime(time)
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

        private fun parseTime(time: String?): String? {
            var resultTime: String? = null
            if (time != null) {
                if (time.length > 8) {
                    val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

                    try {
                        val date = format.parse(time)
                        if (date != null) {
                            val calendar = GregorianCalendar.getInstance()
                            calendar.time = date

                            resultTime = calendar[Calendar.HOUR_OF_DAY].toString()
                                .plus(":")
                                .plus(calendar[Calendar.MINUTE])

                        }
                    } catch (e: Exception) {
                    }
                } else {
                    try {
                        val splitTime = time.split(":")
                        resultTime = splitTime[0].plus(":").plus(splitTime[1])
                    } catch (ex: IndexOutOfBoundsException) {
                    }
                }
            }

            return resultTime
        }
    }
}