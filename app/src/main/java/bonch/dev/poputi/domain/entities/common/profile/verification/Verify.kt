package bonch.dev.poputi.domain.entities.common.profile.verification

import bonch.dev.poputi.domain.entities.common.media.Photo
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

open class Verify(
    @SerializedName("user_id")
    @Expose
    var userId: Int? = null,

    @SerializedName("verify")
    @Expose
    var isVerify: Boolean = false,

    @SerializedName("document")
    @Expose
    var docArray: IntArray = intArrayOf(),

    @SerializedName("documents")
    @Expose
    var photoArray: Array<Photo> = arrayOf()
)


open class VerifyDTO(
    @SerializedName("verify")
    @Expose
    var verify: Verify? = null
)


open class NewPhoto(
    @SerializedName("document")
    @Expose
    var docArray: IntArray = intArrayOf()
)