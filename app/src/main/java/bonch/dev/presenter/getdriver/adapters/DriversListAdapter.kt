package bonch.dev.presenter.getdriver.adapters


import android.content.Context
import android.graphics.Point
import android.view.Display
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import bonch.dev.R
import bonch.dev.model.getdriver.pojo.Driver
import bonch.dev.presenter.getdriver.DriverItemTimer
import bonch.dev.presenter.getdriver.DriverMainTimer
import bonch.dev.utils.Constants.TIMER_USER_GET_DRIVER
import bonch.dev.view.getdriver.GetDriverView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.driver_item.view.*
import kotlinx.android.synthetic.main.get_driver_layout.view.*


class DriversListAdapter(
    var list: ArrayList<Driver>,
    val context: Context,
    val getDriverView: GetDriverView
) : RecyclerView.Adapter<DriversListAdapter.ItemPostHolder>() {

    init {
        DriverMainTimer.getInstance(list, this)!!.start()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemPostHolder {
        return ItemPostHolder(
            LayoutInflater.from(context)
                .inflate(R.layout.driver_item, parent, false)
        )
    }


    override fun getItemCount(): Int = list.size


    override fun onBindViewHolder(holder: ItemPostHolder, position: Int) {
        val driver = list[position]
        val timeLine = holder.itemView.timer_driver_offer
        val startTime = driver.timeLine!!.toLong() * TIMER_USER_GET_DRIVER

        holder.bind(driver)

        holder.driverItemTimer?.cancel()
        holder.driverItemTimer = DriverItemTimer(startTime, 10, timeLine)
        holder.driverItemTimer!!.start()

        holder.itemView.accept_driver.setOnClickListener {
            val presenter = getDriverView.getDriverPresenter
            presenter?.selectDriver(driver)
        }
    }


    fun setNewDriver(driver: Driver) {
        val activity = getDriverView.activity

        activity?.let {
            if (driver.timeLine == null) {
                val display: Display = it.windowManager.defaultDisplay
                val size = Point()
                display.getSize(size)
                val width: Int = size.x
                driver.timeLine = width - 70
            }
        }

        list.add(0, driver)
        notifyItemInserted(0)
        notifyItemChanged(0)
    }


    fun rejectDriver(position: Int?, isUserAction: Boolean) {
        getDriverView.view?.driver_list?.post {
            try {
                if (isUserAction) {
                    list.removeAt(position!!)
                    notifyItemRemoved(position)
                }
            } catch (ex: IndexOutOfBoundsException) {
                println(ex.message)
            }

            try {
                if (!isUserAction) {
                    list.removeAt(list.size - 1)
                    notifyItemRemoved(list.size)
                }
            } catch (ex: IndexOutOfBoundsException) {
                println(ex.message)
            }
        }
    }


    inner class ItemPostHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var driverItemTimer: DriverItemTimer? = null
        private val driverName = itemView.driver_name
        private val carName = itemView.car_name
        private val rating = itemView.driver_rating
        private val driverImg = itemView.img_driver
        private val price = itemView.price


        fun bind(driver: Driver) {
            driverName.text = driver.nameDriver
            carName.text = driver.carName
            rating.text = driver.rating.toString()
            price.text = driver.price.toString().plus(" ₽")

            Glide.with(context).load(driver.imgDriver)
                .apply(RequestOptions().centerCrop().circleCrop())
                .into(driverImg)

            itemView.reject_driver.setOnClickListener {
                driverItemTimer!!.cancel()
                rejectDriver(adapterPosition, true)
            }
        }
    }
}
