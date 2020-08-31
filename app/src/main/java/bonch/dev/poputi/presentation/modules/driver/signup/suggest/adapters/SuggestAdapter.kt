package bonch.dev.poputi.presentation.modules.driver.signup.suggest.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import bonch.dev.poputi.R
import bonch.dev.poputi.presentation.modules.driver.signup.suggest.presenter.ISuggestPresenter
import kotlinx.android.synthetic.main.string_suggest_item.view.*
import javax.inject.Inject


class SuggestAdapter @Inject constructor(
    private var suggestPresenter: ISuggestPresenter
) : RecyclerView.Adapter<SuggestAdapter.ItemPostHolder>() {

    var list: ArrayList<String> = arrayListOf()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemPostHolder {
        return ItemPostHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.string_suggest_item, parent, false)
        )
    }


    override fun getItemCount() = list.size


    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: ItemPostHolder, position: Int) {
        val suggestText = list[position]
        holder.bind(suggestText)

        holder.itemView.setOnClickListener {
            suggestPresenter.suggestDone(suggestText)
        }


        holder.itemView.setOnTouchListener { _, _ ->
            suggestPresenter.instance().getView()?.hideKeyboard()
            false
        }

    }


    class ItemPostHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(post: String) {
            itemView.text_suggest?.text = post
        }
    }

}

