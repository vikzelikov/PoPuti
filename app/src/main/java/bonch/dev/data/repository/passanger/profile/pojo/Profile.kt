package bonch.dev.data.repository.passanger.profile.pojo

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass


open class ProfileData(
    @SerializedName("data")
    @Expose
    val data: Profile
)


@RealmClass
open class Profile(
    @PrimaryKey
    @SerializedName("id")
    @Expose
    var id: Int = 0,

    @SerializedName("first_name")
    @Expose
    var firstName: String? = null,

    @SerializedName("last_name")
    @Expose
    var lastName: String? = null,

    var fullName: String? = null,

    @SerializedName("phone")
    @Expose
    var phone: String? = null,

    @SerializedName("email")
    @Expose
    var email: String? = null,

    @SerializedName("rating")
    @Expose
    var rating: Float? = null,

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
        parcel.readValue(Float::class.java.classLoader) as? Float,
        parcel.readString(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(firstName)
        parcel.writeString(lastName)
        parcel.writeString(fullName)
        parcel.writeString(phone)
        parcel.writeString(email)
        parcel.writeValue(rating)
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
