package bonch.dev.presentation.modules.common.chat.presenter

interface IChatPresenter {

    fun instance(): ChatPresenter

    fun sendMessage()

    fun loadMessages()

}