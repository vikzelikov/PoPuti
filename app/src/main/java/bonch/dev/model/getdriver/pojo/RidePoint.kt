package bonch.dev.model.getdriver.pojo

import android.os.Parcel
import android.os.Parcelable


data class RidePoint(
    var latitude: Double = 0.0,
    val longitude: Double = 0.0
) : Parcelable {
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
}