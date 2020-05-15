package bonch.dev.data.repository.common.chat

import bonch.dev.domain.entities.common.chat.Message

class ChatRepository : IChatRepository {

    override fun loadMessages(): ArrayList<Message> {
        val list: ArrayList<Message> = arrayListOf()

        for (i in 1..25) {
            if (i % 2 == 0) {
                list.add(
                    Message(
                        "Привет, как дела?",
                        "21:23",
                        true
                    )
                )
            } else {
                list.add(
                    Message(
                        "Привет, как дела?",
                        "21:23",
                        false
                    )
                )
            }
        }

        return arrayListOf()
    }

}