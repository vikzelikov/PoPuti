package bonch.dev.presenter.driver.signup

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.Rect
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import androidx.fragment.app.FragmentManager
import bonch.dev.R
import bonch.dev.model.driver.signup.SignupStatusModel
import bonch.dev.model.driver.signup.pojo.DocsStep
import bonch.dev.model.driver.signup.pojo.SignupStep
import bonch.dev.utils.Camera
import bonch.dev.utils.Constants
import bonch.dev.utils.Constants.CAMERA
import bonch.dev.utils.Constants.DRIVER_DOC_BACK
import bonch.dev.utils.Constants.DRIVER_DOC_FRONT
import bonch.dev.utils.Constants.DRIVER_SIGNUP_CHECK_PHOTO
import bonch.dev.utils.Constants.DRIVER_SIGNUP_COMPLETE
import bonch.dev.utils.Constants.DRIVER_SIGNUP_DOCS_VIEW
import bonch.dev.utils.Constants.DRIVER_SIGNUP_PROCESS
import bonch.dev.utils.Constants.DRIVER_SIGNUP_START
import bonch.dev.utils.Constants.GALLERY
import bonch.dev.utils.Constants.PASSPORT_ADDRESS_PHOTO
import bonch.dev.utils.Constants.PASSPORT_PHOTO
import bonch.dev.utils.Constants.SELF_PHOTO_PASSPORT
import bonch.dev.utils.Constants.STS_DOC_BACK
import bonch.dev.utils.Constants.STS_DOC_FRONT
import bonch.dev.utils.Constants.USER_PHOTO
import bonch.dev.utils.Coordinator.replaceFragment
import bonch.dev.view.driver.signup.DriverSignupActivity
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.check_correct_doc_fragment.view.*
import kotlinx.android.synthetic.main.signup_car_info_fragment.view.*

class DriverSignupPresenter(val driverSignupActivity: DriverSignupActivity?) {


    var signupStatusModel: SignupStatusModel? = null


    init {
        if (driverSignupActivity != null && signupStatusModel == null) {
            signupStatusModel = SignupStatusModel(this)
        }
    }


    fun getStatusSignup() {
        signupStatusModel?.getStatusSignup()
    }


    fun receiveStatus(status: Int) {
        val fm = driverSignupActivity!!.supportFragmentManager

        when (status) {
            DRIVER_SIGNUP_START -> {
                getListDocsView(fm)
            }

            DRIVER_SIGNUP_PROCESS -> {
                getTableDocs(fm)
            }

            DRIVER_SIGNUP_COMPLETE -> {
                //go to driver interface
            }
        }
    }


    fun getListDocsView(fm: FragmentManager) {
        replaceFragment(DRIVER_SIGNUP_DOCS_VIEW, null, fm)
    }


    fun startDriverSignup(fm: FragmentManager) {
        replaceFragment(Constants.DRIVER_SIGNUP_CAR_INFO, null, fm)
    }


    fun startSettingDocs(fm: FragmentManager) {
        replaceFragment(Constants.DRIVER_SIGNUP_STEP_VIEW, null, fm)
    }


    fun getTableDocs(fm: FragmentManager) {
        SignupStep.isTableView = true
        replaceFragment(Constants.DRIVER_SIGNUP_TABLE_DOCS, null, fm)
    }


