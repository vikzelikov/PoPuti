package bonch.dev.presenter.driver.signup

import android.app.Activity
import android.content.Intent
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import bonch.dev.Permissions
import bonch.dev.R
import bonch.dev.model.driver.signup.SignupStatusModel
import bonch.dev.model.driver.signup.pojo.DocsStep
import bonch.dev.model.driver.signup.SignupMainData
import bonch.dev.model.driver.signup.pojo.DocsRealm
import bonch.dev.utils.Camera
import bonch.dev.utils.Constants
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
import kotlinx.android.synthetic.main.check_doc_fragment.view.*
import kotlinx.android.synthetic.main.signup_car_info_fragment.view.*

class DriverSignupPresenter(val driverSignupActivity: DriverSignupActivity?) {


    private var signupStatusModel: SignupStatusModel? = null


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
        SignupMainData.isTableView = true
        replaceFragment(Constants.DRIVER_SIGNUP_TABLE_DOCS, null, fm)
    }


    fun getCamera(fragment: Fragment) {
        //for correct getting camera
        val activity = (fragment.activity as DriverSignupActivity)
        if(Permissions.isAccess(Constants.STORAGE_PERMISSION, activity)){
            SignupMainData.imgUri = Camera.getCamera(activity).toString()
        }else{
            Permissions.access(Constants.STORAGE_PERMISSION_REQUEST, fragment)
        }
    }


    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?, fm: FragmentManager) {
        //MODE_PRIVATE - it is flag of suggest car info
        if(resultCode != Activity.MODE_PRIVATE){
            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == GALLERY) {
                    SignupMainData.imgUri = data?.data.toString()
                }

                replaceFragment(DRIVER_SIGNUP_CHECK_PHOTO, null, fm)

            } else {
                if (SignupMainData.isTableView) {
                    getTableDocs(fm)
                } else {
                    startSettingDocs(fm)
                }
            }
        }
    }


    fun receiveDataForCheck(root: View) {
        val title = root.title_check_photo
        val photoContainer = root.photo_container

        title.text = getTitleForCheckPhoto(SignupMainData.idStep)
        Glide.with(root.context).load(SignupMainData.imgUri).into(photoContainer)
    }


    fun getNextStep(activity: DriverSignupActivity) {
        val fm = activity.supportFragmentManager
        //TODO holder block btn

        //save photo
        if (SignupMainData.imgUri != null) {
            try {
                if (SignupMainData.isTableView) {
                    //remake photo (insert)
                    SignupMainData.listDocs[SignupMainData.idStep].imgDocs = SignupMainData.imgUri!!
                    SignupMainData.listDocs[SignupMainData.idStep].isAccess = null
                } else {
                    //start signup (add)
                    val docsRealm = DocsRealm(SignupMainData.idStep, SignupMainData.imgUri!!)
                    SignupMainData.listDocs.add(docsRealm)
                }
            } catch (ex: IndexOutOfBoundsException) {
                println(ex.message)
            }
        }

        if (SignupMainData.isTableView || SignupMainData.idStep > STS_DOC_BACK - 1) {
            //end settings docs
            getTableDocs(fm)
        } else {
            SignupMainData.idStep = SignupMainData.idStep.inc()
            startSettingDocs(fm)
        }
    }


    fun getNextStepDocs(idStep: Int): DocsStep {
        val docsStepData = DocsStep()

        when (idStep) {
            USER_PHOTO -> {
                SignupMainData.idStep = USER_PHOTO
                docsStepData.title = "Ваше фото"
                docsStepData.subtitle = "Ваше фото будет видно пассажирам"
                docsStepData.imgDocs = R.drawable.ic_photo
            }

            PASSPORT_PHOTO -> {
                SignupMainData.idStep = PASSPORT_PHOTO
                docsStepData.title = "Фото паспорта"
                docsStepData.subtitle =
                    "Убедитесь, что четко видно фото, номер паспорта, фио и другие данные"
                docsStepData.imgDocs = R.drawable.ic_passport
                docsStepData.descriptionDocs = "Разворот паспорта с фотографией"
            }

            SELF_PHOTO_PASSPORT -> {
                SignupMainData.idStep = SELF_PHOTO_PASSPORT
                docsStepData.title = "Селфи с паспортом"
                docsStepData.subtitle =
                    "Убедитесь, что четко видно фото, номер паспорта, фио и другие данные"
                docsStepData.imgDocs = R.drawable.ic_selfi_passport
            }

            PASSPORT_ADDRESS_PHOTO -> {
                SignupMainData.idStep = PASSPORT_ADDRESS_PHOTO
                docsStepData.title = "Паспорт с пропиской"
                docsStepData.subtitle =
                    "Убедитесь, что четко видно номер и серию паспорта и информацию о прописке"
                docsStepData.imgDocs = R.drawable.ic_passport_address
            }

            DRIVER_DOC_FRONT -> {
                SignupMainData.idStep = DRIVER_DOC_FRONT
                docsStepData.title = "Водительское удостоверение"
                docsStepData.subtitle =
                    "Убедитесь, что четко видно фото, ФИО, дату выдачи и действия документа"
                docsStepData.imgDocs = R.drawable.ic_front_drive_doc
                docsStepData.descriptionDocs = "Лицевая сторона водительского удостоверения"
            }

            DRIVER_DOC_BACK -> {
                SignupMainData.idStep = DRIVER_DOC_BACK
                docsStepData.title = "Обратная сторона ВУ"
                docsStepData.subtitle =
                    "Убедитесь, что четко видно номер водительского удастоверения и категорию прав"
                docsStepData.imgDocs = R.drawable.ic_back_drive_doc
                docsStepData.descriptionDocs = "Обратная сторона водительского удостоверения"
            }

            STS_DOC_FRONT -> {
                SignupMainData.idStep = STS_DOC_FRONT
                docsStepData.title = "Свидетельство о регистраци транспортного средства"
                docsStepData.subtitle =
                    "Убедитесь, что четко видно VIN, номер документа и другие данные"
                docsStepData.imgDocs = R.drawable.ic_front_sts
                docsStepData.descriptionDocs = "Лицевая сторона СТС"
            }

            STS_DOC_BACK -> {
                SignupMainData.idStep = STS_DOC_BACK
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


    fun clearData() {
        SignupMainData.idStep = 0
        SignupMainData.imgUri = null
        SignupMainData.listDocs = arrayListOf()
    }


    fun finish(activity: DriverSignupActivity) {
        activity.finish()
    }
}