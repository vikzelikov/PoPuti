package bonch.dev.presentation.modules.common.chat.view

import bonch.dev.presentation.interfaces.IBaseView
import bonch.dev.presentation.modules.common.chat.adapters.ChatAdapter

interface IChatView : IBaseView {

    fun getAdapter(): ChatAdapter

    fun setMessageView()

    fun checkoutBackground(isShow: Boolean)

    fun scrollBottom()

}