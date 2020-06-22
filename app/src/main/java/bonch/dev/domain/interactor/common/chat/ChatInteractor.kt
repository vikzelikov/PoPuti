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
                        if (arrMessages != null) {
                            arrMessages.forEach { message ->
                                if (message.author?.id == userId) message.isSender = true

                                try {
                                    val del = message.date?.length?.minus(3)
                                    del?.let {
                                        message.date = message.date?.substring(0, del)
                                    }
                                } catch (ex: StringIndexOutOfBoundsException) {

                                } catch (ex: Exception) {

                                }
                            }

                            callback(arrMessages, null)

                        } else callback(null, "error")
                    }
                } else {
                    messages.forEach { message ->
                        if (message.author?.id == userId) message.isSender = true

                        try {
                            val del = message.date?.length?.minus(3)
                            del?.let {
                                message.date = message.date?.substring(0, del)
                            }
                        } catch (ex: StringIndexOutOfBoundsException) {

                        } catch (ex: Exception) {

                        }
                    }

                    callback(messages, null)
                }
            }
        } else callback(null, "error")
    }


    override fun connectSocket(callback: SuccessHandler) {
        val rideId = ActiveRide.activeRide?.rideId
        val token = profileStorage.getToken()

        if (token != null && rideId != null) {
            chatRepository.connectSocket(rideId, token) { isSuccess ->
                if (isSuccess) {
                    callback(true)
                } else {
                    //retry connect
                    chatRepository.connectSocket(rideId, token, callback)
                }
            }
        } else callback(false)
    }


    override fun subscribeOnMessages(callback: DataHandler<String?>) {
        chatRepository.subscribeOnMessages(callback)
    }


    override fun disconnectSocket() {
        chatRepository.disconnectSocket()
    }

}