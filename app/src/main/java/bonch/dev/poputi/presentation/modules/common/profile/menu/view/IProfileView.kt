package bonch.dev.poputi.presentation.modules.common.profile.menu.view

import bonch.dev.poputi.domain.entities.common.profile.Profile
import bonch.dev.poputi.presentation.interfaces.IBaseView

interface IProfileView : IBaseView {

    fun setProfile(profileData: Profile)

    fun isPassanger(): Boolean

    fun setMyCity(address: String)

    fun stopSearchOrders()

    fun showModerateIcon()

}