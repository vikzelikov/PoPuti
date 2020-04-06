package bonch.dev.view.passanger.profile

import android.content.Intent
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import bonch.dev.Permissions
import bonch.dev.R
import bonch.dev.model.passanger.profile.pojo.Profile
import bonch.dev.presenter.passanger.profile.ProfileDetailPresenter
import bonch.dev.utils.Camera
import bonch.dev.utils.Constants
import bonch.dev.utils.Gallery
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.profile_detail_activity.view.*


class ProfileDetailView : AppCompatActivity() {

    private var profileDetailPresenter: ProfileDetailPresenter? = null
    var loadPhotoBottomSheet: BottomSheetBehavior<*>? = null
    var errorBottomSheet: BottomSheetBehavior<*>? = null

    init {
        if (profileDetailPresenter == null) {
            profileDetailPresenter = ProfileDetailPresenter(this)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile_detail_activity)

        val root = findViewById<View>(R.id.profile_container)

        profileDetailPresenter?.getProfileDataDB(root)
        profileDetailPresenter?.startProfile = profileDetailPresenter?.getProfileData()

        setBottomSheet(root)

        setListeners(root)

        setMovingButtonListener(root)
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (Permissions.isAccess(Constants.STORAGE_PERMISSION, this)) {
            profileDetailPresenter?.imageUri = Camera.getCamera(this)
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    fun setProfileData(root: View, profileData: Profile) {
        val firstName = root.first_name
        val lastName = root.last_name
        val phone = root.phone_number
        val email = root.email
        val userImg = root.img_user
        val notificationsSwitch = root.notifications_switch
        val callsSwitch = root.calls_switch

        if (profileData.imgUser != null) {
            val imageUri = Uri.parse(profileData.imgUser)
            Glide.with(applicationContext).load(imageUri)
                .apply(RequestOptions().centerCrop().circleCrop())
                .into(userImg)
        }

        phone.text = profileData.phone
        firstName.setText(profileData.firstName)
        lastName.setText(profileData.lastName)
        email.setText(profileData.email)
        notificationsSwitch.isChecked = profileData.isNotificationsEnable
        callsSwitch.isChecked = profileData.isCallsEnable
    }


    private fun setListeners(root: View) {
        val userImg = root.img_user
        val makePhoto = root.make_photo
        val clipPhoto = root.clip_photo
        val notificationsSwitch = root.notifications_switch
        val callsSwitch = root.calls_switch
        val logout = root.logout
        val backBtn = root.back_btn
        val onView = root.on_view_profile
        val errorBtn = root.error_btn
        val firstName = root.first_name
        val lastName = root.last_name
        val email = root.email

        userImg.setOnClickListener {
            profileDetailPresenter?.getBottomSheet()
        }

        onView.setOnClickListener {
            profileDetailPresenter?.hideBottomSheet()
        }


        errorBtn.setOnClickListener {
            profileDetailPresenter?.hideBottomSheet()
        }


        backBtn.setOnClickListener {
            profileDetailPresenter?.back()
        }

        makePhoto.setOnClickListener {
            profileDetailPresenter?.getCamera()
        }

        clipPhoto.setOnClickListener {
            Gallery.getPhoto(this)
        }

        notificationsSwitch.setOnClickListener {
            //profileDetailPresenter?.showNotifications(notification)
        }

        callsSwitch.setOnClickListener {
            //profileDetailPresenter?.showNotifications(notification)
        }

        logout.setOnClickListener {
            profileDetailPresenter?.logout()
        }

        profileDetailPresenter?.listenerEditText(firstName)
        profileDetailPresenter?.listenerEditText(lastName)
        profileDetailPresenter?.listenerEditText(email)

        profileDetailPresenter?.keyboardListener()
    }


    private fun setBottomSheet(root: View) {
        loadPhotoBottomSheet = BottomSheetBehavior.from<View>(root.load_photo_bottom_sheet)
        errorBottomSheet = BottomSheetBehavior.from<View>(root.error_bottom_sheet)


        loadPhotoBottomSheet!!.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                profileDetailPresenter?.onSlideBottomSheet(slideOffset)
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                profileDetailPresenter?.onStateChangedBottomSheet(newState)
            }
        })

        errorBottomSheet!!.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                profileDetailPresenter?.onSlideBottomSheet(slideOffset)
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                profileDetailPresenter?.onStateChangedBottomSheet(newState)
            }
        })
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        profileDetailPresenter?.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }


    override fun onBackPressed() {
        if(profileDetailPresenter!!.back()){
            super.onBackPressed()
        }
    }


    override fun onDestroy() {
        profileDetailPresenter?.onDestroy()
        super.onDestroy()
    }


    fun setMovingButtonListener(root: View) {
        val logout = root.logout_text
        var heightDiff: Int
        val rect = Rect()
        var screenHeight = 0
        var startHeight = 0

        root.viewTreeObserver
            .addOnGlobalLayoutListener {
                root.getWindowVisibleDisplayFrame(rect)
                heightDiff = screenHeight - (rect.bottom - rect.top)

                if (screenHeight == 0) {
                    screenHeight = root.rootView.height
                }

                if (startHeight == 0) {
                    startHeight = screenHeight - (rect.bottom - rect.top)
                }

                if (heightDiff > startHeight) {
                    //move UP
                    logout.visibility = View.GONE
                } else {
                    //move DOWN
                    logout.visibility = View.VISIBLE
                }

            }
    }
}
