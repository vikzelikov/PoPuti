package bonch.dev.poputi.domain.entities.passenger.regulardrive

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.*

open class DateInfo(
    @SerializedName("monday")
    @Expose
    var monday: Int? = null,

    @SerializedName("tuesday")
    @Expose
    var tuesday: Int? = null,

    @SerializedName("wednesday")
    @Expose
    var wednesday: Int? = null,

    @SerializedName("thursday")
    @Expose
    var thursday: Int? = null,

    @SerializedName("friday")
    @Expose
    var friday: Int? = null,

    @SerializedName("saturday")
    @Expose
    var saturday: Int? = null,

    @SerializedName("sunday")
    @Expose
    var sunday: Int? = null,

    @SerializedName("time")
    @Expose
    var date: Date? = null,

    @SerializedName("ride_id")
    @Expose
    var rideId: Int? = null
)