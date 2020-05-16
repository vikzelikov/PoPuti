package bonch.dev.presentation.modules.common.profile.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import bonch.dev.Permissions
import bonch.dev.R
import bonch.dev.domain.entities.common.profile.Profile
import bonch.dev.domain.utils.Camera
import bonch.dev.domain.utils.Constants
import bonch.dev.domain.utils.Gallery
import bonch.dev.domain.utils.Keyboard
import bonch.dev.presentation.modules.common.profile.ProfileComponent
import bonch.dev.presentation.modules.common.profile.presenter.IProfileDetailPresenter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.profile_detail_activity.*
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import javax.inject.Inject


class ProfileDetailView : AppCompatActivity(), IProfileDetailView {

    @Inject
    lateinit var profileDetailPresenter: IProfileDetailPresenter

    private var loadPhotoBottomSheet: BottomSheetBehavior<*>? = null
    private var errorBottomSheet: BottomSheetBehavior<*>? = null
    private var handlerAnimation: Handler? = null

    private val PROFILE_CHECK_PHOTO = 12
    private val IS_SHOW_POPUP = "IS_SHOW_POPUP"
    private val PROFILE_DATA = "PROFILE_DATA"
    private val EXIT = -2



    init {
        ProfileComponent.profileComponent?.inject(this)

        profileDetailPresenter.instance().attachView(this)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile_detail_activity)

        profileDetailPresenter.getProfileDataDB()

        setBottomSheet()

