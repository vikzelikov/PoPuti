package bonch.dev.presentation.modules.common.chat.presenter

import android.os.Handler
import android.os.Looper
import bonch.dev.App
import bonch.dev.R
import bonch.dev.domain.entities.common.chat.Message
import bonch.dev.domain.interactor.common.chat.IChatInteractor
import bonch.dev.domain.utils.NetworkUtil
import bonch.dev.presentation.base.BasePresenter
import bonch.dev.presentation.modules.common.CommonComponent
import bonch.dev.presentation.modules.common.chat.view.IChatView
import java.text.DateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class ChatPresenter : BasePresenter<IChatView>(), IChatPresenter {

    @Inject
    lateinit var chatInteractor: IChatInteractor


    init {
        CommonComponent.commonComponent?.inject(this)
    }

    override fun sendMessage(text: String) {
        if (text.isNotEmpty()) {
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
        getView()?.getAdapter()?.list?.addAll(list)
        getView()?.getAdapter()?.notifyDataSetChanged()
    }


    override fun instance(): ChatPresenter {
        return this
    }
}