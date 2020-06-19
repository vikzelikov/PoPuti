package bonch.dev.presentation.modules.common.chat.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.recyclerview.widget.LinearLayoutManager
import bonch.dev.App
import bonch.dev.R
import bonch.dev.di.component.common.DaggerCommonComponent
import bonch.dev.di.module.common.CommonModule
import bonch.dev.domain.entities.common.chat.Message
import bonch.dev.domain.entities.common.ride.RideInfo
import bonch.dev.domain.entities.common.ride.ActiveRide
import bonch.dev.domain.entities.common.ride.Driver
import bonch.dev.domain.utils.Keyboard
import bonch.dev.presentation.modules.common.CommonComponent
import bonch.dev.presentation.modules.common.chat.adapters.ChatAdapter
import bonch.dev.presentation.modules.common.chat.presenter.IChatPresenter
import kotlinx.android.synthetic.main.chat_activity.*
import javax.inject.Inject

class ChatView : AppCompatActivity(), IChatView {

    @Inject
    lateinit var chatPresenter: IChatPresenter

    @Inject
    lateinit var chatAdapter: ChatAdapter


    private var handlerAnimation: Handler? = null


    init {
        initDI()

        CommonComponent.commonComponent?.inject(this)

        chatPresenter.instance().attachView(this)
    }


    //first build DI component
    private fun initDI() {
        if (CommonComponent.commonComponent == null) {
            CommonComponent.commonComponent = DaggerCommonComponent
                .builder()
                .commonModule(CommonModule())
                .appComponent(App.appComponent)
                .build()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chat_activity)

        val ride = ActiveRide.activeRide
        name_driver.text = ride?.driver?.firstName

        initChatAdapter()

        chatPresenter.loadMessages()

        setListeners()

        setMovingButtonListener()
    }


    override fun setListeners() {
        send_message.setOnClickListener {
            chatPresenter.sendMessage()
        }

        back_btn.setOnClickListener {
            finish()
        }
    }


    override fun setMessageView() {
        val textMessage = message_text_edit.text.toString().trim()

        if (textMessage.isNotEmpty()) {
            val message = Message(
                textMessage,
                "12:12",
                true
            )
            getAdapter().list.add(message)
            message_text_edit.setText("")
            scrollBottom()
        }

        //******* TODO remove it
        val message = Message(
            "Добрый день! Где вы находитесь?",
            "12:12",
            false
        )
        Handler().postDelayed({
            getAdapter().list.add(message)
            scrollBottom()
        }, 1000)
        //******
    }


    private fun initChatAdapter() {
        chat_recycler.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = chatAdapter
        }
    }


    override fun scrollBottom() {
        chat_recycler.scrollToPosition(getAdapter().list.count() - 1)
    }


    override fun getAdapter(): ChatAdapter {
        return chatAdapter
    }


    override fun getNavHost(): NavController? {
        return null
    }


    override fun hideKeyboard() {
        Keyboard.hideKeyboard(this, this.chat_activity)
    }


    override fun showLoading() {}


    override fun hideLoading() {}


    private fun setMovingButtonListener() {
        var heightDiff: Int
        val rect = Rect()
        var screenHeight = 0
        var startHeight = 0
        var isUp = true

        chat_activity.viewTreeObserver
            .addOnGlobalLayoutListener {
                chat_activity.getWindowVisibleDisplayFrame(rect)
                heightDiff = screenHeight - (rect.bottom - rect.top)

                if (screenHeight == 0) {
                    screenHeight = chat_activity.rootView.height
                }

                if (startHeight == 0) {
                    startHeight = screenHeight - (rect.bottom - rect.top)
                }

                if (heightDiff > startHeight) {
                    //move UP
                    if (isUp) {
                        scrollBottom()
                        isUp = false
                    }
                } else {
                    //move DOWN
                    if (!isUp) {
                        scrollBottom()
                        isUp = true
                    }
                }

            }
    }


    override fun showNotification(text: String) {
        val view = general_notification

        view.text = text
        handlerAnimation?.removeCallbacksAndMessages(null)
        handlerAnimation = Handler()
        view.translationY = 0.0f
        view.alpha = 0.0f

        view.animate()
            .setDuration(500L)
            .translationY(100f)
            .alpha(1.0f)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    handlerAnimation?.postDelayed({ hideNotifications() }, 2000)
                }
            })
    }


    private fun hideNotifications() {
        val view = general_notification

        view.animate()
            .setDuration(500L)
            .translationY(-100f)
            .alpha(0.0f)
    }


    override fun checkoutBackground(isShow: Boolean) {
        if (isShow) {
            chat_empty_icon.visibility = View.VISIBLE
            chat_empty_text.visibility = View.VISIBLE
        } else {
            chat_empty_icon.visibility = View.GONE
            chat_empty_text.visibility = View.GONE
        }
    }


    override fun onDestroy() {
        chatPresenter.instance().detachView()
        CommonComponent.commonComponent = null
        super.onDestroy()
    }
}