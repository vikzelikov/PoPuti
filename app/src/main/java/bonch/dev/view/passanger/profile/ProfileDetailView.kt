package bonch.dev.view.passanger.profile

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import bonch.dev.Permissions
import bonch.dev.R
import bonch.dev.model.passanger.profile.pojo.Profile
import bonch.dev.presenter.passanger.profile.ProfileDetailPresenter
import bonch.dev.utils.Camera
import bonch.dev.utils.Constants
import bonch.dev.utils.Keyboard
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.profile_detail_activity.view.*


class ProfileDetailView : AppCompatActivity() {

    private var profileDetailPresenter: ProfileDetailPresenter? = null

    init {
        if (profileDetailPresenter == null) {
            profileDetailPresenter = ProfileDetailPresenter(this)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile_detail_activity)

        val root = findViewById<View>(R.id.rootLinearLayout)

        profileDetailPresenter?.getProfileDataDB(root)

        setListeners(root)
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(Permissions.isAccess(Constants.STORAGE_PERMISSION, this)){
            profileDetailPresenter?.imageUri = Camera.getCamera(this)
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    fun setProfileData(root: View, profileData: Profile) {
        val fullName = root.full_name
        val nameUser = root.name_user
        val phone = root.phone_number
        val email = root.email
        val userImg = root.img_user

        if (profileData.imgUser != null) {
            val bitmap =
                BitmapFactory.decodeByteArray(profileData.imgUser, 0, profileData.imgUser!!.size)
            Glide.with(applicationContext).load(bitmap)
                .apply(RequestOptions().centerCrop().circleCrop())
                .into(userImg)
        }
        nameUser.text = profileData.fullName

        fullName.setText(profileData.fullName)
        phone.setText(profileData.phone)
        email.setText(profileData.email)
    }


    private fun setListeners(root: View) {
        val userImg = root.img_user
        val logout = root.logout
        val backBtn = root.back_btn

        userImg.setOnClickListener {
            profileDetailPresenter?.getCamera()
        }

        backBtn.setOnClickListener {
            //save data
            val profileData = profileDetailPresenter?.getProfileData()
            profileDetailPresenter?.saveProfileData(profileData)

            setDataIntent(profileData)

            Keyboard.hideKeyboard(this, root)
            finish()
        }

        logout.setOnClickListener {
            profileDetailPresenter?.logout()
        }
    }


    private fun setDataIntent(profileData: Profile?){
        val intent = Intent()
        intent.putExtra(Constants.PROFILE_DATA, profileData)
        setResult(RESULT_OK, intent)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        profileDetailPresenter?.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }


    override fun onBackPressed() {
        val profileData = profileDetailPresenter?.getProfileData()
        profileDetailPresenter?.saveProfileData(profileData)

        setDataIntent(profileData)

        super.onBackPressed()
    }


    override fun onDestroy() {
        profileDetailPresenter?.onDestroy()
        super.onDestroy()
    }
}
