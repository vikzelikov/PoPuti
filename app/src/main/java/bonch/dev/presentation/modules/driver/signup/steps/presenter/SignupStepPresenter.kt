package bonch.dev.presentation.modules.driver.signup.steps.presenter

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import androidx.fragment.app.Fragment
import bonch.dev.App
import bonch.dev.Permissions
import bonch.dev.R
import bonch.dev.domain.entities.driver.signup.Docs
import bonch.dev.domain.entities.driver.signup.SignupMainData
import bonch.dev.domain.entities.driver.signup.SignupMainData.idStep
import bonch.dev.domain.entities.driver.signup.SignupMainData.imgUri
import bonch.dev.domain.entities.driver.signup.SignupMainData.listDocs
import bonch.dev.domain.entities.driver.signup.SignupStep
import bonch.dev.domain.entities.driver.signup.Step
import bonch.dev.domain.interactor.driver.signup.ISignupInteractor
import bonch.dev.domain.utils.Camera
import bonch.dev.domain.utils.Constants.GALLERY
import bonch.dev.presentation.base.BasePresenter
import bonch.dev.presentation.modules.common.checkphoto.CheckPhotoView
import bonch.dev.presentation.modules.common.media.IMediaEvent
import bonch.dev.presentation.modules.common.media.MediaEvent
import bonch.dev.presentation.modules.driver.signup.SignupComponent
import bonch.dev.presentation.modules.driver.signup.steps.view.ISignupStepView
import bonch.dev.presentation.modules.driver.signup.tabledocs.presenter.TitlePhoto
import bonch.dev.route.MainRouter
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

                nextStep()

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
        //save photo
        if (imgUri != null) {
            try {
                if (idStep == Step.USER_PHOTO) {
                    //todo sent this photo to profile
                } else {
                    val docs = Docs()
                    docs.id = idStep.step
                    docs.imgDocs = imgUri
                    listDocs.add(docs)

                    //sent photo to server
                    sentPhoto(docs)
                }
            } catch (ex: IndexOutOfBoundsException) {
            }
        }

        if (idStep.step > Step.STS_DOC_BACK.step - 1) {
            //end settings docs
            MainRouter.showView(R.id.show_table_docs_view, getView()?.getNavHost(), null)
        } else {
            //next step
            val id = getByValue(idStep.step.inc())
            if (id != null) {
                idStep = id
                val signupData = getStepData(idStep)
                getView()?.setDataStep(signupData)
            }
        }
    }


    //todo remove comment code
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
                    //val exifOrientation = mediaEvent.getOrientation(uri)

                    //compress and convert to JPEG
                    val file = mediaEvent.convertImage(btm, it)

                    if (file != null) {
                        //save orientation
//                        if (exifOrientation != null) {
//                            val newExif = ExifInterface(file)
//                            newExif.setAttribute(ExifInterface.TAG_ORIENTATION, exifOrientation)
//                            newExif.saveAttributes()
//                        }

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