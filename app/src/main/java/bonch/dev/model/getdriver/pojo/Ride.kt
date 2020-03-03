package bonch.dev.model.getdriver.pojo

import android.os.Parcel
import android.os.Parcelable

data class Ride(
    var address: String? = null,
    var city: String? = null,
    var uri: String? = null,
    var long: Double? = null,
    var lat: Double? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Double::class.java.classLoader) as? Double,
        parcel.readValue(Double::class.java.classLoader) as? Double
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(address)
        parcel.writeString(city)
        parcel.writeString(uri)
        parcel.writeValue(long)
        parcel.writeValue(lat)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Ride> {
        override fun createFromParcel(parcel: Parcel): Ride {
            return Ride(parcel)
        }

        override fun newArray(size: Int): Array<Ride?> {
            return arrayOfNulls(size)
        }
    }
}
