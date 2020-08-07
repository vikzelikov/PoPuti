package bonch.dev.poputi.presentation.modules.driver.signup.steps.presenter

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.fragment.app.Fragment
import bonch.dev.poputi.App
import bonch.dev.poputi.Permissions
import bonch.dev.poputi.R
import bonch.dev.poputi.domain.entities.common.media.Photo
import bonch.dev.poputi.domain.entities.driver.signup.SignupMainData
import bonch.dev.poputi.domain.entities.driver.signup.SignupMainData.idStep
import bonch.dev.poputi.domain.entities.driver.signup.SignupMainData.imgUri
import bonch.dev.poputi.domain.entities.driver.signup.SignupMainData.listDocs
import bonch.dev.poputi.domain.entities.driver.signup.SignupStep
import bonch.dev.poputi.domain.entities.driver.signup.Step
import bonch.dev.poputi.domain.interactor.driver.signup.ISignupInteractor
import bonch.dev.domain.utils.Camera
import bonch.dev.domain.utils.Constants.GALLERY
import bonch.dev.poputi.presentation.base.BasePresenter
import bonch.dev.poputi.presentation.interfaces.SuccessHandler
import bonch.dev.poputi.presentation.modules.common.checkphoto.CheckPhotoView
import bonch.dev.poputi.presentation.modules.common.media.IMediaEvent
import bonch.dev.poputi.presentation.modules.common.media.MediaEvent
import bonch.dev.poputi.presentation.modules.driver.signup.SignupComponent
import bonch.dev.poputi.presentation.modules.driver.signup.steps.view.ISignupStepView
import bonch.dev.poputi.presentation.modules.driver.signup.tabledocs.presenter.TitlePhoto
import bonch.dev.poputi.route.MainRouter
import javax.inject.Inject


class SignupStepPresenter : BasePresenter<ISignupStepView>(), ISignupStepPresenter {

    @Inject
    lateinit var signupInteractor: ISignupInteractor

    private var mediaEvent: IMediaEvent

    private val PHOTO = "PHOTO"
    private val TITLE = "TITLE"
    private val CHECK_PHOTO = 12


    init {
        SignupComponent.driverSignupComponent?.inject(this)

        mediaEvent = MediaEvent()
    }


    override fun getCamera(fragment: Fragment) {
        val context = fragment.context
        context?.let {
            if (Permissions.isAccess(Permissions.STORAGE_PERMISSION, context)) {
                imgUri = Camera.getCamera(fragment).toString()
            } else {
                Permissions.access(Permissions.STORAGE_PERMISSION_REQUEST, fragment)
            }
        }
    }


