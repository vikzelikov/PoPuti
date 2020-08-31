package bonch.dev.poputi.presentation.modules.common.chat.presenter

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Handler
import android.os.Looper
import bonch.dev.poputi.App
import bonch.dev.poputi.R
import bonch.dev.poputi.domain.entities.common.chat.Message
import bonch.dev.poputi.domain.entities.common.chat.MessageObject
import bonch.dev.poputi.domain.interactor.common.chat.IChatInteractor
import bonch.dev.poputi.domain.utils.NetworkUtil
import bonch.dev.poputi.presentation.base.BasePresenter
import bonch.dev.poputi.presentation.modules.common.CommonComponent
import bonch.dev.poputi.presentation.modules.common.chat.view.IChatView
import bonch.dev.poputi.service.passenger.PassengerRideService
import com.google.gson.Gson
import java.text.DateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class ChatPresenter : BasePresenter<IChatView>(), IChatPresenter {

    @Inject
    lateinit var chatInteractor: IChatInteractor

    private var isRegistered = false


    init {
        CommonComponent.commonComponent?.inject(this)
    }


    private val chatReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            onReceiveMessage(intent)
        }
    }


    private fun onReceiveMessage(intent: Intent?) {
        val data = intent?.getStringExtra(PassengerRideService.CHAT_TAG)

        if (data != null) {
            val message = Gson().fromJson(data, MessageObject::class.java)?.message
            if (message != null) {
                getView()?.setMessageView(message)
            }
        }
    }


    override fun registerReceivers() {
        val app = App.appComponent.getApp()

        //check regestered receivers before
        if (!isRegistered) {
            app.registerReceiver(
                chatReceiver,
                IntentFilter(PassengerRideService.CHAT_TAG)
            )

            isRegistered = true
        }
    }


    override fun sendMessage(text: String?) {
        if (text != null && text.isNotBlank()) {
            val message = Message()
            message.text = text
            message.isSender = true
            message.date = DateFormat.getTimeInstance(DateFormat.SHORT).format(Date())

            chatInteractor.sendMessage(message) { isSuccess ->
                val mainHandler = Handler(Looper.getMainLooper())
                val myRunnable = Runnable {
                    kotlin.run {
                        message.isSuccess = isSuccess
                        getView()?.getAdapter()?.notifyDataSetChanged()
                    }
                }

                mainHandler.post(myRunnable)
            }

            getView()?.setMessageView(message)

            getView()?.hideEmptyIcon()
        }
    }


    override fun getMessages() {
        val context = App.appComponent.getContext()

        if (NetworkUtil.isNetworkConnected(context)) {
            //get data and set recycler in DialogsView
            chatInteractor.getMessages { messages, _ ->
                val mainHandler = Handler(Looper.getMainLooper())
                val myRunnable = Runnable {
                    kotlin.run {

                        getView()?.hideLoading()

                        if (messages != null) {
                            if (messages.isEmpty()) {
                                getView()?.showEmptyIcon()
                            } else {
                                setMessages(messages)
                                getView()?.scrollBottom()
                                getView()?.hideEmptyIcon()
                            }
                        } else {
                            getView()?.showEmptyIcon()
                            getView()?.showNotification(context.getString(R.string.errorSystem))
                        }
                    }
                }

                mainHandler.post(myRunnable)
            }
        } else {
            getView()?.hideLoading()
            getView()?.showEmptyIcon()
            getView()?.showNotification(context.resources.getString(R.string.checkInternet))
        }
    }


    private fun setMessages(list: ArrayList<Message>) {
        try {
            list.forEach { message ->
                message.date?.let { date ->
                    if (date.length > 5) {
                        val del = message.date?.length?.minus(3)
                        del?.let {
                            message.date = message.date?.substring(0, del)
                        }
                    }
                }
            }
        } catch (ex: StringIndexOutOfBoundsException) {

        } catch (ex: Exception) {

        }

        getView()?.getAdapter()?.list?.addAll(list)
        getView()?.getAdapter()?.notifyDataSetChanged()
    }


    override fun isCheckoutDriver() = chatInteractor.isCheckoutDriver()


    override fun instance(): ChatPresenter {
        return this
    }
}