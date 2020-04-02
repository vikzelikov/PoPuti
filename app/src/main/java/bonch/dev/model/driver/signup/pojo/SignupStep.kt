package bonch.dev.model.driver.signup.pojo

import android.net.Uri

object SignupStep {
    var idStep: Int = 0
    var isTableView = false
    var imgUri: Uri? = null
    var listDocs: ArrayList<Uri> = arrayListOf()
}