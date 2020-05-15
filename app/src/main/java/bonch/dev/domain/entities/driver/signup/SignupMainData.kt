package bonch.dev.domain.entities.driver.signup

object SignupMainData {
    var idStep: Step = Step.USER_PHOTO
    var imgUri: String? = null
    var listDocs: ArrayList<Docs> = arrayListOf()
    var driverData: DriverData? = null
}