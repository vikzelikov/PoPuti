package bonch.dev.presenter.passanger.profile

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.view.View
import androidx.core.content.ContextCompat
import bonch.dev.MainActivity
import bonch.dev.R
import bonch.dev.model.passanger.profile.ProfileModel
import bonch.dev.model.passanger.profile.pojo.Profile
import bonch.dev.utils.Camera
import bonch.dev.utils.Constants
import bonch.dev.view.passanger.profile.ProfileDetailView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.yandex.runtime.Runtime.getApplicationContext
import kotlinx.android.synthetic.main.profile_detail_activity.*
import java.io.ByteArrayOutputStream


class ProfileDetailPresenter(val profileDetailView: ProfileDetailView) : IProfilePresenter {

    private var profileModel: ProfileModel? = null
    private var imageUri: Uri? = null

    init {
        if (profileModel == null) {
            profileModel = ProfileModel(this)
        }
    }


    fun getProfileDataDB(root: View) {
        profileModel?.initRealm()

        val profileData = profileModel?.getProfileData()

        profileData?.let {
            profileDetailView.setProfileData(root, it)
        }
    }


    fun getProfileData(): Profile {
        val profileData = Profile()
        val root = profileDetailView
        val fullName = root.full_name.text.toString().trim()
        val phone = root.phone_number.text.toString().trim()
        val email = root.email.text.toString().trim()
        val userImg = root.img_user.drawable

        if (userImg != ContextCompat.getDrawable(profileDetailView, R.drawable.ava)) {
            val bitmap = (userImg as BitmapDrawable).bitmap
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream)
            val byteArray: ByteArray = stream.toByteArray()
            profileData.imgUser = byteArray
        }

        if (fullName.isNotEmpty()) {
            profileData.fullName = fullName
        }

        if (phone.isNotEmpty()) {
            profileData.phone = phone
        }

        if (email.isNotEmpty()) {
            profileData.email = email
        }

        return profileData
    }


    fun saveProfileData(profileData: Profile?) {
        if (profileData != null) {
            profileModel?.saveProfileData(profileData)
        }
    }


    fun getCamera() {
        imageUri = Camera.getCamera(profileDetailView)
    }


    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val userImg = profileDetailView.img_user
        when (requestCode) {
            Constants.CAMERA -> {
                if(resultCode == Activity.RESULT_OK){
                    Glide.with(profileDetailView.applicationContext).load(imageUri)
                        .apply(RequestOptions().centerCrop().circleCrop())
                        .into(userImg)
                }
            }

            Constants.GALLERY -> {
                //TODO
            }
        }
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
        val mgr = getApplicationContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        mgr[AlarmManager.RTC, System.currentTimeMillis() + 100] = mPendingIntent

        profileDetailView.finishAffinity()
    }


    fun onDestroy() {
        profileModel?.realm?.close()
    }


    override fun getContext(): Context? {
        return profileDetailView.applicationContext
    }
}