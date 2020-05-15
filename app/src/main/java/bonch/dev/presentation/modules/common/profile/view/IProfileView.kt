package bonch.dev.presentation.modules.common.profile.view

import bonch.dev.domain.entities.common.profile.Profile
import bonch.dev.presentation.interfaces.IBaseView

interface IProfileView : IBaseView {

    fun setProfileData(profileData: Profile?)

    fun showNotification(text: String)

    fun isPassanger(): Boolean

}