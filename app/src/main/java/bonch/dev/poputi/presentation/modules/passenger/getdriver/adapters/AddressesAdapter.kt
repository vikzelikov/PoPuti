package bonch.dev.poputi.presentation.modules.passenger.getdriver.adapters


import android.annotation.SuppressLint
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import bonch.dev.poputi.App
import bonch.dev.poputi.R
import bonch.dev.poputi.domain.entities.common.ride.Address
import bonch.dev.poputi.presentation.modules.passenger.getdriver.presenter.ContractPresenter
import kotlinx.android.synthetic.main.ride_item.view.*
import javax.inject.Inject


class AddressesAdapter @Inject constructor(private val createRidePresenter: ContractPresenter.ICreateRidePresenter) :
    RecyclerView.Adapter<AddressesAdapter.ItemPostHolder>() {

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
            createRidePresenter.onClickItem(list[position])
        }


        holder.itemView.setOnTouchListener { _, _ ->
            Handler().postDelayed({
                createRidePresenter.instance().getView()?.hideKeyboard()
            }, 250)

            false
        }
    }


    class ItemPostHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(post: Address) {
            itemView.address?.text = post.address

            itemView.city?.text = if (post.description.isNullOrEmpty()) {
                App.appComponent.getApp().getString(R.string.place)
            } else {
                post.description
            }
        }
    }
}

