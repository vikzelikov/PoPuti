package bonch.dev.poputi.presentation.modules.common.profile.city

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import bonch.dev.poputi.R
import bonch.dev.poputi.domain.entities.common.ride.Address
import bonch.dev.poputi.presentation.modules.common.profile.ContractPresenter
import kotlinx.android.synthetic.main.string_suggest_item.view.*
import javax.inject.Inject

class SelectCityAdapter @Inject constructor(
    private var presenter: ContractPresenter.ISelectCityPresenter
) : RecyclerView.Adapter<SelectCityAdapter.ItemPostHolder>() {

    var list: ArrayList<Address> = arrayListOf()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemPostHolder {
        return ItemPostHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.string_suggest_item, parent, false)
        )
    }


    override fun getItemCount() = list.size


    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: ItemPostHolder, position: Int) {
        val address = list[position]
        holder.bind(address)

        holder.itemView.setOnClickListener {
            presenter.suggestDone(address)
        }


        holder.itemView.setOnTouchListener { _, _ ->
            presenter.instance().getView()?.hideKeyboard()
            false
        }

    }


    class ItemPostHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(post: Address) {
            itemView.text_suggest.text = post.address
        }
    }
}