    override fun onActivityResult(
        fragment: Fragment,
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CHECK_PHOTO) {

                Thread(Runnable {
                    nextStep()
                }).start()

            } else {
                if (requestCode == GALLERY) {
                    //if not camera
                    imgUri = data?.data.toString()
                }

                val img = imgUri
                val title = TitlePhoto.getTitleForCheckPhoto(idStep)
                if (img != null) {
                    showCheckPhoto(fragment, img, title)
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


    private fun nextStep() {
        val textError = App.appComponent.getContext().getString(R.string.tryAgain)

        //save photo
        if (imgUri != null) {

            getView()?.showLoading()

            try {
                val photo = Photo()
                photo.id = idStep.step
                photo.imgDocs = imgUri

                if (idStep != Step.USER_PHOTO) listDocs.add(photo)

                //sent photo to server
                sendPhoto(photo) { isSuccess ->
                    getView()?.hideLoading()

                    if (isSuccess) {
                        if (idStep.step > Step.STS_DOC_BACK.step - 1) {
                            //end settings docs
                            MainRouter.showView(
                                R.id.show_table_docs_view,
                                getView()?.getNavHost(),
                                null
                            )
                        } else {
                            //next step
                            val id = getByValue(idStep.step.inc())
                            if (id != null) {
                                idStep = id
                                val signupData = getStepData(idStep)
                                getView()?.setDataStep(signupData)
                            } else getView()?.showNotification(textError)
                        }
                    } else {
                        getView()?.showNotification(textError)
                        listDocs.removeAt(listDocs.lastIndex)
                    }
                }

            } catch (ex: IndexOutOfBoundsException) {
            }
        } else getView()?.showNotification(textError)
    }


    private fun sendPhoto(photo: Photo, callback: SuccessHandler) {
        val uri = Uri.parse(photo.imgDocs)

        if (uri != null) {
            val position = photo.id

            //convert to Bitmap
            val btm = mediaEvent.getBitmap(uri)

            if (btm != null && position != null) {
                //get title
                val title = TitlePhoto.getTitlePhoto(getByValue(position))

                if (title != null) {
                    //compress and convert to JPEG
                    val file = mediaEvent.convertImage(btm, title)

                    if (file != null) {
                        //upload
                        signupInteractor.loadPhoto(file, position, callback)

                    } else callback(false)
                } else callback(false)
            } else callback(false)
        } else callback(false)
    }


    private fun getByValue(step: Int) = Step.values().firstOrNull { it.step == step }


    override fun getStepData(idStep: Step): SignupStep {
        val docsStepData = SignupStep()
        val res = App.appComponent.getContext().resources

        when (idStep) {
            Step.USER_PHOTO -> {
                SignupMainData.idStep = Step.USER_PHOTO
                docsStepData.title = res.getString(R.string.yourPhoto)
                docsStepData.subtitle = res.getString(R.string.yourPhotoTitle)
                docsStepData.imgDocs = R.drawable.ic_photo
            }

            Step.PASSPORT_PHOTO -> {
                SignupMainData.idStep = Step.PASSPORT_PHOTO
                docsStepData.title = res.getString(R.string.photoPassport)
                docsStepData.subtitle = res.getString(R.string.photoPassportTitle)
                docsStepData.imgDocs = R.drawable.ic_passport
                docsStepData.descriptionDocs = res.getString(R.string.photoPassportDescription)
            }

            Step.SELF_PHOTO_PASSPORT -> {
                SignupMainData.idStep = Step.SELF_PHOTO_PASSPORT
                docsStepData.title = res.getString(R.string.selfPassport)
                docsStepData.subtitle = res.getString(R.string.selfPassportTitle)
                docsStepData.imgDocs = R.drawable.ic_selfi_passport
            }

            Step.PASSPORT_ADDRESS_PHOTO -> {
                SignupMainData.idStep = Step.PASSPORT_ADDRESS_PHOTO
                docsStepData.title = res.getString(R.string.passportWithAddress)
                docsStepData.subtitle = res.getString(R.string.passportWithAddressTitle)
                docsStepData.imgDocs = R.drawable.ic_passport_address
            }

            Step.DRIVER_DOC_FRONT -> {
                SignupMainData.idStep = Step.DRIVER_DOC_FRONT
                docsStepData.title = res.getString(R.string.driverDocFront)
                docsStepData.subtitle = res.getString(R.string.driverDocFrontTitle)
                docsStepData.imgDocs = R.drawable.ic_front_drive_doc
                docsStepData.descriptionDocs = res.getString(R.string.driverDocFrontDesciption)
            }

            Step.DRIVER_DOC_BACK -> {
                SignupMainData.idStep = Step.DRIVER_DOC_BACK
                docsStepData.title = res.getString(R.string.driverDocBack)
                docsStepData.subtitle = res.getString(R.string.driverDocBackTitle)
                docsStepData.imgDocs = R.drawable.ic_back_drive_doc
                docsStepData.descriptionDocs = res.getString(R.string.driverDocBackDescription)
            }

            Step.STS_DOC_FRONT -> {
                SignupMainData.idStep = Step.STS_DOC_FRONT
                docsStepData.title = res.getString(R.string.stsDoc)
                docsStepData.subtitle = res.getString(R.string.stsDocFrontTitle)
                docsStepData.imgDocs = R.drawable.ic_front_sts
                docsStepData.descriptionDocs = res.getString(R.string.stsDocFrontDescription)
            }

            Step.STS_DOC_BACK -> {
                SignupMainData.idStep = Step.STS_DOC_BACK
                docsStepData.title = res.getString(R.string.stsDoc)
                docsStepData.subtitle = res.getString(R.string.stsDocBackTitle)
                docsStepData.imgDocs = R.drawable.ic_back_sts
                docsStepData.descriptionDocs = res.getString(R.string.stsDocBackDescription)
            }
        }

        return docsStepData
    }


    override fun instance(): SignupStepPresenter {
        return this
    }
}