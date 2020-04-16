package bonch.dev.view.passanger.getdriver

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import bonch.dev.R
import bonch.dev.presenter.passanger.getdriver.ChatPresenter
import kotlinx.android.synthetic.main.chat_activity.*

class ChatView : AppCompatActivity() {

    private var chatPresenter: ChatPresenter? = null

    init {
        if (chatPresenter == null) {
            chatPresenter = ChatPresenter(this)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chat_activity)

        chatPresenter?.initChatAdapter()

        chatPresenter?.loadMessages()

        setListeners()
    }


    private fun setListeners() {
        send_message.setOnClickListener {
            chatPresenter?.sendMessage()
        }

        clip_photo.setOnClickListener {
            Toast.makeText(this, "Clip photo", Toast.LENGTH_SHORT).show()
        }

        back_btn.setOnClickListener {
            finish()
        }
    }

}