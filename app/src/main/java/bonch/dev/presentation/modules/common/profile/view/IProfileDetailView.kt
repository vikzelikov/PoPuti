package bonch.dev.presentation.modules.common.profile.view

import bonch.dev.domain.entities.common.profile.Profile
import bonch.dev.presentation.interfaces.IBaseView
import java.io.File
import java.io.InputStream

interface IProfileDetailView : IBaseView {

    fun setProfileData(profileData: Profile)

    fun getProfileData(): Profile

    fun isDataNamesComplete(): Boolean

    fun setDataIntent(isShowPopup: Boolean, profileData: Profile?)

    fun showNotifications(isPositive: Boolean)

    fun finishAvtivity()

}