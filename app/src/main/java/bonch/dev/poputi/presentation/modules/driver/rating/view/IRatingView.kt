package bonch.dev.poputi.presentation.modules.driver.rating.view

import bonch.dev.poputi.domain.entities.common.profile.Profile
import bonch.dev.poputi.presentation.interfaces.IBaseView
import bonch.dev.poputi.presentation.modules.driver.rating.adapter.RatingAdapter

interface IRatingView : IBaseView {

    fun getRating()

    fun showEmptyText()

    fun hideEmptyText()

    fun getAdapter(): RatingAdapter

    fun setProfile(profile: Profile)

}