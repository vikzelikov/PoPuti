package bonch.dev.model.driver.signup

import android.net.Uri
import bonch.dev.model.driver.signup.pojo.DocsPhoto

object SignupMainData {
    var idStep: Int = 0
    var isTableView = false
    var imgUri: Uri? = null
    var listDocs: ArrayList<DocsPhoto> = arrayListOf()
}