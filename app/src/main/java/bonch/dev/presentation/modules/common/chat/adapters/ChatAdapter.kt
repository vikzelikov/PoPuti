package bonch.dev.presentation.modules.common.chat.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import bonch.dev.R
import bonch.dev.domain.entities.common.chat.Message
import bonch.dev.presentation.modules.common.chat.presenter.IChatPresenter
import kotlinx.android.synthetic.main.message_receiver_item.view.*
import kotlinx.android.synthetic.main.message_sender_item.view.*
import javax.inject.Inject

class ChatAdapter @Inject constructor(private val chatPresenter: IChatPresenter) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var list: ArrayList<Message> = arrayListOf()

    private val RECEIVER = 0
    private val SENDER = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == SENDER) {
            SenderViewHolder(
                (LayoutInflater.from(parent.context).inflate(
                    R.layout.message_sender_item,
                    parent,
                    false
                ))
            )
        } else {
            ReceiverViewHolder(
                (LayoutInflater.from(parent.context).inflate(
                    R.layout.message_receiver_item,
                    parent,
                    false
                ))
            )
        }
    }


    override fun getItemCount(): Int = list.size


    override fun getItemViewType(position: Int): Int {
        return if (list[position].isSender!!) {
            SENDER
        } else RECEIVER
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        return if (getItemViewType(position) == SENDER) {
            (holder as SenderViewHolder).bind(position)
        } else {
            (holder as ReceiverViewHolder).bind(position)
        }
    }

    inner class SenderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(position: Int) {
            itemView.message_text_sender.text = list[position].textMessage
            itemView.date_sender.text = list[position].date
        }
    }

    inner class ReceiverViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(position: Int) {
            itemView.message_text_receiver.text = list[position].textMessage
            itemView.date_receiver.text = list[position].date
        }
    }

}