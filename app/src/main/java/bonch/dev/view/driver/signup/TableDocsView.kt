package bonch.dev.view.driver.signup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import bonch.dev.Permissions
import bonch.dev.R
import bonch.dev.model.driver.signup.SignupMainData
import bonch.dev.model.driver.signup.pojo.DocsRealm
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

        //TODO TODO *******************************
        val activity = activity as DriverSignupActivity
        val pref = PreferenceManager.getDefaultSharedPreferences(activity.applicationContext)
        val editor = pref.edit()
        editor.putBoolean(Constants.DRIVER_SIGNUP_PROCESS.toString(), true)
        editor.apply()
        //TODO TODO *******************************

        Thread(Runnable {
            var listDocs: ArrayList<DocsRealm> = arrayListOf()
            val listStatus = tableDocsPresenter?.getStatusDocs(arguments)

            tableDocsPresenter?.driverSignupModel?.initRealm()

            if (SignupMainData.listDocs.isNotEmpty()) {
                tableDocsPresenter?.saveDocs(SignupMainData.listDocs)
            } else {
                listDocs = tableDocsPresenter!!.getDocsDB()

                //merge docs and status docs in one mas
                listStatus?.forEach {
                    for (i in 0 until listDocs.size) {
                        listDocs[i].isAccess = it.isAccess
                    }
                }
            }

            tableDocsPresenter?.setDocs(listDocs)

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


    private fun setListeners(root: View) {
        val onViewTable = root.on_view_table
        val reshoot = root.make_photo
        val clipPhoto = root.clip_photo
        val backBtn = root.back_btn

        val viewsImgDocs: Array<ImageView> = arrayOf(
            root.passport,
            root.self_passport,
            root.passport_address,
            root.driver_doc_front,
            root.driver_doc_back,
            root.sts_front,
            root.sts_back
        )

        for(i in viewsImgDocs.indices){
            viewsImgDocs[i].setOnClickListener{
                tableDocsPresenter?.getReshootBottomSheet(i + 1)
            }
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
            SignupMainData.imgUri = Camera.getCamera(activity)
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}