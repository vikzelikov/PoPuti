package bonch.dev.poputi.presentation.modules.common.chat.presenter

import bonch.dev.poputi.presentation.modules.common.chat.presenter.ChatPresenter

interface IChatPresenter {

    fun instance(): ChatPresenter

    fun sendMessage(text: String)

    fun getMessages()

    fun registerReceivers()

    fun isCheckoutDriver(): Boolean

}