        setListeners()
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (Permissions.isAccess(Permissions.STORAGE_PERMISSION, this)) {
            profileDetailPresenter.instance().tempImageUri = Camera.getCamera(this)
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    override fun getProfileData(): Profile {
        val profileData = Profile()
        val firstName = first_name.text.toString().trim()
        val lastName = last_name.text.toString().trim()
        val email = email.text.toString().trim()
        val imageUri = profileDetailPresenter.instance().imageUri

        if (imageUri != null) {
            //if user change ava
            profileData.imgUser = imageUri.toString()
        }

        if (firstName.isNotEmpty() && firstName.length < 20) {
            profileData.firstName = firstName
        }

        if (lastName.isNotEmpty() && lastName.length < 20) {
            profileData.lastName = lastName
        }

        if (profileDetailPresenter.isValidEmail(email))
            profileData.email = email
        else
            profileData.email = ""

        profileData.isNotificationsEnable = notifications_switch.isChecked
        profileData.isCallsEnable = calls_switch.isChecked

        return profileData
    }


    override fun setProfileData(profileData: Profile) {
        if (profileData.imgUser != null) {
            val imageUri = Uri.parse(profileData.imgUser)
            Glide.with(img_user.context).load(imageUri)
                .apply(RequestOptions().centerCrop().circleCrop())
                .into(img_user)
        }

        phone_number.text = profileData.phone
        first_name.setText(profileData.firstName)
        last_name.setText(profileData.lastName)
        email.setText(profileData.email)
        notifications_switch.isChecked = profileData.isNotificationsEnable
        calls_switch.isChecked = profileData.isCallsEnable
    }


    override fun setListeners() {
        img_user.setOnClickListener {
            getBottomSheet(loadPhotoBottomSheet)
        }

        on_view_profile.setOnClickListener {
            hideBottomSheet()
        }


        error_btn.setOnClickListener {
            hideBottomSheet()
        }


        back_btn.setOnClickListener {
            profileDetailPresenter.back()
        }

        make_photo.setOnClickListener {
            profileDetailPresenter.getCamera(this)
        }

        clip_photo.setOnClickListener {
            Gallery.getPhoto(this)
        }

        notifications_switch.setOnClickListener {
            //TODO
            //profileDetailPresenter?.showNotifications(notification)
        }

        calls_switch.setOnClickListener {
            //TODO
            //profileDetailPresenter?.showNotifications(notification)
        }

        logout.setOnClickListener {
            profileDetailPresenter.logout()

            setResult(EXIT)
            finish()
        }

        profileDetailPresenter.listenerEditText(first_name)

        profileDetailPresenter.listenerEditText(last_name)

        profileDetailPresenter.listenerEditText(email)
    }


    private fun setBottomSheet() {
        loadPhotoBottomSheet = BottomSheetBehavior.from<View>(load_photo_bottom_sheet)
        errorBottomSheet = BottomSheetBehavior.from<View>(error_bottom_sheet)


        loadPhotoBottomSheet?.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                onSlideBottomSheet(slideOffset)
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                onStateChangedBottomSheet(newState)
            }
        })

        errorBottomSheet?.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                onSlideBottomSheet(slideOffset)
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                onStateChangedBottomSheet(newState)
            }
        })
    }


    private fun hideBottomSheet() {
        loadPhotoBottomSheet?.state = BottomSheetBehavior.STATE_COLLAPSED
        errorBottomSheet?.state = BottomSheetBehavior.STATE_COLLAPSED
    }


    private fun onSlideBottomSheet(slideOffset: Float) {
        if (slideOffset > 0) {
            if (slideOffset * 0.8f > 0.8) {
                on_view_profile.alpha = 0.8f
            } else {
                on_view_profile.alpha = slideOffset * 0.8f
            }
        }
    }


    private fun onStateChangedBottomSheet(newState: Int) {
        if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
            on_view_profile.visibility = View.GONE
        } else {
            on_view_profile.visibility = View.VISIBLE
        }
    }


    override fun isDataNamesComplete(): Boolean {
        var isComplete = false
        val firstNameText = first_name.text.toString().trim()
        val lastNameText = last_name.text.toString().trim()

        if (firstNameText.isNotEmpty() && lastNameText.isNotEmpty()) {
            isComplete = true
        }

        return isComplete
    }


    override fun setDataIntent(isShowPopup: Boolean, profileData: Profile?) {
        val intent = Intent()
        intent.putExtra(PROFILE_DATA, profileData)
        intent.putExtra(IS_SHOW_POPUP, isShowPopup)
        setResult(RESULT_OK, intent)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        var tempImageUri = profileDetailPresenter.instance().tempImageUri

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PROFILE_CHECK_PHOTO) {
                //after check photo
                //set new photo
                profileDetailPresenter.instance().imageUri = tempImageUri

                Glide.with(img_user.context).load(tempImageUri)
                    .apply(RequestOptions().centerCrop().circleCrop())
                    .into(img_user)
            } else {
                //before check photo
                if (requestCode == Constants.GALLERY) {
                    tempImageUri = data?.data
                    profileDetailPresenter.instance().tempImageUri = tempImageUri
                }

                profileDetailPresenter.showCheckPhoto(this, tempImageUri.toString())
            }

            hideBottomSheet()

        } else {
            profileDetailPresenter.instance().tempImageUri = null
        }
    }


    override fun showNotifications(isPositive: Boolean) {
        val view = notification

        if (isPositive) {
            handlerAnimation?.removeCallbacksAndMessages(null)
            handlerAnimation = Handler()
            view.translationY = 0.0f
            view.alpha = 0.0f

            view.animate()
                .setDuration(500L)
                .translationY(100f)
                .alpha(1.0f)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        super.onAnimationEnd(animation)
                        handlerAnimation?.postDelayed({ hideNotifications() }, 1000)
                    }
                })
        } else {
            //error popup
            getBottomSheet(errorBottomSheet)
        }
    }


    private fun hideNotifications() {
        val view = notification

        view.animate()
            .setDuration(500L)
            .translationY(-100f)
            .alpha(0.0f)
    }


    private fun getBottomSheet(bottomSheet: BottomSheetBehavior<*>?) {
        hideKeyboard()

        bottomSheet?.state = BottomSheetBehavior.STATE_EXPANDED
    }


    override fun finishAvtivity() {
        finish()
    }


    override fun getNavHost(): NavController? {
        return null
    }


    override fun hideKeyboard() {
        Keyboard.hideKeyboard(this, profile_container)
    }


    override fun onBackPressed() {
        if (profileDetailPresenter.back()) {
            super.onBackPressed()
        }
    }


    override fun onDestroy() {
        profileDetailPresenter.instance().detachView()
        profileDetailPresenter.onDestroy()
        super.onDestroy()
    }
}