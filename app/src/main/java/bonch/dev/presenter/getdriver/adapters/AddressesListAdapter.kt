package bonch.dev.presenter.getdriver.adapters


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import bonch.dev.MainActivity.Companion.hideKeyboard
import bonch.dev.R
import bonch.dev.model.getdriver.pojo.Ride
import bonch.dev.view.getdriver.GetDriverView
import kotlinx.android.synthetic.main.get_driver_fragment.*
import kotlinx.android.synthetic.main.get_driver_layout.*


class AddressesListAdapter(
    val getDriverView: GetDriverView,
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
        val getDriverPresenter = getDriverView.getDriverPresenter

        holder.bind(list[position])

        holder.itemView.setOnClickListener {

            val fromAdrView = getDriverView.from_adr
            val toAdrView = getDriverView.to_adr

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
            val activity = getDriverView.activity!!
            hideKeyboard(activity, getDriverView.view!!)

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

