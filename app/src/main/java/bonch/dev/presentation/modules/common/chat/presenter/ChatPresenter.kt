package bonch.dev.presentation.modules.common.chat.presenter

import bonch.dev.App
import bonch.dev.R
import bonch.dev.domain.entities.common.chat.Message
import bonch.dev.domain.interactor.common.chat.IChatInteractor
import bonch.dev.domain.utils.NetworkUtil
import bonch.dev.presentation.base.BasePresenter
import bonch.dev.presentation.modules.common.CommonComponent
import bonch.dev.presentation.modules.common.chat.view.IChatView
import javax.inject.Inject

class ChatPresenter : BasePresenter<IChatView>(), IChatPresenter {

    @Inject
    lateinit var chatInteractor: IChatInteractor


    init {
        CommonComponent.commonComponent?.inject(this)
    }

    override fun sendMessage() {
        getView()?.setMessageView()

        getView()?.checkoutBackground(false)
    }


    override fun loadMessages() {
        val context = App.appComponent.getContext()

        if (NetworkUtil.isNetworkConnected(context)) {
            //get data and set recycler in DialogsView
            val list: ArrayList<Message> = chatInteractor.loadMessages()
            setMessages(list)

            if(list.isEmpty()){
                getView()?.checkoutBackground(true)
            }else{
                getView()?.checkoutBackground(false)
            }


        } else {
            getView()?.showNotification(context.resources.getString(R.string.checkInternet))
        }

        getView()?.scrollBottom()
    }


    private fun setMessages(list: ArrayList<Message>?) {
        if (list != null) {
            getView()?.getAdapter()?.list?.addAll(list)
        }
    }


    override fun instance(): ChatPresenter {
        return this
    }


}