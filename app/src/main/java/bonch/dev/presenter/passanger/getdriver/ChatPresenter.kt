package bonch.dev.presenter.passanger.getdriver

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import bonch.dev.model.passanger.getdriver.ChatModel
import bonch.dev.model.passanger.getdriver.pojo.Message
import bonch.dev.presenter.passanger.getdriver.adapters.ChatAdapter
import bonch.dev.view.passanger.getdriver.ChatView
import kotlinx.android.synthetic.main.chat_activity.*

class ChatPresenter(var chatView: ChatView) {

    private var chatModel: ChatModel? = null
    private var chatAdapter: ChatAdapter? = null
    private var chatRecycler: RecyclerView? = null

    init {
        if (chatModel == null) {
            chatModel = ChatModel()
        }
    }

    fun sendMessage() {
        val editText = chatView.message_text_edit
        val textMessage = editText.text.toString().trim()

        if (textMessage.isNotEmpty()) {
            val message = Message(textMessage, "12:12", true)
            chatAdapter?.messageList?.add(message)
            editText.setText("")
            scrollBottom()
        }
    }


    fun loadMessages() {
        if (true) {
            //TODO realm and check internet
            //get data and set recycler in DialogsView
            val list: ArrayList<Message>? = chatModel?.loadMessages()
            setMessages(list)
        } else {
            //realm
        }

        scrollBottom()
    }


    private fun setMessages(list: ArrayList<Message>?) {
        if (list != null) {
            chatAdapter?.messageList?.addAll(list)
        }
    }


    private fun scrollBottom() {
        chatRecycler?.scrollToPosition(chatAdapter?.messageList!!.count() - 1)
    }


    fun initChatAdapter() {
        chatAdapter = ChatAdapter(ArrayList())
        chatRecycler = chatView.chat_recycler
        chatRecycler?.layoutManager = LinearLayoutManager(chatView)
        chatRecycler?.adapter = chatAdapter
    }


}