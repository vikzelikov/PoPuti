package bonch.dev.poputi.domain.entities.driver.signup

import bonch.dev.poputi.domain.entities.common.media.Photo
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

open class DriverData(
    @SerializedName("id")
    @Expose
    var driverId: Int? = null,

    @SerializedName("user_id")
    @Expose
    var userId: Int? = null,

    @SerializedName("verify")
    @Expose
    var isVerify: Boolean = false,

    @SerializedName("car_brand")
    @Expose
    var carName: String? = null,

    @SerializedName("car_model")
    @Expose
    var carModel: String? = null,

    @SerializedName("car_number")
    @Expose
    var carNumber: String? = null,

    @SerializedName("document")
    @Expose
    var docArray: IntArray = intArrayOf(),

    @SerializedName("documents")
    @Expose
    var photoArray: Array<Photo> = arrayOf()
)


open class DriverDataDTO(
    @SerializedName("driver")
    @Expose
    var driver: DriverData? = null
)


open class NewPhoto(
    @SerializedName("document")
    @Expose
    var docArray: IntArray = intArrayOf()
)