package bonch.dev.presenter.driver.signup.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.ContentFrameLayout
import androidx.recyclerview.widget.RecyclerView
import bonch.dev.R
import bonch.dev.presenter.driver.signup.CarInfoSuggestPresenter
import bonch.dev.utils.Keyboard
import kotlinx.android.synthetic.main.car_suggest_item.view.*


class CarInfoSuggestAdapter(
    private val carInfoSuggestPresenter: CarInfoSuggestPresenter,
    var list: ArrayList<String>
) : RecyclerView.Adapter<CarInfoSuggestAdapter.ItemPostHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemPostHolder {
        return ItemPostHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.car_suggest_item, parent, false)
        )
    }


    override fun getItemCount(): Int {
        return list.size
    }


    override fun onBindViewHolder(holder: ItemPostHolder, position: Int) {
        val suggestText = list[position]
        holder.bind(suggestText)

        holder.itemView.setOnClickListener {
            carInfoSuggestPresenter.suggestDone(suggestText)
        }


        holder.itemView.setOnTouchListener { _, _ ->
            val activity = carInfoSuggestPresenter.carInfoSuggestView
            val rootView = activity.findViewById<ContentFrameLayout>(android.R.id.content).rootView
            Keyboard.hideKeyboard(activity, rootView)

            false
        }

    }


    class ItemPostHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(post: String) {
            itemView.text_suggest.text = post
        }
    }

}

