package bonch.dev.poputi.domain.entities.common.media

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

open class Media(
    @SerializedName("media")
    @Expose
    val media: MediaObject
)

open class MediaObject(
    @SerializedName("id")
    @Expose
    var id: Int = 0,

    @SerializedName("name")
    @Expose
    var name: String? = null
)