package bonch.dev.view.getdriver


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import bonch.dev.Constant.Companion.DETAIL_RIDE_VIEW
import bonch.dev.Coordinator
import bonch.dev.MainActivity
import bonch.dev.R
import bonch.dev.model.getdriver.pojo.Ride


class AddressesListAdapter(
    val getDriverView: GetDriverView,
    var list: ArrayList<Ride>,
    val context: Context,
    val root: View
) : RecyclerView.Adapter<AddressesListAdapter.ItemPostHolder>() {

    private var imm: InputMethodManager? = null
    private var coordinator: Coordinator? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemPostHolder {

        imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

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

            if(getDriverView.fromAddress.isFocused){
                getDriverView.fromAddress.setText(list[position].address)

                coordinator!!.replaceFragment(DETAIL_RIDE_VIEW)

                //make routing +
            }

            if(getDriverView.toAddress.isFocused){
                getDriverView.toAddress.setText(list[position].address)

                coordinator!!.replaceFragment(DETAIL_RIDE_VIEW)

            }

        }


        holder.itemView.setOnTouchListener { _, _ ->
            imm!!.hideSoftInputFromWindow(root.windowToken, 0)

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


    init {
        if(coordinator == null){
            coordinator = (getDriverView.activity as MainActivity).coordinator
        }
    }

}

