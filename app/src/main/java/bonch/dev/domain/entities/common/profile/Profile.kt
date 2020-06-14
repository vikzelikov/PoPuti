package bonch.dev.domain.entities.common.profile

import android.os.Parcel
import android.os.Parcelable
import bonch.dev.domain.entities.common.media.Photo
import bonch.dev.domain.entities.driver.signup.DriverData
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.Ignore
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

    @SerializedName("phone")
    @Expose
    var phone: String? = null,

    @SerializedName("email")
    @Expose
    var email: String? = null,

    @SerializedName("rating")
    @Expose
    var rating: Float? = null,

    var isNotificationsEnable: Boolean = false,

    var isCallsEnable: Boolean = false,

    var imgUser: String? = null,

    @Ignore
    @SerializedName("photos")
    @Expose
    var photos: Array<Photo>? = null,

    @Ignore
    @SerializedName("driver")
    @Expose
    var driver: DriverData? = null

) : RealmObject(), Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Float::class.java.classLoader) as? Float,
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readString()
    )
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(firstName)
        parcel.writeString(lastName)
        parcel.writeString(phone)
        parcel.writeString(email)
        parcel.writeValue(rating)
        parcel.writeByte(if (isNotificationsEnable) 1 else 0)
        parcel.writeByte(if (isCallsEnable) 1 else 0)
        parcel.writeString(imgUser)
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


open class ProfilePhoto(
    @Ignore
    @SerializedName("photo")
    @Expose
    var imgId: IntArray? = intArrayOf()
)
