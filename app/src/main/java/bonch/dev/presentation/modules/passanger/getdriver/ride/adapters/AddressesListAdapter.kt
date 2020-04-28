package bonch.dev.presentation.modules.passanger.getdriver.ride.adapters


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import bonch.dev.R
import bonch.dev.data.repository.passanger.getdriver.pojo.Ride
import bonch.dev.presentation.modules.passanger.getdriver.ride.presenter.ContractPresenter
import kotlinx.android.synthetic.main.ride_item.view.*
import javax.inject.Inject


class AddressesListAdapter @Inject constructor(private val createRidePresenter: ContractPresenter.ICreateRidePresenter) :
    RecyclerView.Adapter<AddressesListAdapter.ItemPostHolder>() {

    var list: ArrayList<Ride> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemPostHolder {
        return ItemPostHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.ride_item, parent, false)
        )
    }


    override fun getItemCount(): Int {
        return list.size
    }


    override fun onBindViewHolder(holder: ItemPostHolder, position: Int) {

        holder.bind(list[position])

        holder.itemView.setOnClickListener {
            createRidePresenter.onClickItem(list[position])
        }


        holder.itemView.setOnTouchListener { _, _ ->
            //            val activity = createRideView.activity!!
//            hideKeyboard(activity, createRideView.view!!)

            false
        }

    }


    class ItemPostHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(post: Ride) {
            itemView.address.text = post.address
            itemView.city.text = post.description
        }
    }

}

