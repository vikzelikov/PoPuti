package bonch.dev.presenter.passanger.profile

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
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
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import bonch.dev.MainActivity
import bonch.dev.Permissions
import bonch.dev.R
import bonch.dev.model.passanger.profile.ProfileModel
import bonch.dev.model.passanger.profile.pojo.Profile
import bonch.dev.utils.Constants
import bonch.dev.utils.Keyboard
import bonch.dev.view.passanger.profile.ProfileDetailView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.yandex.runtime.Runtime.getApplicationContext
import kotlinx.android.synthetic.main.profile_detail_activity.*
import java.io.ByteArrayOutputStream

class ProfileDetailPresenter(val profileDetailView: ProfileDetailView) : IProfilePresenter {

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


    fun listenerEditText(editText: EditText) {
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

                val root = profileDetailView.findViewById<View>(R.id.rootLinearLayout)
                Keyboard.hideKeyboard(profileDetailView, root)
                return@OnEditorActionListener true
            }

            false
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
        //save data
        val root = profileDetailView.findViewById<View>(R.id.rootLinearLayout)
        Keyboard.hideKeyboard(profileDetailView, root)

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


    private fun showErrorBottomSheet() {
        Thread(Runnable {
            while (true) {
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
        }).start()
    }


    fun getBottomSheet() {
        profileDetailView.loadPhotoBottomSheet!!.state = BottomSheetBehavior.STATE_EXPANDED
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
        val root = profileDetailView.rootLinearLayout

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

