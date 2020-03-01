package bonch.dev.presenter.getdriver.adapters


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import bonch.dev.Constant.Companion.DETAIL_RIDE_VIEW
import bonch.dev.Coordinator
import bonch.dev.Coordinator.Companion.replaceFragment
import bonch.dev.MainActivity
import bonch.dev.MainActivity.Companion.hideKeyboard
import bonch.dev.R
import bonch.dev.model.getdriver.pojo.Ride
import bonch.dev.view.getdriver.GetDriverView


class AddressesListAdapter(
    val getDriverView: GetDriverView,
    var list: ArrayList<Ride>,
    val context: Context,
    val root: View
) : RecyclerView.Adapter<AddressesListAdapter.ItemPostHolder>() {

    private val FROM = "FROM"
    private val TO = "TO"


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
        var bundle: Bundle
        val post = list[position]

        holder.bind(post)

        holder.itemView.setOnClickListener {

            if (getDriverView.fromAddress.isFocused) {
                getDriverView.fromAddress.setText(list[position].address)
            }

            if (getDriverView.toAddress.isFocused) {
                getDriverView.toAddress.setText(list[position].address)

                bundle = Bundle()
                bundle.putString(FROM, getDriverView.fromAddress.text.toString())
                bundle.putString(TO, getDriverView.toAddress.text.toString())

                val fm = (getDriverView.activity as MainActivity).supportFragmentManager
                replaceFragment(DETAIL_RIDE_VIEW, bundle, fm)

            }

        }


        holder.itemView.setOnTouchListener { _, _ ->
            hideKeyboard(getDriverView.activity!!, root)

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

