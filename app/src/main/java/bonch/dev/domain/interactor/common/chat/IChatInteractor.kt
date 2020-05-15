package bonch.dev.domain.interactor.common.chat

import bonch.dev.domain.entities.common.chat.Message

interface IChatInteractor {

    fun loadMessages(): ArrayList<Message>

}