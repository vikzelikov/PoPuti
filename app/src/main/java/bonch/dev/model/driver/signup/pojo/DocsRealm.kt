package bonch.dev.model.driver.signup.pojo

import io.realm.RealmObject
import io.realm.annotations.RealmClass

@RealmClass
open class DocsRealm (
    var imgDocs: ByteArray? = null
) : RealmObject()