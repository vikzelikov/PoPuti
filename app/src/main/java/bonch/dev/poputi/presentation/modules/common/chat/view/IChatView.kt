package bonch.dev.poputi.presentation.modules.common.chat.view

import bonch.dev.poputi.domain.entities.common.chat.Message
import bonch.dev.poputi.presentation.interfaces.IBaseView
import bonch.dev.poputi.presentation.modules.common.chat.adapters.ChatAdapter

interface IChatView : IBaseView {

    fun getAdapter(): ChatAdapter

    fun setMessageView(message: Message)

    fun scrollBottom()

    fun showEmptyIcon()

    fun hideEmptyIcon()

}