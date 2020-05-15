package bonch.dev.domain.entities.passanger.getdriver

import android.os.Parcel
import android.os.Parcelable
import io.realm.RealmObject
import io.realm.annotations.RealmClass

@RealmClass(name = "address_point")
open class AddressPoint(
    var latitude: Double = 0.0,
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