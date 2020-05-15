package bonch.dev.domain.entities.passanger.getdriver

import android.os.Parcel
import android.os.Parcelable

data class BankCard(
    var numberCard: String? = null,
    var validUntil: String? = null,
    var cvc: String? = null,
    var img: Int? = null,
    var isSelect: Boolean? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Boolean::class.java.classLoader) as? Boolean
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(numberCard)
        parcel.writeString(validUntil)
        parcel.writeString(cvc)
        parcel.writeValue(img)
        parcel.writeValue(isSelect)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BankCard> {
        override fun createFromParcel(parcel: Parcel): BankCard {
            return BankCard(parcel)
        }

        override fun newArray(size: Int): Array<BankCard?> {
            return arrayOfNulls(size)
        }
    }
}