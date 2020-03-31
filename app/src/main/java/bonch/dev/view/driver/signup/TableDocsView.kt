package bonch.dev.view.driver.signup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import bonch.dev.R
import bonch.dev.model.driver.signup.pojo.SignupStep
import bonch.dev.presenter.driver.signup.TableDocsPresenter
import bonch.dev.utils.Constants.DRIVER_DOC_BACK
import bonch.dev.utils.Constants.DRIVER_DOC_FRONT
import bonch.dev.utils.Constants.PASSPORT_ADDRESS_PHOTO
import bonch.dev.utils.Constants.PASSPORT_PHOTO
import bonch.dev.utils.Constants.SELF_PHOTO_PASSPORT
import bonch.dev.utils.Constants.STS_DOC_BACK
import bonch.dev.utils.Constants.STS_DOC_FRONT
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.table_docs_fragment.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

        tableDocsPresenter?.checkStatusSignup()

        if (SignupStep.listDocs.isNotEmpty()) {
            tableDocsPresenter?.setDocs(SignupStep.listDocs)

            CoroutineScope(Dispatchers.IO).launch {
                withContext(Dispatchers.Main) {
                    tableDocsPresenter?.saveDocs(SignupStep.listDocs)
                }
            }

        } else {
            tableDocsPresenter?.getDocs()
        }


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
        val passport = root.doc1
        val passportAddress = root.doc2
        val selfPassport = root.doc3
        val driverDocFront = root.doc4
        val driverDocBack = root.doc5
        val stsFront = root.doc6
        val stsBack = root.doc7
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
            val activity = activity as DriverSignupActivity
            tableDocsPresenter?.getCamera(activity)
        }

        clipPhoto.setOnClickListener {
            //todo
        }

        backBtn.setOnClickListener {
            val activity = activity as DriverSignupActivity
            tableDocsPresenter?.finish(activity)
        }
    }


    override fun onDestroy() {
        tableDocsPresenter?.driverSignupModel?.realm?.close()
        super.onDestroy()
    }

}