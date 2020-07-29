package bonch.dev.domain.interactor.common.chat

import bonch.dev.data.repository.common.chat.IChatRepository
import bonch.dev.data.storage.common.profile.IProfileStorage
import bonch.dev.domain.entities.common.chat.Message
import bonch.dev.domain.entities.common.ride.ActiveRide
import bonch.dev.presentation.interfaces.DataHandler
import bonch.dev.presentation.interfaces.SuccessHandler
import bonch.dev.presentation.modules.common.CommonComponent
import javax.inject.Inject

class ChatInteractor : IChatInteractor {

    @Inject
    lateinit var chatRepository: IChatRepository

    @Inject
    lateinit var profileStorage: IProfileStorage

    init {
        CommonComponent.commonComponent?.inject(this)
    }


    override fun sendMessage(message: Message, callback: SuccessHandler) {
        val token = profileStorage.getToken()
        val userId = profileStorage.getUserId()
        val rideId = ActiveRide.activeRide?.rideId

        if (token != null && rideId != null && userId != -1) {
            message.authorId = userId
            message.rideId = rideId

            chatRepository.sendMessage(message, token) { isSuccess ->
                if (isSuccess) callback(true)
                else chatRepository.sendMessage(message, token, callback)
            }
        } else callback(false)
    }


    override fun getMessages(callback: DataHandler<ArrayList<Message>?>) {
        val rideId = ActiveRide.activeRide?.rideId
        val userId = profileStorage.getUserId()

        if (rideId != null && userId != -1) {
            chatRepository.getMessages(rideId) { messages, error ->
                if (messages == null || error != null) {
                    //retry request
                    chatRepository.getMessages(rideId) { arrMessages, _ ->
                        if (arrMessages != null) eventMessages(arrMessages, userId, callback)
                        else callback(null, "error")
                    }
                } else eventMessages(messages, userId, callback)
            }
        } else callback(null, "error")
    }


    private fun eventMessages(
        messages: ArrayList<Message>,
        userId: Int,
        callback: DataHandler<ArrayList<Message>?>
    ) {
        messages.forEach { message ->
            if (message.author?.id == userId) message.isSender = true
        }

        callback(messages, null)
    }


    override fun isCheckoutDriver() = profileStorage.isCheckoutDriver()
}