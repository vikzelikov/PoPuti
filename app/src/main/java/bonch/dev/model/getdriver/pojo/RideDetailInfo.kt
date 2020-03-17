package bonch.dev.model.getdriver.pojo

import android.os.Parcel
import android.os.Parcelable

data class RideDetailInfo(
    var priceRide: String? = null,
    val comment: String? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(priceRide)
        parcel.writeString(comment)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<RideDetailInfo> {
        override fun createFromParcel(parcel: Parcel): RideDetailInfo {
            return RideDetailInfo(parcel)
        }

        override fun newArray(size: Int): Array<RideDetailInfo?> {
            return arrayOfNulls(size)
        }
    }
}