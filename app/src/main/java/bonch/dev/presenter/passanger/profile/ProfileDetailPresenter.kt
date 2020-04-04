package bonch.dev.presenter.passanger.profile

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.provider.MediaStore
import android.view.View
import bonch.dev.MainActivity
import bonch.dev.Permissions
import bonch.dev.model.passanger.profile.ProfileModel
import bonch.dev.model.passanger.profile.pojo.Profile
import bonch.dev.utils.Constants
import bonch.dev.view.passanger.profile.ProfileDetailView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.yandex.runtime.Runtime.getApplicationContext
import kotlinx.android.synthetic.main.profile_detail_activity.*
import java.io.ByteArrayOutputStream


class ProfileDetailPresenter(val profileDetailView: ProfileDetailView) : IProfilePresenter {

    private var profileModel: ProfileModel? = null
    var imageUri: Uri? = null

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
        val firstName = root.first_name.text.toString().trim()
        val lastName = root.last_name.text.toString().trim()
        val phone = root.phone_number.text.toString().trim()
        val email = root.email.text.toString().trim()
        val imgUser = root.img_user

        if (imageUri != null) {
            profileData.imgUser = imageUri.toString()
        } else {
            println("SAVE")
            val bitmap = (imgUser.drawable as BitmapDrawable).bitmap
            val bytes = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
            val path: String = MediaStore.Images.Media.insertImage(
                profileDetailView.contentResolver,
                bitmap,
                "User image",
                null
            )
            profileData.imgUser = (Uri.parse(path)).toString()
        }

        if (firstName.isNotEmpty()) {
            profileData.fullName = firstName
        }

        if (lastName.isNotEmpty()) {
            profileData.lastName = lastName
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
        //for correct getting camera
        Permissions.access(Constants.STORAGE_PERMISSION_REQUEST, profileDetailView)
    }


    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            val userImg = profileDetailView.img_user

            if (requestCode == Constants.GALLERY) {
                imageUri = data?.data
            }

            Glide.with(profileDetailView.applicationContext).load(imageUri)
                .apply(RequestOptions().centerCrop().circleCrop())
                .into(userImg)

            hideBottomSheet()
        } else {
            imageUri = null
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


    fun getBottomSheet() {
        profileDetailView.loadPhotoBottomSheet!!.state = BottomSheetBehavior.STATE_EXPANDED
    }


    fun hideBottomSheet() {
        profileDetailView.loadPhotoBottomSheet!!.state = BottomSheetBehavior.STATE_COLLAPSED
    }


    fun onSlideBottomSheet(slideOffset: Float) {
        val onMapView = profileDetailView.on_view_profile

        if (slideOffset > 0) {
            onMapView?.alpha = slideOffset * 0.8f
        }
    }


    fun onStateChangedBottomSheet(newState: Int) {
        val onMapView = profileDetailView.on_view_profile

        if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
            onMapView?.visibility = View.GONE
        } else {
            onMapView?.visibility = View.VISIBLE
        }
    }


    fun onDestroy() {
        profileModel?.realm?.close()
    }


    override fun getContext(): Context? {
        return profileDetailView.applicationContext
    }
}