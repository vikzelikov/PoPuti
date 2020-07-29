package bonch.dev.presentation.modules.common.chat.presenter

interface IChatPresenter {

    fun instance(): ChatPresenter

    fun sendMessage(text: String)

    fun getMessages()

    fun registerReceivers()

    fun isCheckoutDriver(): Boolean

}