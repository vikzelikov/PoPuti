package bonch.dev.model.passanger.profile.pojo

import android.os.Parcel
import android.os.Parcelable
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass

@RealmClass
open class Profile(
    @PrimaryKey
    var id: Int = 0,
    var firstName: String? = null,
    var lastName: String? = null,
    var fullName: String? = null,
    var phone: String? = null,
    var email: String? = null,
    var imgUser: String? = null,
    var isNotificationsEnable: Boolean = false,
    var isCallsEnable: Boolean = false
) : RealmObject(), Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(firstName)
        parcel.writeString(lastName)
        parcel.writeString(fullName)
        parcel.writeString(phone)
        parcel.writeString(email)
        parcel.writeString(imgUser)
        parcel.writeByte(if (isNotificationsEnable) 1 else 0)
        parcel.writeByte(if (isCallsEnable) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Profile> {
        override fun createFromParcel(parcel: Parcel): Profile {
            return Profile(parcel)
        }

        override fun newArray(size: Int): Array<Profile?> {
            return arrayOfNulls(size)
        }
    }
}
