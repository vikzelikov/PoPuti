package bonch.dev.poputi.presentation.modules.common.profile.driver.profits

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.annotation.AnimRes
import androidx.recyclerview.widget.RecyclerView
import bonch.dev.poputi.R
import bonch.dev.poputi.domain.entities.common.ride.RideInfo
import bonch.dev.poputi.presentation.modules.common.profile.ContractPresenter
import kotlinx.android.synthetic.main.order_profits_item.view.*
import javax.inject.Inject
import kotlin.math.roundToInt

class ProfitsAdapter @Inject constructor(private val presenter: ContractPresenter.IProfitsPresenter) :
    RecyclerView.Adapter<ProfitsAdapter.ItemPostHolder>() {


    var list: ArrayList<RideInfo> = arrayListOf()

    var isLeft = true

    var lastPosition = -1


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemPostHolder {
        return ItemPostHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.order_profits_item, parent, false)
        )
    }


    override fun getItemCount() = list.size


    override fun onBindViewHolder(holder: ItemPostHolder, position: Int) {
        holder.bind(list[position])

        holder.itemView.setOnClickListener { }

        val anim = if (isLeft) R.anim.slide_in_left else R.anim.slide_in_right
        setAnimation(holder.itemView, anim, position)
    }


    private fun setAnimation(viewToAnimate: View, @AnimRes id: Int, position: Int) {
        if (position in (lastPosition + 1)..4) {
            val animation = AnimationUtils.loadAnimation(viewToAnimate.context, id)
            viewToAnimate.startAnimation(animation)

            lastPosition = position
        }
    }


    inner class ItemPostHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(ride: RideInfo) {
            itemView.from.text = ride.position
            itemView.to.text = ride.destination
            itemView.price.text = ride.price.toString().plus(" ₽")
            itemView.time.text = "12:34"

            val fee = ride.price?.times(presenter.instance().FEE)
            val output = ((fee?.times(100.0))?.roundToInt() ?: 0) / 100.0
            itemView.fee.text = itemView.context.getString(R.string.fee)
                .plus(": -")
                .plus(output.toString())
                .plus(" ₽")
        }
    }
}