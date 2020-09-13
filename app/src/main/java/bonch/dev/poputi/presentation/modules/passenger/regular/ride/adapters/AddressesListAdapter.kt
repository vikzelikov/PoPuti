package bonch.dev.poputi.presentation.modules.passenger.regular.ride.adapters


import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import bonch.dev.poputi.App
import bonch.dev.poputi.R
import bonch.dev.poputi.domain.entities.common.ride.Address
import bonch.dev.poputi.presentation.modules.passenger.regular.ride.presenter.ContractPresenter
import kotlinx.android.synthetic.main.ride_item.view.*
import javax.inject.Inject


class AddressesListAdapter @Inject constructor(private val createRegularDrivePresenter: ContractPresenter.ICreateRegularDrivePresenter) :
    RecyclerView.Adapter<AddressesListAdapter.ItemPostHolder>() {

    var list: ArrayList<Address> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemPostHolder {
        return ItemPostHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.ride_item, parent, false)
        )
    }


    override fun getItemCount(): Int = list.size


    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: ItemPostHolder, position: Int) {
        holder.bind(list[position])

        holder.itemView.setOnClickListener {
            createRegularDrivePresenter.onClickItem(list[position])
        }


        holder.itemView.setOnTouchListener { _, _ ->
            createRegularDrivePresenter.instance().getView()?.hideKeyboard()
            false
        }

    }


    class ItemPostHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(post: Address) {
            itemView.address.text = post.address

            itemView.city.text = if (post.description.isNullOrEmpty()) {
                App.appComponent.getApp().getString(R.string.place)
            } else {
                post.description
            }
        }
    }
}

