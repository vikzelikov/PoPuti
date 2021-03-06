package bonch.dev.poputi.domain.entities.passenger.signup

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Token (
    @SerializedName("access_token")
    @Expose
    val accessToken: String? = null
)