package bonch.dev.poputi.data.repository.common.chat

import bonch.dev.poputi.domain.entities.common.chat.Message
import bonch.dev.poputi.presentation.interfaces.DataHandler
import bonch.dev.poputi.presentation.interfaces.SuccessHandler

interface IChatRepository {

    fun sendMessage(message: Message, token: String, callback: SuccessHandler)

    fun getMessages(rideId: Int, callback: DataHandler<ArrayList<Message>?>)

    fun connectSocket(
        rideId: Int,
        token: String,
        callback: SuccessHandler
    )

    fun subscribeOnChat(callback: DataHandler<String?>)

    fun disconnectSocket()
}