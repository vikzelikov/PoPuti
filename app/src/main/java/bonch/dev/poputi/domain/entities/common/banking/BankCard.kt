package bonch.dev.poputi.domain.entities.common.banking

import android.os.Parcel
import android.os.Parcelable
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass

@RealmClass
open class BankCard(
    @PrimaryKey
    var id: Int = 0,
    var numberCard: String? = null,
    var validUntil: String? = null,
    var cvc: String? = null,
    var isSelect: Boolean = false
) : RealmObject(), Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readByte() != 0.toByte()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(numberCard)
        parcel.writeString(validUntil)
        parcel.writeString(cvc)
        parcel.writeByte(if (isSelect) 1 else 0)
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


    override fun equals(other: Any?): Boolean {
        return id == (other as BankCard).id
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + (numberCard?.hashCode() ?: 0)
        result = 31 * result + (validUntil?.hashCode() ?: 0)
        result = 31 * result + (cvc?.hashCode() ?: 0)
        result = 31 * result + isSelect.hashCode()
        return result
    }
}
