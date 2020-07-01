package bonch.dev.domain.interactor.common.chat

import bonch.dev.domain.entities.common.chat.Message
import bonch.dev.presentation.interfaces.DataHandler
import bonch.dev.presentation.interfaces.SuccessHandler

interface IChatInteractor {

    fun sendMessage(message: Message, callback: SuccessHandler)

    fun getMessages(callback: DataHandler<ArrayList<Message>?>)

}