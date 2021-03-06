package bonch.dev.poputi.domain.entities.common.chat

import bonch.dev.poputi.domain.entities.common.profile.Profile
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


open class MessageObject(
    @SerializedName("message")
    @Expose
    var message: Message? = null
)

open class Message(
    @SerializedName("author_id")
    @Expose
    var authorId: Int? = null,

    @SerializedName("ride_id")
    @Expose
    var rideId: Int? = null,

    @SerializedName("text")
    @Expose
    var text: String? = null,

    @SerializedName("created_at")
    @Expose
    var date: String? = null,

    var isSender: Boolean = false,

    var isSuccess: Boolean = true,

    @SerializedName("author")
    @Expose
    var author: Profile? = null
)