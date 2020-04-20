package bonch.dev.presentation.presenter.passanger.getdriver.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import bonch.dev.R
import bonch.dev.data.repository.passanger.getdriver.pojo.Message
import kotlinx.android.synthetic.main.message_receiver_item.view.*
import kotlinx.android.synthetic.main.message_sender_item.view.*

class ChatAdapter(var messageList: ArrayList<Message>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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

    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (messageList[position].isSender!!) {
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
            itemView.message_text_sender.text = messageList[position].textMessage
            itemView.date_sender.text = messageList[position].date
        }
    }

    inner class ReceiverViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(position: Int) {
            itemView.message_text_receiver.text = messageList[position].textMessage
            itemView.date_receiver.text = messageList[position].date
        }
    }

}