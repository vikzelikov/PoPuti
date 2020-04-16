package bonch.dev.model.passanger.getdriver

import bonch.dev.model.passanger.getdriver.pojo.Message

class ChatModel {

    fun loadMessages(): ArrayList<Message> {
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

        return list
    }




}