package bonch.dev.poputi.presentation.modules.passenger.getdriver.adapters


import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
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


    fun setItem(index: Int, address: Address) {
        try {
            list.add(index, address)
            notifyItemChanged(index)
        } catch (ex: IndexOutOfBoundsException) {

        }
    }


    class ItemPostHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(post: Address) {
            itemView.address.text = post.address

            itemView.city.text = if (post.description.isNullOrEmpty()) {
                "Место"//todo
            } else {
                post.description
            }
        }
    }
}

