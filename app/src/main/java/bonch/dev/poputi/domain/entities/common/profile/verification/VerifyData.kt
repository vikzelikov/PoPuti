package bonch.dev.poputi.domain.entities.common.profile.verification

import bonch.dev.poputi.domain.entities.common.media.Photo

object VerifyData {
    var idStep: VerifyStep = VerifyStep.SELF_PHOTO_PASSPORT
    var imgUri: String? = null
    var listDocs: ArrayList<Photo> = arrayListOf()
}