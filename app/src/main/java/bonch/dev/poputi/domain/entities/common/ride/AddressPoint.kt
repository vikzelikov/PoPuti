package bonch.dev.poputi.domain.entities.common.ride

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.RealmClass

@RealmClass(name = "address_point")
open class AddressPoint(
    @SerializedName("latitude")
    @Expose
    var latitude: Double = 0.0,

    @SerializedName("longitude")
    @Expose
    var longitude: Double = 0.0

) : RealmObject(), Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readDouble(),
        parcel.readDouble()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeDouble(latitude)
        parcel.writeDouble(longitude)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AddressPoint> {
        override fun createFromParcel(parcel: Parcel): AddressPoint {
            return AddressPoint(parcel)
        }

        override fun newArray(size: Int): Array<AddressPoint?> {
            return arrayOfNulls(size)
        }
    }

    override fun equals(other: Any?): Boolean {
        return (latitude == (other as AddressPoint).latitude && longitude == (other).longitude)
    }

    override fun hashCode(): Int {
        var result = latitude.hashCode()
        result = 31 * result + longitude.hashCode()
        return result
    }
}


open class Location(
    @SerializedName("location")
    @Expose
    val location: AddressPoint
)