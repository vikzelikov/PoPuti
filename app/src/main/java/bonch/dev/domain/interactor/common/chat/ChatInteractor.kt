package bonch.dev.domain.interactor.common.chat

import bonch.dev.data.repository.common.chat.IChatRepository
import bonch.dev.domain.entities.common.chat.Message
import bonch.dev.presentation.modules.common.CommonComponent
import javax.inject.Inject

class ChatInteractor :
    IChatInteractor {

    @Inject
    lateinit var chatRespository: IChatRepository

    init {
        CommonComponent.commonComponent?.inject(this)
    }

    override fun loadMessages(): ArrayList<Message> {
        return chatRespository.loadMessages()
    }

}