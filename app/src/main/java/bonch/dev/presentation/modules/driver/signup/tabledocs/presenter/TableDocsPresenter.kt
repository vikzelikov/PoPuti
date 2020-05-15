package bonch.dev.presentation.modules.driver.signup.tabledocs.presenter

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import androidx.fragment.app.Fragment
import bonch.dev.App
import bonch.dev.Permissions
import bonch.dev.R
import bonch.dev.domain.entities.driver.signup.Docs
import bonch.dev.domain.entities.driver.signup.NewPhoto
import bonch.dev.domain.entities.driver.signup.SignupMainData
import bonch.dev.domain.entities.driver.signup.SignupMainData.idStep
import bonch.dev.domain.entities.driver.signup.SignupMainData.listDocs
import bonch.dev.domain.entities.driver.signup.Step
import bonch.dev.domain.interactor.driver.signup.ISignupInteractor
import bonch.dev.domain.utils.Constants
import bonch.dev.presentation.base.BasePresenter
import bonch.dev.presentation.modules.common.checkphoto.CheckPhotoView
import bonch.dev.presentation.modules.common.media.IMediaEvent
import bonch.dev.presentation.modules.common.media.MediaEvent
import bonch.dev.presentation.modules.driver.signup.SignupComponent
import bonch.dev.presentation.modules.driver.signup.tabledocs.view.ITableDocsView
import javax.inject.Inject


class TableDocsPresenter : BasePresenter<ITableDocsView>(), ITableDocsPresenter {

    @Inject
    lateinit var signupInteractor: ISignupInteractor

    private var mediaEvent: IMediaEvent


    private val PHOTO = "PHOTO"
    private val TITLE = "TITLE"
    private val CHECK_PHOTO = 12

    var isDriverCreated = false


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
                while (true) {
                    val imageId = listDocs[i].id
                    if (imageId != null) {
                        arr[i] = imageId
                        break
                    }
                }
            }

            driverData.docArray = arr

            signupInteractor.createDriver(driverData) { isSuccess ->
                if (!isSuccess) {
                    //error show
                    val res = App.appComponent.getContext().resources
                    getView()?.showNotification(res.getString(R.string.errorSystem))
                }

                isDriverCreated = true
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
                //block UI
                isDriverCreated = false

                //do it with server
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
                            val docs = Docs()
                            docs.id = idStep.step
                            docs.imgDocs = SignupMainData.imgUri
                            docs.isVerify = null
                            listDocs[idStep.step] = docs

                            //sent photo to server
                            sentPhoto(docs)

                            //put new photo
                            Thread(Runnable {
                                putNewPhoto()
                            }).start()
                        } else {
                            //error delete old photo from server
                            val res = App.appComponent.getContext().resources
                            getView()?.showNotification(res.getString(R.string.errorSystem))
                        }
                    }
                }
            } catch (ex: IndexOutOfBoundsException) {
            }
        }
    }


    private fun putNewPhoto() {
        while (true) {
            val imageId = listDocs[idStep.step].id
            if (imageId != null) {
                val photo = NewPhoto()
                photo.docArray = intArrayOf(imageId)
                signupInteractor.putNewPhoto(photo) { isSuccess ->
                    //remove block UI
                    isDriverCreated = true

                    if (isSuccess) {
                        getView()?.loadPhoto()
                    } else {
                        //error put new photo
                        val res = App.appComponent.getContext().resources
                        getView()?.showNotification(res.getString(R.string.errorSystem))
                    }
                }
                break
            }
        }
    }


    private fun sentPhoto(docs: Docs) {
        val uri = Uri.parse(docs.imgDocs)
        if (uri != null) {
            //convert to Bitmap
            val btm = mediaEvent.getBitmap(uri)

            val position = docs.id

            if (position != null) {
                listDocs[position].id = null

                //get title
                val title = TitlePhoto.getTitlePhoto(getByValue(position))

                title?.let {
                    //get orientation
                    val exifOrientation = mediaEvent.getOrientation(uri)

                    //compress and convert to JPEG
                    val file = mediaEvent.convertImage(btm, it)

                    if (file != null) {
                        //save orientation
                        if (exifOrientation != null) {
                            val newExif = ExifInterface(file)
                            newExif.setAttribute(ExifInterface.TAG_ORIENTATION, exifOrientation)
                            newExif.saveAttributes()
                        }

                        //upload
                        signupInteractor.loadDocs(file, position) { isSuccess ->
                            if (!isSuccess) {
                                //error show
                                val res = App.appComponent.getContext().resources
                                getView()?.showNotification(res.getString(R.string.errorSystem))
                            }
                        }
                    }
                }
            }
        }
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
        val arr = arrayListOf<Docs>()
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


    override fun instance(): TableDocsPresenter {
        return this
    }


    override fun onBackPressed(): Boolean = isDriverCreated

}