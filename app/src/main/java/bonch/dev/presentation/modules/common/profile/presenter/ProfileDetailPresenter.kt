package bonch.dev.presentation.modules.common.profile.presenter

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Patterns
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import bonch.dev.App
import bonch.dev.Permissions
import bonch.dev.R
import bonch.dev.domain.entities.common.profile.Profile
import bonch.dev.domain.interactor.common.profile.IProfileInteractor
import bonch.dev.domain.utils.Camera
import bonch.dev.domain.utils.NetworkUtil
import bonch.dev.presentation.base.BasePresenter
import bonch.dev.presentation.modules.common.checkphoto.CheckPhotoView
import bonch.dev.presentation.modules.common.media.MediaEvent
import bonch.dev.presentation.modules.common.profile.ProfileComponent
import bonch.dev.presentation.modules.common.profile.view.IProfileDetailView
import javax.inject.Inject


class ProfileDetailPresenter : BasePresenter<IProfileDetailView>(),
    IProfileDetailPresenter {

    @Inject
    lateinit var profileInteractor: IProfileInteractor

    private var startDataProfile: Profile? = null
    private var mediaEvent: MediaEvent? = null

    private val PHOTO = "PHOTO"
    private val PROFILE_CHECK_PHOTO = 12

    private var isDataSaved = false
    private var isPhotoUploaded = true


    var tempImageUri: Uri? = null
    var imageUri: Uri? = null


    init {
        ProfileComponent.profileComponent?.inject(this)

        mediaEvent = MediaEvent()
    }


    override fun getProfile() {
        var profile: Profile?

        val context = App.appComponent.getContext()
        if (!NetworkUtil.isNetworkConnected(context)) {
            getView()?.showNotification(context.resources.getString(R.string.checkInternet))
        }

        profileInteractor.initRealm()
        getView()?.showLoading()

        profileInteractor.getProfileRemote { profileData, _ ->
            val mainHandler = Handler(Looper.getMainLooper())
            val myRunnable = Runnable {
                kotlin.run {
                    profile = profileData

                    //try to get from locate storage
                    if (profile == null) {
                        profile = profileInteractor.getProfileLocal()
                    }
                    //todo remove it ******** пока не свяжем это с сервером
                    val x = profileInteractor.getProfileLocal()
                    x?.let {
                        profile?.isCallsEnable = x.isCallsEnable
                        profile?.isNotificationsEnable = x.isNotificationsEnable
                    }
                    //todo*********

                    getView()?.hideLoading()

                    profile?.let {
                        //set start data
                        startDataProfile = profile

                        if (it.imgUser != null) {
                            imageUri = Uri.parse(it.imgUser)
                        }

                        getView()?.setProfile(it)
                    }
                }
            }
            mainHandler.post(myRunnable)
        }
    }


    override fun saveProfile(profileData: Profile?) {
        if (profileData != null) {
            //set phone (not allow user to change it)
            profileData.phone = startDataProfile?.phone

            //local save
            profileInteractor.localSaveProfile(profileData)

            //remote save
            profileInteractor.remoteSaveProfile(profileData)

            //update start data
            startDataProfile = profileData

            //load photo
            imageUri?.let {
                isPhotoUploaded = false
                imageUri = null

                Thread(Runnable {
                    val btm = mediaEvent?.getBitmap(it)
                    if (btm != null) {
                        val file = mediaEvent?.convertImage(btm, "photo")
                        if (file != null) {
                            profileInteractor.loadPhoto(file, profileData) {
                                isPhotoUploaded = true
                            }
                        } else isPhotoUploaded = true
                    } else isPhotoUploaded = true
                }).start()
            }
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

        //if Net off, let go to back
        if (!NetworkUtil.isNetworkConnected(App.appComponent.getContext())) return true

        val isDataComplete = getView()?.isDataNamesComplete()
        val res = App.appComponent.getContext().resources

        return if (isDataComplete != null) {
            if (isDataComplete) {

                if (isDataChanged()) {
                    val profileData = getView()?.getProfile()

                    setDataIntent(profileData)

                    saveProfile(profileData)
                }

                if (isPhotoUploaded) {
                    true
                } else {
                    getView()?.showNotification(res.getString(R.string.photoProfileLoading))
                    false
                }
            } else {
                getView()?.showErrorNotification()
                false
            }
        } else false
    }


    private fun setDataIntent(profileData: Profile?) {
        var isShowPopup = false

        if (!isDataSaved && isDataChanged()) {
            isShowPopup = true
        }

        profileData?.imgUser = tempImageUri?.toString()

        getView()?.setDataIntent(isShowPopup, profileData)
    }


    private fun isDataChanged(): Boolean {
        var isChanged = false
        val actualProfile = getView()?.getProfile()

        //set photo
        actualProfile?.imgUser = imageUri?.toString()

        startDataProfile?.let {
            if (it.firstName != actualProfile?.firstName
                || it.lastName != actualProfile?.lastName
                || it.email != actualProfile?.email
                || it.imgUser != actualProfile?.imgUser
                || it.isNotificationsEnable != actualProfile?.isNotificationsEnable
                || it.isCallsEnable != actualProfile.isCallsEnable
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
                    if (isDataChanged()) {
                        val profileData = getView()?.getProfile()

                        saveProfile(profileData)

                        startDataProfile = profileData
                    }

                    true
                } else {
                    false
                }

                isDataComplete?.let {
                    val res = App.appComponent.getContext().resources
                    if (it)
                        getView()?.showNotification(res.getString(R.string.dataSaved))
                    else getView()?.showErrorNotification()
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
    }


    override fun isValidEmail(target: String): Boolean {
        return if (TextUtils.isEmpty(target) || target.length > 40) {
            false
        } else {
            Patterns.EMAIL_ADDRESS.matcher(target).matches() && target.length < 40
        }
    }


    override fun instance(): ProfileDetailPresenter {
        return this
    }


    override fun onDestroy() {
        profileInteractor.closeRealm()
    }

}

