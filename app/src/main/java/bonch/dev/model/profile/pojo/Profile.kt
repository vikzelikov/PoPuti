package bonch.dev.model.profile.pojo

import io.realm.RealmObject
import io.realm.annotations.RealmClass

@RealmClass
open class Profile(
    var fullName: String? = null,
    var phone: String? = null,
    var email: String? = null,
    var imgUser: ByteArray? = null
) : RealmObject()