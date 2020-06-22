package bonch.dev.presentation.modules.common.chat.view

import bonch.dev.domain.entities.common.chat.Message
import bonch.dev.presentation.interfaces.IBaseView
import bonch.dev.presentation.modules.common.chat.adapters.ChatAdapter

interface IChatView : IBaseView {

    fun getAdapter(): ChatAdapter

    fun setMessageView(message: Message)

    fun scrollBottom()

    fun showEmptyIcon()

    fun hideEmptyIcon()

}