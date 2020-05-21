package bonch.dev.presentation.modules.common.profile.view

import bonch.dev.domain.entities.common.profile.Profile
import bonch.dev.presentation.interfaces.IBaseView
import java.io.File
import java.io.InputStream

interface IProfileDetailView : IBaseView {

    fun setProfile(profileData: Profile)

    fun getProfile(): Profile

    fun isDataNamesComplete(): Boolean

    fun setDataIntent(isShowPopup: Boolean, profileData: Profile?)

    fun showNotifications(text: String, isPositive: Boolean)

    fun startAnimLoading()

    fun hideLoading()

}