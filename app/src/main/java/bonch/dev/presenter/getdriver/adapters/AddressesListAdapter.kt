package bonch.dev.presenter.getdriver.adapters


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.PointerIcon
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import bonch.dev.Constant
import bonch.dev.Constant.Companion.DETAIL_RIDE_VIEW
import bonch.dev.Coordinator
import bonch.dev.Coordinator.Companion.replaceFragment
import bonch.dev.MainActivity
import bonch.dev.MainActivity.Companion.hideKeyboard
import bonch.dev.R
import bonch.dev.model.getdriver.pojo.Ride
import bonch.dev.presenter.getdriver.GetDriverPresenter
import bonch.dev.view.getdriver.GetDriverView
import com.yandex.mapkit.geometry.Point


class AddressesListAdapter(
    val getDriverView: GetDriverView,
    var list: ArrayList<Ride>,
    val context: Context,
    val root: View
) : RecyclerView.Adapter<AddressesListAdapter.ItemPostHolder>() {

    var fromAdr: Ride? = null
    var toAdr: Ride? = null
    private val fromAddressView = getDriverView.fromAddress
    private val toAddressView = getDriverView.toAddress

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemPostHolder {
        return ItemPostHolder(
            LayoutInflater.from(context)
                .inflate(R.layout.ride_item, parent, false)
        )
    }


    override fun getItemCount(): Int {
        return list.size
    }


    override fun onBindViewHolder(holder: ItemPostHolder, position: Int) {
        val post = list[position]

        holder.bind(post)

        holder.itemView.setOnClickListener {

            if (fromAddressView.isFocused) {
                fromAddressView.setText(list[position].address)
                fromAddressView.setSelection(fromAddressView.text.length)

                fromAdr = list[position]
            }

            if (toAddressView.isFocused) {
                toAddressView.setText(list[position].address)
                toAddressView.setSelection(toAddressView.text.length)

                toAdr = list[position]
            }

            getDriverView.getDriverPresenter!!.addressesDone()

        }


        holder.itemView.setOnTouchListener { _, _ ->
            val activity = getDriverView.activity!!
            hideKeyboard(activity, root)

            false
        }

    }


    class ItemPostHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val address = itemView.findViewById<TextView>(R.id.address)
        private val city = itemView.findViewById<TextView>(R.id.city)

        fun bind(post: Ride) {
            address.text = post.address
            city.text = post.city
        }
    }

}

