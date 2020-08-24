package bonch.dev.poputi.presentation.modules.common.profile.me.view

import bonch.dev.poputi.domain.entities.common.profile.Profile
import bonch.dev.poputi.presentation.interfaces.IBaseView

interface IProfileDetailView : IBaseView {

    fun setProfile(profileData: Profile)

    fun getProfile(): Profile

    fun isDataNamesComplete(): Boolean

    fun showErrorNotification()

    fun setDataIntent(isShowPopup: Boolean, profileData: Profile?)

}