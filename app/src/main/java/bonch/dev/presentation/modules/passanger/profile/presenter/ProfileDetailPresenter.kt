package bonch.dev.presentation.modules.passanger.profile.presenter

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.VectorDrawable
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import bonch.dev.Permissions
import bonch.dev.data.repository.passanger.profile.ProfileModel
import bonch.dev.data.repository.passanger.profile.pojo.Profile
import bonch.dev.domain.utils.Camera
import bonch.dev.domain.utils.Constants
import bonch.dev.route.Coordinator.showCheckPhoto
import bonch.dev.domain.utils.Keyboard
import bonch.dev.presentation.modules.passanger.profile.view.ProfileDetailView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.profile_detail_activity.*
import java.io.ByteArrayOutputStream

class ProfileDetailPresenter(private val profileDetailView: ProfileDetailView) :
    IProfilePresenter {

    private var profileModel: ProfileModel? = null
    private var handlerAnimation: Handler? = null
    private var isDataSaved = false
    private var isKeyboardShow = false
    var startProfile: Profile? = null
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
        val notificationsSwitch = root.notifications_switch
        val callsSwitch = root.calls_switch

        if (imageUri != null) {
            //if user change ava
            profileData.imgUser = imageUri.toString()
        } else if (imgUser.drawable !is VectorDrawable) {
            //if it is not default ava
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
            profileData.firstName = firstName
        }

        if (lastName.isNotEmpty()) {
            profileData.lastName = lastName
        }

        if (phone.isNotEmpty()) {
            profileData.phone = phone
        }

        if (email.isNotEmpty() && isValidEmail(email)) {
            profileData.email = email
        }

        profileData.isNotificationsEnable = notificationsSwitch.isChecked
        profileData.isCallsEnable = callsSwitch.isChecked

        return profileData
    }


    private fun saveProfileData(profileData: Profile?) {
        if (profileData != null) {
            profileModel?.saveProfileData(profileData)
        }
    }


    fun getCamera() {
        //for correct getting camera
        if (Permissions.isAccess(Constants.STORAGE_PERMISSION, profileDetailView)) {
            imageUri = Camera.getCamera(profileDetailView)
        } else {
            Permissions.access(Constants.STORAGE_PERMISSION_REQUEST, profileDetailView)
        }
    }


    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == Constants.PROFILE_CHECK_PHOTO) {
                //after check photo
                Glide.with(profileDetailView.applicationContext).load(imageUri)
                    .apply(RequestOptions().centerCrop().circleCrop())
                    .into(profileDetailView.img_user)
            } else {
                //before check photo
                if (requestCode == Constants.GALLERY) {
                    imageUri = data?.data
                }

                checkPhoto(imageUri.toString())
            }

            hideBottomSheet()
        } else {
            imageUri = null
        }
    }


    private fun checkPhoto(img: String) {
        val context = profileDetailView.applicationContext
        showCheckPhoto(context, profileDetailView, img)
    }


    fun listenerEditText(editText: EditText) {

        setOnChangedListener(editText)

        editText.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {

                val isDataComplete = isDataNamesComplete()

                isDataSaved = if (isDataComplete) {
                    val profileData = getProfileData()
                    saveProfileData(profileData)

                    true
                } else {
                    false
                }

                showNotifications(isDataComplete)

                Keyboard.hideKeyboard(profileDetailView, profileDetailView.profile_container)
                return@OnEditorActionListener true
            }

            false
        })
    }


    private fun setOnChangedListener(editText: EditText) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                isDataSaved = false
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })
    }


    private fun showNotifications(isPositive: Boolean) {
        val view = profileDetailView.notification

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
            showErrorBottomSheet()
        }
    }


    private fun hideNotifications() {
        val view = profileDetailView.notification

        view.animate()
            .setDuration(500L)
            .translationY(-100f)
            .alpha(0.0f)
    }


    fun back(): Boolean {
        Keyboard.hideKeyboard(profileDetailView, profileDetailView.profile_container)

        val isDataComplete = isDataNamesComplete()

        if (isDataComplete) {
            val profileData = getProfileData()
            saveProfileData(profileData)

            setDataIntent(profileData)

            profileDetailView.finish()

        } else {
            showNotifications(false)
        }

        return isDataComplete
    }


    private fun isDataNamesComplete(): Boolean {
        var isComplete = false
        val firstNameText = profileDetailView.first_name.text.toString().trim()
        val lastNameText = profileDetailView.last_name.text.toString().trim()

        if (firstNameText.isNotEmpty() && lastNameText.isNotEmpty()) {
            isComplete = true
        }

        return isComplete
    }


    private fun setDataIntent(profileData: Profile?) {
        var isShowPopup = false
        if (!isDataSaved && isDataChanged()) {
            isShowPopup = true
        }

        val intent = Intent()
        intent.putExtra(Constants.PROFILE_DATA, profileData)
        intent.putExtra(Constants.IS_SHOW_POPUP, isShowPopup)
        profileDetailView.setResult(AppCompatActivity.RESULT_OK, intent)
    }


    private fun isDataChanged(): Boolean {
        var isChanged = false
        val actualProfile = getProfileData()

        startProfile?.let {
            if (it.firstName != actualProfile.firstName
                || it.lastName != actualProfile.lastName
                || it.email != actualProfile.email
            ) {
                isChanged = true
            }
        }

        return isChanged
    }


    fun logout() {
        //clear data and close app
        profileModel?.removeAccessToken()

        profileDetailView.setResult(Constants.EXIT)
        profileDetailView.finish()
    }


    private fun showErrorBottomSheet() {
        Thread(Runnable {
            while (true) {
                println("error")

                if (!isKeyboardShow) {
                    //get main thread for get UI
                    val mainHandler = Handler(Looper.getMainLooper())
                    val myRunnable = Runnable {
                        kotlin.run {
                            profileDetailView.errorBottomSheet!!.state =
                                BottomSheetBehavior.STATE_EXPANDED
                        }
                    }
                    mainHandler.post(myRunnable)

                    break
                }
            }
            //kill thread
            Thread.currentThread().interrupt()
        }).start()
    }


    fun getBottomSheet() {
        Keyboard.hideKeyboard(profileDetailView, profileDetailView.profile_container)

        Thread(Runnable {
            while (true) {
                println("image")

                if (!isKeyboardShow) {
                    //get main thread for get UI
                    val mainHandler = Handler(Looper.getMainLooper())
                    val myRunnable = Runnable {
                        kotlin.run {
                            profileDetailView.loadPhotoBottomSheet!!.state =
                                BottomSheetBehavior.STATE_EXPANDED
                        }
                    }
                    mainHandler.post(myRunnable)

                    break
                }
            }
            //kill thread
            Thread.currentThread().interrupt()
        }).start()
    }


    fun hideBottomSheet() {
        profileDetailView.loadPhotoBottomSheet!!.state = BottomSheetBehavior.STATE_COLLAPSED
        profileDetailView.errorBottomSheet!!.state = BottomSheetBehavior.STATE_COLLAPSED
    }


    fun onSlideBottomSheet(slideOffset: Float) {
        val onMapView = profileDetailView.on_view_profile

        if (slideOffset > 0) {
            if (slideOffset * 0.8f > 0.8) {
                onMapView?.alpha = 0.8f
            } else {
                onMapView?.alpha = slideOffset * 0.8f
            }
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


    fun keyboardListener() {
        val root = profileDetailView.profile_container

        root.viewTreeObserver.addOnGlobalLayoutListener {
            val r = Rect()
            root.getWindowVisibleDisplayFrame(r)

            val screenHeight = root.rootView.height
            val keypadHeight = screenHeight - r.bottom

            isKeyboardShow = keypadHeight > screenHeight * 0.15
        }
    }


    private fun isValidEmail(target: String): Boolean {
        return if (TextUtils.isEmpty(target)) {
            false
        } else {
            Patterns.EMAIL_ADDRESS.matcher(target).matches()
        }
    }
}

