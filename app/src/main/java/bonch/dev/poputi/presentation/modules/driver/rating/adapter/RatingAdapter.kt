package bonch.dev.poputi.presentation.modules.driver.rating.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import bonch.dev.poputi.R
import bonch.dev.poputi.domain.entities.common.rate.Review
import kotlinx.android.synthetic.main.rating_item.view.*
import javax.inject.Inject

class RatingAdapter @Inject constructor() :
    RecyclerView.Adapter<RatingAdapter.ItemPostHolder>() {


    var list: ArrayList<Review> = arrayListOf()


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemPostHolder {
        return ItemPostHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.rating_item, parent, false)
        )
    }


    override fun getItemCount() = list.size


    override fun onBindViewHolder(holder: ItemPostHolder, position: Int) {
        holder.bind(list[position])
    }


    inner class ItemPostHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(review: Review) {
            itemView.name.text = review.author?.firstName

            if (review.text.isNullOrEmpty()) itemView.comment?.visibility = View.GONE
            else {
                itemView.comment?.visibility = View.VISIBLE
                itemView.comment?.text = review.text
            }

            itemView.time.text = ""

            review.rating?.let { setRating(it) }

        }

        private fun setRating(rating: Int) {
            when (rating) {
                0 -> {
                    getStar().forEach { it.visibility = View.INVISIBLE }

                    getDisableStar().forEach { it.visibility = View.VISIBLE }
                }

                1 -> {
                    getStar().forEachIndexed { index, star ->
                        star.visibility = if (index < 1) View.VISIBLE
                        else View.INVISIBLE
                    }

                    getDisableStar().forEachIndexed { index, star ->
                        star.visibility = if (index < 1) View.INVISIBLE
                        else View.VISIBLE
                    }
                }

                2 -> {
                    getStar().forEachIndexed { index, star ->
                        star.visibility = if (index < 2) View.VISIBLE
                        else View.INVISIBLE
                    }

                    getDisableStar().forEachIndexed { index, star ->
                        star.visibility = if (index < 2) View.INVISIBLE
                        else View.VISIBLE
                    }
                }

                3 -> {
                    getStar().forEachIndexed { index, star ->
                        star.visibility = if (index < 3) View.VISIBLE
                        else View.INVISIBLE
                    }

                    getDisableStar().forEachIndexed { index, star ->
                        star.visibility = if (index < 3) View.INVISIBLE
                        else View.VISIBLE
                    }
                }

                4 -> {
                    getStar().forEachIndexed { index, star ->
                        star.visibility = if (index < 4) View.VISIBLE
                        else View.INVISIBLE
                    }

                    getDisableStar().forEachIndexed { index, star ->
                        star.visibility = if (index < 4) View.INVISIBLE
                        else View.VISIBLE
                    }
                }

                5 -> {
                    getStar().forEach { it.visibility = View.VISIBLE }

                    getDisableStar().forEach {it.visibility = View.INVISIBLE}
                }
            }
        }


        private fun getStar(): ArrayList<ImageView> {
            return arrayListOf(
                itemView.star1,
                itemView.star2,
                itemView.star3,
                itemView.star4,
                itemView.star5
            )
        }

        private fun getDisableStar(): ArrayList<ImageView> {
            return arrayListOf(
                itemView.dis_star1,
                itemView.dis_star2,
                itemView.dis_star3,
                itemView.dis_star4,
                itemView.dis_star5
            )
        }
    }
}