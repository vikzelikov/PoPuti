package bonch.dev.presenter.profile

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.view.View
import androidx.core.content.ContextCompat
import bonch.dev.MainActivity
import bonch.dev.R
import bonch.dev.model.profile.ProfileModel
import bonch.dev.model.profile.pojo.Profile
import bonch.dev.utils.Camera
import bonch.dev.utils.Constants
import bonch.dev.view.profile.ProfileDetailView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.yandex.runtime.Runtime.getApplicationContext
import kotlinx.android.synthetic.main.profile_detail_activity.*
import java.io.ByteArrayOutputStream


class ProfileDetailPresenter(val profileDetailView: ProfileDetailView) {

    private var profileModel: ProfileModel? = null
    private var imageUri: Uri? = null

    init {
        if (profileModel == null) {
            profileModel = ProfileModel(this)
        }
    }


    fun getProfileData(root: View) {
        profileModel?.initRealm()

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
            //substring mask phone number
            phone = phone.substring(2, phone.length)
            profileData.phone = phone
        }

        if (email.isNotEmpty()) {
            profileData.email = email
        }

        profileModel?.saveProfileData(profileData)
    }


    fun getCamera() {
        imageUri = Camera.getCamera(profileDetailView)
    }


    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val userImg = profileDetailView.img_user
        when (requestCode) {
            Constants.CAMERA -> {
                Glide.with(profileDetailView.applicationContext).load(imageUri)
                    .apply(RequestOptions().centerCrop().circleCrop())
                    .into(userImg)
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
}