package bonch.dev.poputi.domain.entities.common.ride

import bonch.dev.poputi.domain.entities.common.profile.Profile
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

open class Driver(
    @SerializedName("car")
    @Expose
    var car: Car? = null,

    @SerializedName("driver_location")
    @Expose
    var driverLocation: AddressPoint? = null
) : Profile()


open class Car(
    @SerializedName("car_brand")
    @Expose
    var name: String? = null,

    @SerializedName("car_model")
    @Expose
    var model: String? = null,

    @SerializedName("car_number")
    @Expose
    var number: String? = null
)