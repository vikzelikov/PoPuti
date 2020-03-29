package bonch.dev.model.profile.pojo

import android.os.Parcel
import android.os.Parcelable
import io.realm.RealmObject
import io.realm.annotations.RealmClass

@RealmClass
open class Profile(
    var firstName: String? = null,
    var lastName: String? = null,
    var fullName: String? = null,
    var phone: String? = null,
    var email: String? = null,
    var imgUser: ByteArray? = null
) : RealmObject(), Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.createByteArray()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(firstName)
        parcel.writeString(lastName)
        parcel.writeString(fullName)
        parcel.writeString(phone)
        parcel.writeString(email)
        parcel.writeByteArray(imgUser)
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
