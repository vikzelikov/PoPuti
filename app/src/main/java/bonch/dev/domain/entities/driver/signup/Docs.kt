package bonch.dev.domain.entities.driver.signup

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Docs (
    @SerializedName("id")
    @Expose
    var id: Int? = null,

    var imgDocs: String? = null,

    @SerializedName("name")
    @Expose
    var imgName: String? = null,

    @SerializedName("url")
    @Expose
    var imgUrl: String? = null,

    @SerializedName("verify")
    @Expose
    var isVerify: Boolean? = null
)