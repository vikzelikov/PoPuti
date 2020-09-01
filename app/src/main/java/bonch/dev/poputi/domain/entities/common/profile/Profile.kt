package bonch.dev.poputi.domain.entities.common.profile

import bonch.dev.poputi.domain.entities.common.media.Photo
import bonch.dev.poputi.domain.entities.driver.signup.DriverData
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

open class Profile(
    @SerializedName("id")
    @Expose
    var id: Int = 0,

    @SerializedName("first_name")
    @Expose
    var firstName: String? = null,

    @SerializedName("last_name")
    @Expose
    var lastName: String? = null,

    @SerializedName("phone")
    @Expose
    var phone: String? = null,

    @SerializedName("email")
    @Expose
    var email: String? = null,

    @SerializedName("city")
    @Expose
    var city: String? = null,

    @SerializedName("rating")
    @Expose
    var rating: Float? = null,

    @SerializedName("calls_allowed")
    @Expose
    var isCallsEnable: Boolean = false,

    var isNotificationsEnable: Boolean = true,

    var imgUser: String? = null,

    @SerializedName("photos")
    @Expose
    var photos: Array<Photo>? = null,

    @SerializedName("driver")
    @Expose
    var driver: DriverData? = null
)


open class ProfilePhoto(
    @SerializedName("photo")
    @Expose
    var imgId: IntArray? = intArrayOf()
)



object CacheProfile {
    var profile: Profile? = null
}