    fun getCamera(activity: Activity) {
        SignupStep.imgUri = Camera.getCamera(activity)
    }


    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?, fm: FragmentManager) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GALLERY) {
                SignupStep.imgUri = data?.data
            }

            replaceFragment(DRIVER_SIGNUP_CHECK_PHOTO, null, fm)

        } else {
            if (SignupStep.isTableView) {
                getTableDocs(fm)
            } else {
                startSettingDocs(fm)
            }
        }
    }


    fun receiveDataForCheck(root: View) {
        val title = root.title_check_photo
        val photoContainer = root.photo_container

        title.text = getTitleForCheckPhoto(SignupStep.idStep)
        Glide.with(root.context).load(SignupStep.imgUri).into(photoContainer)
    }


    fun getNextStep(activity: DriverSignupActivity) {
        val fm = activity.supportFragmentManager
        //TODO holder

        //save photo
        if (SignupStep.imgUri != null) {

            try {
                SignupStep.listDocs[SignupStep.idStep] = SignupStep.imgUri!!
            } catch (ex: IndexOutOfBoundsException) {
                SignupStep.listDocs.add(SignupStep.imgUri!!)
            }
        }

        if (SignupStep.isTableView || SignupStep.idStep > 1 - 1) {
            //end settings docs
            getTableDocs(fm)
        } else {
            //go to the next step
            SignupStep.idStep = SignupStep.idStep.inc()
            startSettingDocs(fm)
        }
    }


    fun getNextStepDocs(idStep: Int): DocsStep {
        val docsStepData = DocsStep()

        when (idStep) {
            USER_PHOTO -> {
                SignupStep.idStep = USER_PHOTO
                docsStepData.title = "Ваше фото"
                docsStepData.subtitle = "Ваше фото будет видно пассажирам"
                docsStepData.imgDocs = R.drawable.ic_photo
            }

            PASSPORT_PHOTO -> {
                SignupStep.idStep = PASSPORT_PHOTO
                docsStepData.title = "Фото паспорта"
                docsStepData.subtitle =
                    "Убедитесь, что четко видно фото, номер паспорта, фио и другие данные"
                docsStepData.imgDocs = R.drawable.ic_passport
                docsStepData.descriptionDocs = "Разворот паспорта с фотографией"
            }

            SELF_PHOTO_PASSPORT -> {
                SignupStep.idStep = SELF_PHOTO_PASSPORT
                docsStepData.title = "Селфи с паспортом"
                docsStepData.subtitle =
                    "Убедитесь, что четко видно фото, номер паспорта, фио и другие данные"
                docsStepData.imgDocs = R.drawable.ic_selfi_passport
            }

            PASSPORT_ADDRESS_PHOTO -> {
                SignupStep.idStep = PASSPORT_ADDRESS_PHOTO
                docsStepData.title = "Паспорт с пропиской"
                docsStepData.subtitle =
                    "Убедитесь, что четко видно номер и серию паспорта и информацию о прописке"
                docsStepData.imgDocs = R.drawable.ic_passport_address
            }

            DRIVER_DOC_FRONT -> {
                SignupStep.idStep = DRIVER_DOC_FRONT
                docsStepData.title = "Водительское удостоверение"
                docsStepData.subtitle =
                    "Убедитесь, что четко видно фото, ФИО, дату выдачи и действия документа"
                docsStepData.imgDocs = R.drawable.ic_front_drive_doc
                docsStepData.descriptionDocs = "Лицевая сторона водительского удостоверения"
            }

            DRIVER_DOC_BACK -> {
                SignupStep.idStep = DRIVER_DOC_BACK
                docsStepData.title = "Обратная сторона ВУ"
                docsStepData.subtitle =
                    "Убедитесь, что четко видно номер водительского удастоверения и категорию прав"
                docsStepData.imgDocs = R.drawable.ic_back_drive_doc
                docsStepData.descriptionDocs = "Обратная сторона водительского удостоверения"
            }

            STS_DOC_FRONT -> {
                SignupStep.idStep = STS_DOC_FRONT
                docsStepData.title = "Свидетельство о регистраци транспортного средства"
                docsStepData.subtitle =
                    "Убедитесь, что четко видно VIN, номер документа и другие данные"
                docsStepData.imgDocs = R.drawable.ic_front_sts
                docsStepData.descriptionDocs = "Лицевая сторона СТС"
            }

            STS_DOC_BACK -> {
                SignupStep.idStep = STS_DOC_BACK
                docsStepData.title = "Свидетельство о регистраци транспортного средства"
                docsStepData.subtitle = "Убедитесь, что четко видно номер документа и другие данные"
                docsStepData.imgDocs = R.drawable.ic_back_sts
                docsStepData.descriptionDocs = "Обратная сторона СТС"
            }
        }

        return docsStepData
    }


    private fun getTitleForCheckPhoto(idStep: Int?): String? {
        var title: String? = null
        when (idStep) {
            USER_PHOTO -> {
                title = "Убедитесь, что видно Ваше лицо"
            }

            PASSPORT_PHOTO -> {
                title = "Убедитесь, что нет бликов, четко видно фото, ФИО, номер и серию паспорта"
            }

            SELF_PHOTO_PASSPORT -> {
                title = "Убедитесь, что нет бликов, четко видно фото, ФИО и другие данные"
            }

            PASSPORT_ADDRESS_PHOTO -> {
                title =
                    "Убедитесь, что нет бликов, четко видно номер паспорта и информацию о прописке"
            }

            DRIVER_DOC_FRONT -> {
                title =
                    "Убедитесь, что нет бликов, четко видно фото, ФИО, дату выдачи и действия документа"
            }

            DRIVER_DOC_BACK -> {
                title =
                    "Убедитесь, что нет бликов, четко видно номер водительского удостоверения и категорию прав"
            }

            STS_DOC_FRONT -> {
                title = "Убедитесь, что четко видно VIN, номер документа и другие данные"
            }

            STS_DOC_BACK -> {
                title = "Убедитесь, что четко видно номер документа и другие данные"
            }
        }

        return title
    }


    fun isCarInfoEntered(root: View): Boolean {
        var result = false

        val carName = root.car_name
        val carModel = root.car_model
        val carNumber = root.car_number
        val nextBtn = root.next_btn

        if (carName.text.toString().trim().isNotEmpty() &&
            carModel.text.toString().trim().isNotEmpty() &&
            carNumber.text.toString().trim().isNotEmpty()
        ) {
            changeBtnEnable(true, nextBtn)
            result = true
        } else {
            changeBtnEnable(false, nextBtn)
        }


        return result
    }


    private fun changeBtnEnable(enable: Boolean, nextBtn: Button) {
        if (enable) {
            nextBtn.setBackgroundResource(R.drawable.bg_btn_blue)
        } else {
            nextBtn.setBackgroundResource(R.drawable.bg_btn_gray)
        }
    }


    fun setMovingButtonListener(root: View) {
        val nextBtn = root.next_btn
        var heightDiff: Int
        var btnDefaultPosition = 0.0f
        val rect = Rect()
        var startHeight = 0
        var screenHeight = 0

        root.viewTreeObserver
            .addOnGlobalLayoutListener {

                root.getWindowVisibleDisplayFrame(rect)
                heightDiff = screenHeight - (rect.bottom - rect.top)

                if (screenHeight == 0) {
                    screenHeight = root.rootView.height
                }

                if (btnDefaultPosition == 0.0f) {
                    //init default position of button
                    btnDefaultPosition = nextBtn.y
                }

                if (startHeight == 0) {
                    startHeight = screenHeight - (rect.bottom - rect.top)
                }

                if (heightDiff > startHeight) {
                    nextBtn.y = btnDefaultPosition - heightDiff + startHeight
                } else {
                    //move DOWN
                    nextBtn.y = btnDefaultPosition
                }

            }
    }


    fun clearData() {
        SignupStep.idStep = 0
        SignupStep.imgUri = null
        SignupStep.listDocs = arrayListOf()
    }


    fun finish(activity: DriverSignupActivity) {
        activity.finish()
    }
}