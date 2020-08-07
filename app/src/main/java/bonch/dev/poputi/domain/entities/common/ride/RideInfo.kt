package bonch.dev.poputi.domain.entities.common.ride

import android.os.Parcel
import android.os.Parcelable
import bonch.dev.poputi.domain.entities.common.profile.Profile
import bonch.dev.poputi.presentation.modules.driver.getpassenger.presenter.OrdersTimer
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

    @SerializedName("status_id")
    @Expose
    var statusId: Int? = StatusRide.SEARCH.status,

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

    @SerializedName("city")
    @Expose
    var city: String? = null,

    @SerializedName("price")
    @Expose
    var price: Int? = null,

    @SerializedName("user_id")
    @Expose
    var userId: Int? = null,

    val paymentMethod: Int = 0,

    //timer for orders (DriverView)
    var time: Int = OrdersTimer.TIME_EXPIRED_ITEM,

    //distance (driver - passenger) for orders (DriverView)
    var distance: Int? = null,

    //flag for detect that order is new (DriverView)
    var isNewOrder: Boolean = false,

    @SerializedName("passenger")
    @Expose
    val passenger: Profile? = null,

    @SerializedName("driver")
    @Expose
    val driver: Driver? = null
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readValue(Int::class.java.classLoader) as? Int,
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
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readInt(),
        parcel.readInt(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readByte() != 0.toByte(),
        parcel.readParcelable(Profile::class.java.classLoader),
        parcel.readParcelable(Driver::class.java.classLoader)
    )

    override fun equals(other: Any?): Boolean {
        return rideId == (other as RideInfo).rideId
    }


    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(rideId)
        parcel.writeValue(statusId)
        parcel.writeString(position)
        parcel.writeParcelable(fromAdr, flags)
        parcel.writeValue(fromLat)
        parcel.writeValue(fromLng)
        parcel.writeString(destination)
        parcel.writeParcelable(toAdr, flags)
        parcel.writeValue(toLat)
        parcel.writeValue(toLng)
        parcel.writeString(comment)
        parcel.writeString(city)
        parcel.writeValue(price)
        parcel.writeValue(userId)
        parcel.writeInt(paymentMethod)
        parcel.writeInt(time)
        parcel.writeValue(distance)
        parcel.writeByte(if (isNewOrder) 1 else 0)
        parcel.writeParcelable(passenger, flags)
        parcel.writeParcelable(driver, flags)
    }

    override fun hashCode(): Int {
        var result = rideId ?: 0
        result = 31 * result + (statusId ?: 0)
        result = 31 * result + (position?.hashCode() ?: 0)
        result = 31 * result + (fromAdr?.hashCode() ?: 0)
        result = 31 * result + (fromLat?.hashCode() ?: 0)
        result = 31 * result + (fromLng?.hashCode() ?: 0)
        result = 31 * result + (destination?.hashCode() ?: 0)
        result = 31 * result + (toAdr?.hashCode() ?: 0)
        result = 31 * result + (toLat?.hashCode() ?: 0)
        result = 31 * result + (toLng?.hashCode() ?: 0)
        result = 31 * result + (comment?.hashCode() ?: 0)
        result = 31 * result + (city?.hashCode() ?: 0)
        result = 31 * result + (price ?: 0)
        result = 31 * result + (userId ?: 0)
        result = 31 * result + paymentMethod
        result = 31 * result + time
        result = 31 * result + (distance ?: 0)
        result = 31 * result + isNewOrder.hashCode()
        result = 31 * result + (passenger?.hashCode() ?: 0)
        result = 31 * result + (driver?.hashCode() ?: 0)
        return result
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

