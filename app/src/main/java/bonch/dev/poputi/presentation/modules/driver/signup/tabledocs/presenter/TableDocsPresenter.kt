package bonch.dev.poputi.presentation.modules.driver.signup.tabledocs.presenter

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.fragment.app.Fragment
import bonch.dev.poputi.App
import bonch.dev.poputi.Permissions
import bonch.dev.poputi.R
import bonch.dev.poputi.domain.entities.common.media.Photo
import bonch.dev.poputi.domain.entities.driver.signup.NewPhoto
import bonch.dev.poputi.domain.entities.driver.signup.SignupMainData
import bonch.dev.poputi.domain.entities.driver.signup.SignupMainData.idStep
import bonch.dev.poputi.domain.entities.driver.signup.SignupMainData.listDocs
import bonch.dev.poputi.domain.entities.driver.signup.Step
import bonch.dev.poputi.domain.interactor.driver.signup.ISignupInteractor
import bonch.dev.poputi.domain.utils.Constants
import bonch.dev.poputi.presentation.base.BasePresenter
import bonch.dev.poputi.presentation.modules.common.checkphoto.CheckPhotoView
import bonch.dev.poputi.presentation.modules.common.media.IMediaEvent
import bonch.dev.poputi.presentation.modules.common.media.MediaEvent
import bonch.dev.poputi.presentation.modules.driver.signup.SignupComponent
import bonch.dev.poputi.presentation.modules.driver.signup.tabledocs.view.ITableDocsView
import javax.inject.Inject


class TableDocsPresenter : BasePresenter<ITableDocsView>(), ITableDocsPresenter {

    @Inject
    lateinit var signupInteractor: ISignupInteractor

    private var mediaEvent: IMediaEvent


    private val PHOTO = "PHOTO"
    private val TITLE = "TITLE"
    private val CHECK_PHOTO = 12

    private var isBlock = false


    init {
        SignupComponent.driverSignupComponent?.inject(this)

        mediaEvent = MediaEvent()
    }


    //create driver with server
    override fun createDriver() {
        val driverData = SignupMainData.driverData
        if (driverData != null) {
            val arr = IntArray(listDocs.size)

            for (i in arr.indices) {
                val imageId = listDocs[i].imgId
                if (imageId != null) {
                    arr[i] = imageId
                }
            }

            driverData.docArray = arr

            signupInteractor.createDriver(driverData) { isSuccess ->
                if (!isSuccess) {
                    //error show
                    val res = App.appComponent.getContext().resources
                    getView()?.showNotification(res.getString(R.string.tryAgain))
                }

                isBlock = false
            }
        }
    }


    override fun getCamera(fragment: Fragment) {
        Permissions.access(Permissions.STORAGE_PERMISSION_REQUEST, fragment)
    }


    override fun onActivityResult(
        fragment: Fragment,
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CHECK_PHOTO) {
                //start animation
                getView()?.showLoadingPhoto(idStep)

                //block UI
                isBlock = true

                //work with server
                remakePhotoDone()
            } else {
                if (requestCode == Constants.GALLERY) {
                    //if not camera
                    SignupMainData.imgUri = data?.data.toString()
                }

                val img = SignupMainData.imgUri
                val title = TitlePhoto.getTitleForCheckPhoto(idStep)
                if (img != null) {
                    showCheckPhoto(fragment, img, title)
                }
            }
        }
    }


    private fun remakePhotoDone() {
        //save photo
        if (SignupMainData.imgUri != null) {
            try {
                //at first delete photo
                val imgId = listDocs[idStep.step].id
                if (imgId != null) {
                    signupInteractor.deletePhoto(imgId) { isSuccess ->
                        if (isSuccess) {
                            //success delete old photo from server
                            val docs = Photo()
                            docs.id = idStep.step
                            docs.imgDocs = SignupMainData.imgUri
                            docs.isVerify = 0
                            listDocs[idStep.step] = docs

                            Thread(Runnable {
                                //sent photo to server
                                sentPhoto(docs)
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


    private fun sentPhoto(photo: Photo) {
        val uri = Uri.parse(photo.imgDocs)

        if (uri != null) {
            //convert to Bitmap
            val btm = mediaEvent.getBitmap(uri)

            val position = photo.id

            if (btm != null && position != null) {
                //get title
                val title = TitlePhoto.getTitlePhoto(getByValue(position))

                if (title != null) {
                    //compress and convert to JPEG
                    val file = mediaEvent.convertImage(btm, title)

                    if (file != null) {
                        //upload
                        signupInteractor.loadPhoto(file, position) { isSuccess ->
                            if (isSuccess) putNewPhoto()
                            else failLoadPhoto()
                        }
                    } else failLoadPhoto()
                } else failLoadPhoto()
            } else failLoadPhoto()
        } else failLoadPhoto()
    }


    private fun putNewPhoto() {
        val imageId = listDocs[idStep.step].imgId
        if (imageId != null) {
            val photo = NewPhoto()
            photo.docArray = intArrayOf(imageId)
            signupInteractor.putNewPhoto(photo) { isSuccess ->
                //remove block UI
                isBlock = false

                if (isSuccess) {
                    getView()?.loadPhoto()
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


    private fun showCheckPhoto(fragment: Fragment, img: String, title: String?) {
        val context = App.appComponent.getContext()
        val intent = Intent(context, CheckPhotoView::class.java)
        intent.putExtra(PHOTO, img)
        intent.putExtra(TITLE, title)
        fragment.startActivityForResult(intent, CHECK_PHOTO)
    }


    //sort docs by title
    override fun sortDocs() {
        //copy arr
        val arr = arrayListOf<Photo>()
        arr.addAll(listDocs)

        arr.forEach {
            val title = it.imgName
            if (title != null) {
                val step = TitlePhoto.getStepByTitle(title)
                try {
                    listDocs[step.step] = it
                } catch (ex: IndexOutOfBoundsException) {
                }
            }
        }
    }


    override fun getByValue(step: Int) = Step.values().firstOrNull { it.step == step }


    override fun instance() = this


    override fun isBlockBack() = isBlock

}