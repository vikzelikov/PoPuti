package bonch.dev.poputi.domain.entities.common.rate

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
    var rating: Int? = null,

    @SerializedName("author")
    @Expose
    var author: Author? = null

)


open class Author(
    @SerializedName("first_name")
    @Expose
    var firstName: String? = null,

    @SerializedName("last_name")
    @Expose
    var lastName: String? = null
)