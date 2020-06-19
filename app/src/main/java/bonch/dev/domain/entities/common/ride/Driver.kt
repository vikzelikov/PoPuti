package bonch.dev.domain.entities.common.ride

import bonch.dev.domain.entities.common.profile.Profile
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.realm.annotations.Ignore

open class Driver(
    @SerializedName("car")
    @Expose
    @Ignore
    var car: Car? = null,

    @SerializedName("driver_location")
    @Expose
    @Ignore
    var driver_location: AddressPoint? = null
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