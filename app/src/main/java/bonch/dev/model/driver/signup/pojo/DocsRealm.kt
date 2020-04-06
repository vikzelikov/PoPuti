package bonch.dev.model.driver.signup.pojo

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass

@RealmClass
open class DocsRealm (
    @PrimaryKey
    var id: Int = 0,
    var imgDocs: String? = null,
    var isAccess: Boolean? = null
) : RealmObject()