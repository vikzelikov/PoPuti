package bonch.dev.poputi.domain.entities.common.media

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Photo (
    @SerializedName("id")
    @Expose
    var id: Int? = null,

    var imgDocs: String? = null,

    var imgId: Int? = null,

    @SerializedName("name")
    @Expose
    var imgName: String? = null,

    @SerializedName("url")
    @Expose
    var imgUrl: String? = null,

    @SerializedName("verify")
    @Expose
    var isVerify: Int = 0
)