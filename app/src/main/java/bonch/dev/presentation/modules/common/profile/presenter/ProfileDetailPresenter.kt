package bonch.dev.presentation.modules.common.profile.presenter

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.provider.MediaStore
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import bonch.dev.App
import bonch.dev.Permissions
import bonch.dev.domain.entities.common.profile.Profile
import bonch.dev.domain.interactor.common.profile.IProfileInteractor
import bonch.dev.domain.utils.Camera
import bonch.dev.presentation.base.BasePresenter
import bonch.dev.presentation.modules.common.profile.ProfileComponent
import bonch.dev.presentation.modules.common.checkphoto.CheckPhotoView
import bonch.dev.presentation.modules.common.profile.view.IProfileDetailView
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import javax.inject.Inject


class ProfileDetailPresenter : BasePresenter<IProfileDetailView>(),
    IProfileDetailPresenter {

    @Inject
    lateinit var profileInteractor: IProfileInteractor

    private var startDataProfile: Profile? = null

    private val PHOTO = "PHOTO"
    private val PROFILE_CHECK_PHOTO = 12

    private var isDataSaved = false

    var tempImageUri: Uri? = null
    var imageUri: Uri? = null


    init {
        ProfileComponent.profileComponent?.inject(this)
    }


    override fun getProfileDataDB() {
        profileInteractor.initRealm()

        val profileData = profileInteractor.getProfileData()

        profileData?.let {
            //set start data
            startDataProfile = profileData

            if (it.imgUser != null) {
                imageUri = Uri.parse(it.imgUser)
            }

            getView()?.setProfileData(it)
        }
    }


    override fun saveProfileData(profileData: Profile?) {
        if (profileData != null) {
            //set phone (not allow user to change it)
            profileData.phone = startDataProfile?.phone

            //local save
            profileInteractor.saveProfileData(profileData)

            //remote save
            profileInteractor.sendProfileData(profileData)

            //update start data
            startDataProfile = profileData

            //TODO
            //load photo
            //val uri = Uri.parse(profileData.imgUser)
//            val uri = Uri.parse("")
//            if (uri != null) {
//                val contentResolver = App.appComponent.getContext().contentResolver
//
//                val btm = if (android.os.Build.VERSION.SDK_INT >= 29) {
//                    // To handle deprication use
//                    ImageDecoder.decodeBitmap(
//                        ImageDecoder.createSource(
//                            contentResolver,
//                            uri
//                        )
//                    )
//                } else {
//                    // Use older version
//                    MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
//                }
//
//                val file = convertImage(btm)
//
//                if (file != null) {
//                    //profileInteractor.loadPhoto(file)
//                }
//            }
        }
    }


    override fun getCamera(activity: Activity) {
        //for correct getting camera
        if (Permissions.isAccess(Permissions.STORAGE_PERMISSION, activity)) {
            tempImageUri = Camera.getCamera(activity)
        } else {
            Permissions.access(Permissions.STORAGE_PERMISSION_REQUEST, activity)
        }
    }


    override fun showCheckPhoto(activity: Activity, img: String) {
        val context = App.appComponent.getContext()
        val intent = Intent(context, CheckPhotoView::class.java)
        intent.putExtra(PHOTO, img)
        activity.startActivityForResult(intent, PROFILE_CHECK_PHOTO)
    }


    override fun back(): Boolean {
        getView()?.hideKeyboard()

        val isDataComplete = getView()?.isDataNamesComplete()

        return if (isDataComplete != null) {
            if (isDataComplete) {
                val profileData = getView()?.getProfileData()
                profileData?.imgUser = imageUri?.toString()
                setDataIntent(profileData)

                saveProfileData(profileData)

                getView()?.finishAvtivity()

                true
            } else {
                getView()?.showNotifications(false)
                false
            }

        } else {
            false
        }
    }


    private fun setDataIntent(profileData: Profile?) {
        var isShowPopup = false

        if (!isDataSaved && isDataChanged()) {
            isShowPopup = true
        }

        getView()?.setDataIntent(isShowPopup, profileData)
    }


    private fun isDataChanged(): Boolean {
        var isChanged = false
        val actualProfile = getView()?.getProfileData()
        //set photo
        actualProfile?.imgUser = imageUri?.toString()

        startDataProfile?.let {
            if (it.firstName != actualProfile?.firstName
                || it.lastName != actualProfile?.lastName
                || it.email != actualProfile?.email
                || it.imgUser != actualProfile?.imgUser
            ) {
                isChanged = true
            }
        }

        return isChanged
    }


    override fun listenerEditText(editText: EditText) {

        setOnChangedListener(editText)

        editText.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE && isDataChanged()) {

                val isDataComplete = getView()?.isDataNamesComplete()

                isDataSaved = if (isDataComplete != null && isDataComplete) {
                    val profileData = getView()?.getProfileData()
                    profileData?.imgUser = imageUri?.toString()

                    saveProfileData(profileData)

                    true
                } else {
                    false
                }

                isDataComplete?.let {
                    getView()?.showNotifications(it)
                }

                getView()?.hideKeyboard()
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


    override fun logout() {
        //clear data and close app
        profileInteractor.removeProfileData()
        profileInteractor.removeToken()
    }


    override fun isValidEmail(target: String): Boolean {
        return if (TextUtils.isEmpty(target) || target.length > 40) {
            false
        } else {
            Patterns.EMAIL_ADDRESS.matcher(target).matches() && target.length < 40
        }
    }


    private fun convertImage(bitmap: Bitmap): File? {
        val context = App.appComponent.getContext()
        val filesDir: File = context.filesDir
        var file: File?

        val os: OutputStream
        try {
            file = File(filesDir, "profile.jpg")
            os = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os)
            os.flush()
            os.close()
        } catch (e: java.lang.Exception) {
            file = null
            Log.e(javaClass.simpleName, "Error writing bitmap", e)
        }

        return file
    }


    override fun instance(): ProfileDetailPresenter {
        return this
    }


    override fun onDestroy() {
        profileInteractor.closeRealm()
    }

}
