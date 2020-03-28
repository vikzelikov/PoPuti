package bonch.dev.presenter.profile

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.view.View
import bonch.dev.MainActivity
import bonch.dev.model.profile.ProfileModel
import bonch.dev.model.profile.pojo.Profile
import bonch.dev.view.profile.ProfileDetailView
import com.yandex.runtime.Runtime.getApplicationContext
import kotlinx.android.synthetic.main.profile_detail_activity.*
import kotlin.system.exitProcess


class ProfileDetailPresenter(val profileDetailView: ProfileDetailView) {

    private var profileModel: ProfileModel? = null

    init {
        if (profileModel == null) {
            profileModel = ProfileModel(this)
        }
    }


    fun getProfileData(root: View) {
        val profileData = profileModel?.getProfileData()

        profileData?.let {
            profileDetailView.setProfileData(root, it)
        }
    }


    fun saveProfileData() {
        val profileData = Profile()
        val root = profileDetailView
        val fullName = root.full_name.text.toString().trim()
        var phone = root.phone_number.text.toString().trim()
        val email = root.email.text.toString().trim()

        if (fullName.isNotEmpty()) {
            profileData.fullName = fullName
        }

        if (phone.isNotEmpty()) {
            //check on russian phone number
            phone = phone.substring(2, phone.length)
            profileData.phone = phone
        }

        if (email.isNotEmpty()) {
            profileData.email = email
        }

        profileModel?.saveProfileData(profileData)
    }


    fun logout() {
        //clear data and close app
        profileModel?.removeAccessToken()

        val intent = Intent(getApplicationContext(), MainActivity::class.java)
        val mPendingIntent = PendingIntent.getActivity(
            getApplicationContext(),
            1,
            intent,
            PendingIntent.FLAG_CANCEL_CURRENT
        )
        val mgr =  getApplicationContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        mgr[AlarmManager.RTC, System.currentTimeMillis() + 100] = mPendingIntent

        profileDetailView.finishAffinity()
    }
}