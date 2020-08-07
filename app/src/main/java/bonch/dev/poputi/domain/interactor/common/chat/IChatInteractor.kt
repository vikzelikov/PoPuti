package bonch.dev.poputi.domain.interactor.common.chat

import bonch.dev.poputi.domain.entities.common.chat.Message
import bonch.dev.poputi.presentation.interfaces.DataHandler
import bonch.dev.poputi.presentation.interfaces.SuccessHandler

interface IChatInteractor {

    fun sendMessage(message: Message, callback: SuccessHandler)

    fun getMessages(callback: DataHandler<ArrayList<Message>?>)

    fun isCheckoutDriver(): Boolean

}