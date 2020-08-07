package bonch.dev.poputi.domain.entities.driver.signup

import bonch.dev.poputi.R

data class SignupStep(
    var title: String? = null,
    var subtitle: String? = null,
    var descriptionDocs: String? = null,
    var imgDocs: Int = R.drawable.ic_photo
)

enum class Step(val step: Int) {
    //we not send to server user photo
    USER_PHOTO(-1),
    PASSPORT_PHOTO(0),
    SELF_PHOTO_PASSPORT(1),
    PASSPORT_ADDRESS_PHOTO(2),
    DRIVER_DOC_FRONT(3),
    DRIVER_DOC_BACK(4),
    STS_DOC_FRONT(5),
    STS_DOC_BACK(6),
}