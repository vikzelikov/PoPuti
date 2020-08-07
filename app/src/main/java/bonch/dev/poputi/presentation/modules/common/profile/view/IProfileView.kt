package bonch.dev.poputi.presentation.modules.common.profile.view

import bonch.dev.poputi.domain.entities.common.profile.Profile
import bonch.dev.poputi.presentation.interfaces.IBaseView

interface IProfileView : IBaseView {

    fun setProfile(profileData: Profile)

    fun isPassanger(): Boolean

}