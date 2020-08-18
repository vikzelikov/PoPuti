package bonch.dev.poputi.domain.entities.passenger.regular.ride

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

open class Schedule(
    @SerializedName("schedule")
    @Expose
    var dateInfo: DateInfo? = null
)


open class DateInfo(
    @SerializedName("monday")
    @Expose
    var monday: Boolean = false,

    @SerializedName("tuesday")
    @Expose
    var tuesday: Boolean = false,

    @SerializedName("wednesday")
    @Expose
    var wednesday: Boolean = false,

    @SerializedName("thursday")
    @Expose
    var thursday: Boolean = false,

    @SerializedName("friday")
    @Expose
    var friday: Boolean = false,

    @SerializedName("saturday")
    @Expose
    var saturday: Boolean = false,

    @SerializedName("sunday")
    @Expose
    var sunday: Boolean = false,

    @SerializedName("time")
    @Expose
    var time: String? = null,

    @SerializedName("ride_id")
    @Expose
    var rideId: Int? = null,

    @SerializedName("id")
    @Expose
    var id: Int? = null
)