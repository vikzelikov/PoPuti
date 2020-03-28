package bonch.dev.view.profile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import bonch.dev.R
import bonch.dev.model.profile.pojo.Profile
import bonch.dev.presenter.profile.ProfileDetailPresenter
import bonch.dev.utils.Keyboard
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

        profileDetailPresenter?.getProfileData(root)

        setListeners(root)
    }


    fun setProfileData(root: View, profileData: Profile) {
        val fullName = root.full_name
        val nameUser = root.name_user
        val phone = root.phone_number
        val email = root.email

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
            profileDetailPresenter?.saveProfileData()

            Keyboard.hideKeyboard(this, root)
            finish()
        }

        logout.setOnClickListener {
            profileDetailPresenter?.logout()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        profileDetailPresenter?.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }


    override fun onBackPressed() {
        profileDetailPresenter?.saveProfileData()
        super.onBackPressed()
    }
}
