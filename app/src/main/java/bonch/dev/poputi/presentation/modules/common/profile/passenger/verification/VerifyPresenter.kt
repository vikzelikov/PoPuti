package bonch.dev.poputi.presentation.modules.common.profile.passenger.verification

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.os.Looper
import bonch.dev.poputi.App
import bonch.dev.poputi.Permissions
import bonch.dev.poputi.R
import bonch.dev.poputi.domain.entities.common.media.Photo
import bonch.dev.poputi.domain.entities.common.profile.verification.NewPhoto
import bonch.dev.poputi.domain.entities.common.profile.verification.VerifyData
import bonch.dev.poputi.domain.entities.common.profile.verification.VerifyStep
import bonch.dev.poputi.domain.interactor.common.profile.IProfileInteractor
import bonch.dev.poputi.domain.utils.Constants
import bonch.dev.poputi.presentation.base.BasePresenter
import bonch.dev.poputi.presentation.interfaces.SuccessHandler
import bonch.dev.poputi.presentation.modules.common.checkphoto.CheckPhotoView
import bonch.dev.poputi.presentation.modules.common.media.IMediaEvent
import bonch.dev.poputi.presentation.modules.common.media.MediaEvent
import bonch.dev.poputi.presentation.modules.common.profile.ContractPresenter
import bonch.dev.poputi.presentation.modules.common.profile.ContractView
import bonch.dev.presentation.modules.common.profile.ProfileComponent
import javax.inject.Inject

