package bonch.dev.poputi.domain.entities.common.ride

import android.os.Parcel
import android.os.Parcelable
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass

@RealmClass (name = "Address")
open class Address(
    @PrimaryKey
    var id: Int = 0,
    var address: String? = null,
    var description: String? = null,
    var uri: String? = null,
    var point: AddressPoint? = null,
    var isCashed: Boolean = false
) : RealmObject(), Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readParcelable(AddressPoint::class.java.classLoader),
        parcel.readByte() != 0.toByte()
    )


    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(address)
        parcel.writeString(description)
        parcel.writeString(uri)
        parcel.writeParcelable(point, flags)
        parcel.writeByte(if (isCashed) 1 else 0)
    }


    override fun describeContents(): Int {
        return 0
    }


    companion object CREATOR : Parcelable.Creator<Address> {
        override fun createFromParcel(parcel: Parcel): Address {
            return Address(parcel)
        }

        override fun newArray(size: Int): Array<Address?> {
            return arrayOfNulls(size)
        }
    }


    override fun equals(other: Any?): Boolean {
        return (address == (other as Address).address
                && uri == (other).uri
                && description == (other).description)
    }


    override fun hashCode(): Int {
        var result = id
        result = 31 * result + (address?.hashCode() ?: 0)
        result = 31 * result + (description?.hashCode() ?: 0)
        result = 31 * result + (uri?.hashCode() ?: 0)
        result = 31 * result + (point?.hashCode() ?: 0)
        result = 31 * result + isCashed.hashCode()
        return result
    }
}