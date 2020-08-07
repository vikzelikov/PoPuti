package bonch.dev.poputi.presentation.modules.driver.signup.tabledocs.presenter

import bonch.dev.poputi.App
import bonch.dev.poputi.R
import bonch.dev.poputi.domain.entities.driver.signup.Step

object TitlePhoto {

    fun getTitleForCheckPhoto(idStep: Step?): String? {
        var title: String? = null
        val res = App.appComponent.getContext().resources

        when (idStep) {
            Step.USER_PHOTO -> {
                title = res.getString(R.string.checkUserPhoto)
            }

            Step.PASSPORT_PHOTO -> {
                title = res.getString(R.string.checkPassport)
            }

            Step.SELF_PHOTO_PASSPORT -> {
                title = res.getString(R.string.checkSelfPassport)
            }

            Step.PASSPORT_ADDRESS_PHOTO -> {
                title = res.getString(R.string.checkPassportAddress)
            }

            Step.DRIVER_DOC_FRONT -> {
                title = res.getString(R.string.checkDriverDocFront)
            }

            Step.DRIVER_DOC_BACK -> {
                title = res.getString(R.string.checkDriverDocBack)
            }

            Step.STS_DOC_FRONT -> {
                title = res.getString(R.string.checkStsDocFront)
            }

            Step.STS_DOC_BACK -> {
                title = res.getString(R.string.checkStsDocBack)
            }
        }

        return title
    }


    fun getTitlePhoto(idStep: Step?): String? {
        var title: String? = null
        val res = App.appComponent.getContext().resources

        when (idStep) {
            Step.USER_PHOTO -> {
                title = res.getString(R.string.userPhoto)
            }

            Step.PASSPORT_PHOTO -> {
                title = res.getString(R.string.passportPhoto)
            }

            Step.SELF_PHOTO_PASSPORT -> {
                title = res.getString(R.string.self_passport)
            }

            Step.PASSPORT_ADDRESS_PHOTO -> {
                title = res.getString(R.string.passportaddress)
            }

            Step.DRIVER_DOC_FRONT -> {
                title = res.getString(R.string.driverLicenseFront)
            }

            Step.DRIVER_DOC_BACK -> {
                title = res.getString(R.string.driverLicenseBack)
            }

            Step.STS_DOC_FRONT -> {
                title = res.getString(R.string.stsFront)
            }

            Step.STS_DOC_BACK -> {
                title = res.getString(R.string.stsBack)
            }
        }

        return title
    }


    fun getStepByTitle(title: String): Step {
        var step = Step.USER_PHOTO
        val res = App.appComponent.getContext().resources

        when (title) {
            res.getString(R.string.userPhoto) -> {
                step = Step.USER_PHOTO
            }

            res.getString(R.string.passportPhoto) -> {
                step = Step.PASSPORT_PHOTO
            }

            res.getString(R.string.self_passport) -> {
                step = Step.SELF_PHOTO_PASSPORT
            }

            res.getString(R.string.passportaddress) -> {
                step = Step.PASSPORT_ADDRESS_PHOTO
            }

            res.getString(R.string.driverLicenseFront) -> {
                step = Step.DRIVER_DOC_FRONT
            }

            res.getString(R.string.driverLicenseBack) -> {
                step = Step.DRIVER_DOC_BACK
            }

            res.getString(R.string.stsFront) -> {
                step = Step.STS_DOC_FRONT
            }

            res.getString(R.string.stsBack) -> {
                step = Step.STS_DOC_BACK
            }
        }

        return step
    }

}