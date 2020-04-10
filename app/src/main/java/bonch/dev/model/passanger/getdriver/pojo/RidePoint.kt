package bonch.dev.model.passanger.getdriver.pojo

import android.os.Parcel
import android.os.Parcelable
import io.realm.RealmObject
import io.realm.annotations.RealmClass

@RealmClass
open class RidePoint(
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

    companion object CREATOR : Parcelable.Creator<RidePoint> {
        override fun createFromParcel(parcel: Parcel): RidePoint {
            return RidePoint(parcel)
        }

        override fun newArray(size: Int): Array<RidePoint?> {
            return arrayOfNulls(size)
        }
    }

    override fun equals(other: Any?): Boolean {
        return (latitude == (other as RidePoint).latitude && longitude == (other).longitude)
    }

    override fun hashCode(): Int {
        var result = latitude.hashCode()
        result = 31 * result + longitude.hashCode()
        return result
    }
}