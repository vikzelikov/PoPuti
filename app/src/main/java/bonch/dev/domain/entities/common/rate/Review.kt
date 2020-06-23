package bonch.dev.domain.entities.common.rate

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

open class Review(

    @SerializedName("from_id")
    @Expose
    var fromId: Int? = null,

    @SerializedName("to_id")
    @Expose
    var toId: Int? = null,

    @SerializedName("text")
    @Expose
    var text: String? = null,

    @SerializedName("rating")
    @Expose
    var rating: Int? = null

)