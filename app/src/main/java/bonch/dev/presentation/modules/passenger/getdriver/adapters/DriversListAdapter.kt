package bonch.dev.presentation.modules.passenger.getdriver.adapters


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import bonch.dev.R
import bonch.dev.domain.entities.passenger.getdriver.Driver
import bonch.dev.domain.entities.passenger.getdriver.DriverObject
import bonch.dev.presentation.modules.passenger.getdriver.presenter.ContractPresenter
import bonch.dev.presentation.modules.passenger.getdriver.presenter.DriverItemTimer
import bonch.dev.presentation.modules.passenger.getdriver.presenter.DriverMainTimer
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.driver_item.view.*
import javax.inject.Inject


class DriversListAdapter @Inject constructor(val getDriverPresenter: ContractPresenter.IGetDriverPresenter) :
    RecyclerView.Adapter<DriversListAdapter.ItemPostHolder>() {

    var list: ArrayList<Driver> = arrayListOf()

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
        val driver = list[position]
        val timeLine = holder.itemView.timer_driver_offer
        val startTime = driver.timeLine

        holder.bind(driver)

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
            DriverObject.driver = driver
            getDriverPresenter.instance().getView()?.getConfirmAccept()
        }
    }


    fun setNewDriver(driver: Driver) {
        list.add(0, driver)
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
            } catch (ex: java.lang.IndexOutOfBoundsException) {
                println(ex.message)
            }

            try {
                if (!isUserAction) {
                    if (DriverObject.driver == list[list.lastIndex]) {
                        getDriverPresenter.instance().getView()?.hideConfirmAccept()
                    }

                    list.removeAt(list.lastIndex)
                    notifyItemRemoved(list.size)
                }
            } catch (ex: IndexOutOfBoundsException) {
                println(ex.message)
            }

            if (list.isEmpty()) {
                getDriverPresenter.instance().getView()?.checkoutBackground(true)
            }
        }
    }


    inner class ItemPostHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var driverItemTimer: DriverItemTimer? = null

        fun bind(driver: Driver) {
            itemView.driver_name.text = driver.nameDriver
            itemView.car_name.text = driver.carName
            itemView.driver_rating.text = driver.rating.toString()
            itemView.price.text = driver.price.toString().plus(" ₽")

            Glide.with(itemView.context).load(driver.imgDriver)
                .apply(RequestOptions().centerCrop().circleCrop())
                .into(itemView.img_driver)

            itemView.reject_driver.setOnClickListener {
                driverItemTimer?.cancel()
                rejectDriver(adapterPosition, true)
            }
        }
    }
}
