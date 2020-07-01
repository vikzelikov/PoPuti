package bonch.dev.data.repository.common.chat

import bonch.dev.domain.entities.common.chat.Message
import bonch.dev.presentation.interfaces.DataHandler
import bonch.dev.presentation.interfaces.SuccessHandler

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