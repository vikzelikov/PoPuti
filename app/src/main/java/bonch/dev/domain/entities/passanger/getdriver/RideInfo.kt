package bonch.dev.domain.entities.passanger.getdriver

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


open class Ride(
    @SerializedName("ride")
    @Expose
    val ride: RideInfo
)


data class RideInfo(
    @SerializedName("id")
    @Expose
    var rideId: Int? = null,

    @SerializedName("position")
    @Expose
    var position: String? = null,

    var fromAdr: Address? = null,

    @SerializedName("from_lat")
    @Expose
    var fromLat: Double? = null,

    @SerializedName("from_lng")
    @Expose
    var fromLng: Double? = null,

    @SerializedName("destination")
    @Expose
    var destination: String? = null,

    var toAdr: Address? = null,

    @SerializedName("to_lat")
    @Expose
    var toLat: Double? = null,

    @SerializedName("to_lng")
    @Expose
    var toLng: Double? = null,

    @SerializedName("comment")
    @Expose
    var comment: String? = null,

    @SerializedName("price")
    @Expose
    var price: Int? = null,

    @SerializedName("user_id")
    @Expose
    var userId: Int? = null,

    val paymentMethod: Int = 0

) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readParcelable(Address::class.java.classLoader),
        parcel.readValue(Double::class.java.classLoader) as? Double,
        parcel.readValue(Double::class.java.classLoader) as? Double,
        parcel.readString(),
        parcel.readParcelable(Address::class.java.classLoader),
        parcel.readValue(Double::class.java.classLoader) as? Double,
        parcel.readValue(Double::class.java.classLoader) as? Double,
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(rideId)
        parcel.writeString(position)
        parcel.writeParcelable(fromAdr, flags)
        parcel.writeValue(fromLat)
        parcel.writeValue(fromLng)
        parcel.writeString(destination)
        parcel.writeParcelable(toAdr, flags)
        parcel.writeValue(toLat)
        parcel.writeValue(toLng)
        parcel.writeString(comment)
        parcel.writeValue(price)
        parcel.writeValue(userId)
        parcel.writeInt(paymentMethod)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<RideInfo> {
        override fun createFromParcel(parcel: Parcel): RideInfo {
            return RideInfo(parcel)
        }

        override fun newArray(size: Int): Array<RideInfo?> {
            return arrayOfNulls(size)
        }
    }
}