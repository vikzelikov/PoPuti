package bonch.dev.poputi.domain.entities.common.profile.verification

enum class VerifyStep(val step: Int) {
    SELF_PHOTO_PASSPORT(0),
    PASSPORT_ADDRESS_PHOTO(1)
}