class VerifyPresenter : BasePresenter<ContractView.IVerifyView>(),
    ContractPresenter.IVerifyPresenter {


    @Inject
    lateinit var profileInteractor: IProfileInteractor

    private var mediaEvent: IMediaEvent

    private val PHOTO = "PHOTO"
    private val TITLE = "TITLE"
    private val CHECK_PHOTO = 12

    private var isBlock = false
    var isFirstVerification = true


    init {
        ProfileComponent.profileComponent?.inject(this)

        mediaEvent = MediaEvent()
    }


    override fun checkVerification() {
        profileInteractor.getProfile { profile, _ ->

        }
    }


    override fun loadPhoto() {
        Thread(Runnable {
            //todo send request for get photos

            sortDocs()

            val listDocs: ArrayList<Photo> = VerifyData.listDocs


            val mainHandler = Handler(Looper.getMainLooper())
            val myRunnable = Runnable {
                kotlin.run {
                    getView()?.setDocs(listDocs)
                }
            }

            mainHandler.post(myRunnable)
        }).start()
    }


    override fun createVerification() {
        val verifyData = VerifyData.verify
        if (verifyData != null) {
            val arr = IntArray(VerifyData.listDocs.size)

            for (i in arr.indices) {
                val imageId = VerifyData.listDocs[i].imgId
                if (imageId != null) {
                    arr[i] = imageId
                }
            }

            verifyData.docArray = arr

            profileInteractor.verification(verifyData) { isSuccess ->
                if (isSuccess) {
                    getView()?.finishVerification()
                } else {
                    val res = App.appComponent.getContext().resources
                    getView()?.showNotification(res.getString(R.string.tryAgain))
                }

                isBlock = false
            }
        }
    }


    override fun getCamera(activity: Activity) {
        Permissions.access(Permissions.STORAGE_PERMISSION_REQUEST, activity)
    }


    override fun onActivityResult(
        activity: Activity,
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CHECK_PHOTO) {

                if (isFirstVerification) {

                    Thread(Runnable {
                        nextStep()
                    }).start()

                } else {
                    //start animation
                    getView()?.showLoadingPhoto(VerifyData.idStep)

                    //block UI
                    isBlock = true

                    //start work with server
                    remakePhoto()
                }
            } else {
                if (requestCode == Constants.GALLERY) {
                    //if not camera
                    VerifyData.imgUri = data?.data.toString()
                }

                val img = VerifyData.imgUri
                val title = getTitleForCheckPhoto(VerifyData.idStep)
                if (img != null) {
                    showCheckPhoto(activity, img, title)
                }
            }
        }
    }


    /**
     * FOR FIRSTLY VERIFICATION
     */
    private fun nextStep() {
        val textError = App.appComponent.getContext().getString(R.string.tryAgain)

        //save photo
        if (VerifyData.imgUri != null) {

            getView()?.showLoading()

            try {
                val photo = Photo()
                photo.id = VerifyData.idStep.step
                photo.imgDocs = VerifyData.imgUri

                //saving photo in array
                VerifyData.listDocs.add(photo)

                //sent photo to server
                uploadPhoto(photo) { isSuccess ->
                    getView()?.hideLoading()

                    if (isSuccess) {
                        //set mini photo in view
                        VerifyData.imgUri?.let {
                            getView()?.setPhoto(VerifyData.idStep, it)

                            //next step
                            VerifyData.idStep = VerifyStep.PASSPORT_ADDRESS_PHOTO
                        }
                    } else {
                        getView()?.showNotification(textError)
                        VerifyData.listDocs.removeAt(VerifyData.listDocs.lastIndex)
                    }
                }
            } catch (ex: IndexOutOfBoundsException) {
            }
        } else getView()?.showNotification(textError)
    }


    /**
     * FOR REMAKE VERIFICATION
     */
    private fun remakePhoto() {
        //save photo
        if (VerifyData.imgUri != null) {
            try {
                //at first delete photo
                val imgId = VerifyData.listDocs[VerifyData.idStep.step].id
                if (imgId != null) {
                    profileInteractor.deletePhoto(imgId) { isSuccess ->
                        if (isSuccess) {
                            //success delete old photo from server
                            val photo = Photo()
                            photo.id = VerifyData.idStep.step
                            photo.imgDocs = VerifyData.imgUri
                            photo.isVerify = 0
                            VerifyData.listDocs[VerifyData.idStep.step] = photo

                            Thread(Runnable {
                                //sent photo to server
                                uploadPhoto(photo) {}
                            }).start()
                        } else {
                            //error delete old photo from server
                            failLoadPhoto()
                        }
                    }
                } else failLoadPhoto()
            } catch (ex: IndexOutOfBoundsException) {
            }
        } else failLoadPhoto()
    }


    private fun uploadPhoto(photo: Photo, callback: SuccessHandler) {
        val uri = Uri.parse(photo.imgDocs)

        if (uri != null) {
            val position = photo.id

            //convert to Bitmap
            val btm = mediaEvent.getBitmap(uri)

            if (btm != null && position != null) {
                //get title
                val title = getTitlePhoto(getByValue(position))

                if (title != null) {
                    //compress and convert to JPEG
                    val file = mediaEvent.convertImage(btm, title)

                    if (file != null) {
                        //upload
                        if (isFirstVerification) {

                            profileInteractor.uploadPhoto(file, position, callback)

                        } else {
                            profileInteractor.uploadPhoto(file, position) { isSuccess ->
                                if (isSuccess) putNewPhoto()
                                else failLoadPhoto()
                            }
                        }
                    } else {
                        failLoadPhoto()
                        callback(false)
                    }
                } else {
                    failLoadPhoto()
                    callback(false)
                }
            } else {
                failLoadPhoto()
                callback(false)
            }
        } else {
            failLoadPhoto()
            callback(false)
        }
    }


    private fun putNewPhoto() {
        val imageId = VerifyData.listDocs[VerifyData.idStep.step].imgId
        if (imageId != null) {
            val photo = NewPhoto()
            photo.docArray = intArrayOf(imageId)
            profileInteractor.putNewPhoto(photo) { isSuccess ->
                //remove block UI
                isBlock = false

                if (isSuccess) {
                    loadPhoto()

                    getView()?.hideLoadingPhoto()
                } else {
                    //error put new photo
                    failLoadPhoto()
                }
            }
        } else failLoadPhoto()
    }


    private fun failLoadPhoto() {
        val errText = App.appComponent.getApp().getString(R.string.tryAgain)
        getView()?.showNotification(errText)

        getView()?.hideLoadingPhoto()

        isBlock = false
    }


    private fun showCheckPhoto(activity: Activity, img: String, title: String?) {
        val context = App.appComponent.getContext()
        val intent = Intent(context, CheckPhotoView::class.java)
        intent.putExtra(PHOTO, img)
        intent.putExtra(TITLE, title)
        activity.startActivityForResult(intent, CHECK_PHOTO)
    }


    override fun sortDocs() {
        //copy arr
        val arr = arrayListOf<Photo>()
        arr.addAll(VerifyData.listDocs)

        arr.forEach {
            val title = it.imgName
            if (title != null) {
                val step = getStepByTitle(title)
                try {
                    VerifyData.listDocs[step.step] = it
                } catch (ex: IndexOutOfBoundsException) {
                }
            }
        }
    }


    override fun getByValue(step: Int) = VerifyStep.values().firstOrNull { it.step == step }


    override fun instance() = this


    override fun isBlockBack() = isBlock


    private fun getTitlePhoto(idStep: VerifyStep?): String? {
        var title: String? = null
        val res = App.appComponent.getContext().resources

        when (idStep) {
            VerifyStep.SELF_PHOTO_PASSPORT -> {
                title = res.getString(R.string.self_passport)
            }

            VerifyStep.PASSPORT_ADDRESS_PHOTO -> {
                title = res.getString(R.string.passportaddress)
            }
        }

        return title
    }


    private fun getTitleForCheckPhoto(idStep: VerifyStep?): String? {
        var title: String? = null
        val res = App.appComponent.getContext().resources

        when (idStep) {
            VerifyStep.SELF_PHOTO_PASSPORT -> {
                title = res.getString(R.string.checkSelfPassport)
            }

            VerifyStep.PASSPORT_ADDRESS_PHOTO -> {
                title = res.getString(R.string.checkPassportAddress)
            }
        }

        return title
    }


    private fun getStepByTitle(title: String): VerifyStep {
        var step = VerifyStep.SELF_PHOTO_PASSPORT
        val res = App.appComponent.getContext().resources

        when (title) {
            res.getString(R.string.self_passport) -> {
                step = VerifyStep.SELF_PHOTO_PASSPORT
            }

            res.getString(R.string.passportaddress) -> {
                step = VerifyStep.PASSPORT_ADDRESS_PHOTO
            }
        }

        return step
    }


    override fun resetData() {
        VerifyData.idStep = VerifyStep.SELF_PHOTO_PASSPORT
        VerifyData.listDocs = arrayListOf()
        VerifyData.imgUri = null
        VerifyData.verify = null
    }
}