package bonch.dev.poputi.presentation.modules.common.chat.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import bonch.dev.poputi.R
import bonch.dev.poputi.domain.entities.common.chat.Message
import kotlinx.android.synthetic.main.message_receiver_item.view.*
import kotlinx.android.synthetic.main.message_sender_item.view.*
import javax.inject.Inject

class ChatAdapter @Inject constructor() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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


    fun setMessage(message: Message) {
        list.add(message)
        notifyItemInserted(list.lastIndex)
        notifyItemChanged(list.lastIndex)
    }


    override fun getItemCount(): Int = list.size


    override fun getItemViewType(position: Int): Int {
        return if (list[position].isSender) {
            SENDER
        } else RECEIVER
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = list[position]

        return if (getItemViewType(position) == SENDER) {
            (holder as SenderViewHolder).bind(message)
        } else {
            (holder as ReceiverViewHolder).bind(message)
        }
    }


    inner class SenderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(message: Message) {
            if (!message.isSuccess) itemView.error.visibility = View.VISIBLE
            itemView.message_text_sender.text = message.text
            itemView.date_sender.text = message.date
        }
    }


    inner class ReceiverViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(message: Message) {
            itemView.message_text_receiver.text = message.text
            itemView.date_receiver.text = message.date
        }
    }

}