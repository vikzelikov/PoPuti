package bonch.dev.model.driver.signup.pojo

import android.net.Uri

data class DocsPhoto (
    var imgUri: Uri? = null,
    var isRemake: Boolean = false
)