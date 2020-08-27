package bonch.dev.poputi.presentation.modules.driver.rating.presenter

interface IRatingPresenter {

    fun getRating()

    fun getProfile()

    fun instance(): RatingPresenter

}