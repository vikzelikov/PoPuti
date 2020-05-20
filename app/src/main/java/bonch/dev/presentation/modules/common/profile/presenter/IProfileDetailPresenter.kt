package bonch.dev.presentation.modules.common.profile.presenter

import android.app.Activity
import android.widget.EditText
import bonch.dev.domain.entities.common.profile.Profile

interface IProfileDetailPresenter {

    fun getProfileDataDB()

    fun instance(): ProfileDetailPresenter

    fun getCamera(activity: Activity)

    fun isValidEmail(target: String): Boolean

    fun saveProfileData(profileData: Profile?)

    fun showCheckPhoto(activity: Activity, img: String)

    fun listenerEditText(editText: EditText)

    fun saveOldImage()

    fun back(): Boolean

    fun logout()

    fun onDestroy()

}