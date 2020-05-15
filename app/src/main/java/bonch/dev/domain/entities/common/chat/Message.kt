package bonch.dev.domain.entities.common.chat

open class Message (
    var textMessage: String? = null,
    var date: String? = null,
    var isSender: Boolean?
)