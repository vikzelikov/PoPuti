package bonch.dev.presenter.getdriver.adapters


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import bonch.dev.R
import bonch.dev.model.getdriver.pojo.Ride
import bonch.dev.utils.Keyboard.hideKeyboard
import bonch.dev.view.getdriver.CreateRideView
import kotlinx.android.synthetic.main.create_ride_layout.*


class AddressesListAdapter(
    val createRideView: CreateRideView,
    var list: ArrayList<Ride>,
    val context: Context
) : RecyclerView.Adapter<AddressesListAdapter.ItemPostHolder>() {

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
        val getDriverPresenter = createRideView.createRidePresenter

        holder.bind(list[position])

        holder.itemView.setOnClickListener {

            val fromAdrView = createRideView.from_adr
            val toAdrView = createRideView.to_adr

            if (fromAdrView.isFocused) {
                fromAdrView.setText(list[position].address)
                fromAdrView.setSelection(fromAdrView.text.length)
                getDriverPresenter!!.fromAdr = list[position]
            }

            if (toAdrView.isFocused) {
                toAdrView.setText(list[position].address)
                toAdrView.setSelection(toAdrView.text.length)

                getDriverPresenter!!.toAdr = list[position]
            }

            getDriverPresenter!!.addressesDone()
        }


        holder.itemView.setOnTouchListener { _, _ ->
            val activity = createRideView.activity!!
            hideKeyboard(activity, createRideView.view!!)

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

