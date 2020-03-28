package bonch.dev.presenter.profile

import bonch.dev.utils.Constants
import bonch.dev.utils.Coordinator.openActivity
import bonch.dev.view.profile.ProfileView

class ProfilePresenter(val profileView: ProfileView) {


    fun getFullProfile() {
        val context = profileView.context
        openActivity(Constants.PROFILE_FULL, context!!, profileView)

    }


    fun checkoutToDriver() {

    }

}