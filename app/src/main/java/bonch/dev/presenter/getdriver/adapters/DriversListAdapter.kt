package bonch.dev.presenter.getdriver.adapters


import android.content.Context
import android.graphics.Point
import android.os.Handler
import android.view.Display
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import bonch.dev.R
import bonch.dev.model.getdriver.pojo.Driver
import bonch.dev.presenter.getdriver.DriverItemTimer
import bonch.dev.presenter.getdriver.DriverMainTimer
import bonch.dev.view.getdriver.GetDriverView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.driver_item.view.*


class DriversListAdapter(
    var list: ArrayList<Driver>,
    val context: Context,
    val getDriverView: GetDriverView
) : RecyclerView.Adapter<DriversListAdapter.ItemPostHolder>() {

    init {
        DriverMainTimer.getInstance(list)!!.start()
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

        holder.bind(position, driver)
        holder.driverItemTimer?.cancel()

        Handler().postDelayed({
            holder.driverItemTimer = DriverItemTimer(driver.timeLine!!.toLong() * 20, 1, timeLine, this)
            holder.driverItemTimer!!.start()
        }, 100)


        holder.itemView.accept_driver.setOnClickListener {
            val presenter = getDriverView.getDriverPresenter
            presenter?.selectDriver(driver)
        }


        //TODO
//        if (position == 0) {
//            val item: View = holder.itemView
//            val params: LinearLayout.LayoutParams =
//                LinearLayout.LayoutParams(
//                    ViewGroup.LayoutParams.MATCH_PARENT,
//                    ViewGroup.LayoutParams.WRAP_CONTENT
//                )
//            params.setMargins(30, 40, 30, 0)
//            item.layoutParams = params
//        }

    }


    fun setNewDriver(driver: Driver) {
        if (driver.timeLine == null) {
            val activity = getDriverView.activity!!
            val display: Display = activity.windowManager.defaultDisplay
            val size = Point()
            display.getSize(size)
            val width: Int = size.x
            driver.timeLine = width - 70
        }

        list.add(driver)
        notifyItemInserted(list.size)
        notifyItemChanged(list.size)
    }


    fun rejectDriver(position: Int?, isUserAction: Boolean) {
        try {
            if (isUserAction) {
                list.removeAt(position!!)
                notifyItemRemoved(position)
                notifyItemChanged(position)
            }
        } catch (ex: IndexOutOfBoundsException) {
            list.removeAt(list.size - 1)
            notifyItemRemoved(list.size)
        }

        try {
            if (!isUserAction) {
                list.removeAt(0)
                notifyItemRemoved(0)
            }
        } catch (ex: IndexOutOfBoundsException) {
            println(ex.message)
        }
    }


    inner class ItemPostHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var driverItemTimer: DriverItemTimer? = null
        private val driverName = itemView.driver_name
        private val carName = itemView.car_name
        private val rating = itemView.driver_rating
        private val driverImg = itemView.img_driver
        private val price = itemView.price
        private val timeLine = itemView.timer_driver_offer


        fun bind(position: Int, driver: Driver) {
            driverName.text = driver.nameDriver
            carName.text = driver.carName
            rating.text = driver.rating.toString()
            price.text = driver.price.toString().plus(" ₽")

            Glide.with(context).load(driver.imgDriver)
                .apply(RequestOptions().centerCrop().circleCrop())
                .into(driverImg)

            itemView.reject_driver.setOnClickListener {
                driverItemTimer!!.cancel()
                rejectDriver(position, true)

                val layoutParams: ViewGroup.LayoutParams = timeLine.layoutParams
                layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
                timeLine.layoutParams = layoutParams
            }
        }
    }
}
