package bonch.dev.view.driver.signup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import bonch.dev.Permissions
import bonch.dev.R
import bonch.dev.model.driver.signup.pojo.SignupStep
import bonch.dev.presenter.driver.signup.TableDocsPresenter
import bonch.dev.utils.Camera
import bonch.dev.utils.Constants
import bonch.dev.utils.Constants.DRIVER_DOC_BACK
import bonch.dev.utils.Constants.DRIVER_DOC_FRONT
import bonch.dev.utils.Constants.PASSPORT_ADDRESS_PHOTO
import bonch.dev.utils.Constants.PASSPORT_PHOTO
import bonch.dev.utils.Constants.SELF_PHOTO_PASSPORT
import bonch.dev.utils.Constants.STS_DOC_BACK
import bonch.dev.utils.Constants.STS_DOC_FRONT
import bonch.dev.utils.Gallery
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.table_docs_fragment.view.*

class TableDocsView : Fragment() {


    private var tableDocsPresenter: TableDocsPresenter? = null
    var reshootBottomSheet: BottomSheetBehavior<*>? = null


    init {
        if (tableDocsPresenter == null) {
            tableDocsPresenter = TableDocsPresenter(this)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.table_docs_fragment, container, false)

        tableDocsPresenter?.root = root

        //go to process
        tableDocsPresenter?.removeStartStatus()

        tableDocsPresenter?.setStatusCheckDocs(root, arguments)

        //TODO TODO *******************************
        val activity = activity as DriverSignupActivity
        val pref = PreferenceManager.getDefaultSharedPreferences(activity.applicationContext)
        val editor = pref.edit()
        editor.putBoolean(Constants.DRIVER_SIGNUP_PROCESS.toString(), true)
        editor.apply()
        //TODO TODO *******************************


        Thread(Runnable {
            tableDocsPresenter?.driverSignupModel?.initRealm()

            if (SignupStep.listDocs.isNotEmpty()) {
                tableDocsPresenter?.setDocs(SignupStep.listDocs)

                tableDocsPresenter?.saveDocs(SignupStep.listDocs)

            } else {
                tableDocsPresenter?.getDocs()
            }

            tableDocsPresenter?.driverSignupModel?.realm?.close()
        }).start()



        setBottomSheet(root)

        setListeners(root)

        return root
    }


    private fun setBottomSheet(root: View) {
        reshootBottomSheet = BottomSheetBehavior.from<View>(root.reshoot_bottom_sheet)


        reshootBottomSheet!!.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                tableDocsPresenter?.onSlideBottomSheet(slideOffset)
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                tableDocsPresenter?.onStateChangedBottomSheet(newState)
            }
        })
    }


    fun setStatusCheckDocs(root: View) {
        //TODO
        val statusDriverSignup = root.status_driver_signup

        val status_passport = root.status_passport
        val status_passport_address = root.status_passport_address
        val status_self_passport = root.status_self_passport
        val status_driver_doc_front = root.status_driver_doc_front
        val status_driver_doc_back = root.status_driver_doc_back
        val status_sts_front = root.status_sts_front
        val status_sts_back = root.status_sts_back
    }


    private fun setListeners(root: View) {
        val passport = root.passport
        val passportAddress = root.passport_address
        val selfPassport = root.self_passport
        val driverDocFront = root.driver_doc_front
        val driverDocBack = root.driver_doc_back
        val stsFront = root.sts_front
        val stsBack = root.sts_back
        val onViewTable = root.on_view_table
        val reshoot = root.make_photo
        val clipPhoto = root.clip_photo
        val backBtn = root.back_btn



        passport.setOnClickListener {
            tableDocsPresenter?.getReshootBottomSheet(PASSPORT_PHOTO)
        }

        passportAddress.setOnClickListener {
            tableDocsPresenter?.getReshootBottomSheet(PASSPORT_ADDRESS_PHOTO)
        }

        selfPassport.setOnClickListener {
            tableDocsPresenter?.getReshootBottomSheet(SELF_PHOTO_PASSPORT)
        }

        driverDocFront.setOnClickListener {
            tableDocsPresenter?.getReshootBottomSheet(DRIVER_DOC_FRONT)
        }

        driverDocBack.setOnClickListener {
            tableDocsPresenter?.getReshootBottomSheet(DRIVER_DOC_BACK)
        }

        stsFront.setOnClickListener {
            tableDocsPresenter?.getReshootBottomSheet(STS_DOC_FRONT)
        }

        stsBack.setOnClickListener {
            tableDocsPresenter?.getReshootBottomSheet(STS_DOC_BACK)
        }

        onViewTable.setOnClickListener {
            tableDocsPresenter?.hideReshootBottomSheet()
        }

        reshoot.setOnClickListener {
            tableDocsPresenter?.getCamera(this)
        }

        clipPhoto.setOnClickListener {
            val activity = activity as DriverSignupActivity
            Gallery.getPhoto(activity)
        }

        backBtn.setOnClickListener {
            val activity = activity as DriverSignupActivity
            tableDocsPresenter?.finish(activity)
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        val activity = activity as DriverSignupActivity
        if (Permissions.isAccess(Constants.STORAGE_PERMISSION, activity)) {
            SignupStep.imgUri = Camera.getCamera(activity)
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}