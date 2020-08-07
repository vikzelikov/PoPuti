package bonch.dev.poputi.domain.entities.driver.signup

import bonch.dev.poputi.domain.entities.common.media.Photo

object SignupMainData {
    var idStep: Step = Step.USER_PHOTO
    var imgUri: String? = null
    var listDocs: ArrayList<Photo> = arrayListOf()
    var driverData: DriverData? = null
}