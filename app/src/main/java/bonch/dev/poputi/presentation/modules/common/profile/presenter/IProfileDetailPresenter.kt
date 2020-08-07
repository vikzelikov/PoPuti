package bonch.dev.poputi.presentation.modules.common.profile.presenter

import android.app.Activity
import android.widget.EditText
import bonch.dev.poputi.domain.entities.common.profile.Profile

interface IProfileDetailPresenter {

    fun getProfile()

    fun saveProfile(profileData: Profile?)

    fun instance(): ProfileDetailPresenter

    fun getCamera(activity: Activity)

    fun isValidEmail(target: String): Boolean

    fun showCheckPhoto(activity: Activity, img: String)

    fun listenerEditText(editText: EditText)

    fun back(): Boolean

    fun logout()

    fun onDestroy()

}