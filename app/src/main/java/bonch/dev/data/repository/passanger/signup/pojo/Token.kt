package bonch.dev.data.repository.passanger.signup.pojo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Token (
    @SerializedName("access_token")
    @Expose
    val accessToken: String? = null
)