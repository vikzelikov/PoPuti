package bonch.dev.data.repository.common.chat

import bonch.dev.domain.entities.common.chat.Message

interface IChatRepository {

    fun loadMessages(): ArrayList<Message>

}