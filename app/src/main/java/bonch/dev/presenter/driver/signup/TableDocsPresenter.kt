package bonch.dev.presenter.driver.signup

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import bonch.dev.Permissions
import bonch.dev.R
import bonch.dev.model.driver.signup.DriverSignupModel
import bonch.dev.model.driver.signup.pojo.DocsRealm
import bonch.dev.model.driver.signup.SignupMainData
import bonch.dev.utils.Constants
import bonch.dev.view.driver.signup.DriverSignupActivity
import bonch.dev.view.driver.signup.TableDocsView
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.table_docs_fragment.view.*


class TableDocsPresenter(val tableDocsView: TableDocsView) {


    var driverSignupModel: DriverSignupModel? = null
    lateinit var root: View


    init {
        if (driverSignupModel == null) {
            driverSignupModel = DriverSignupModel(this)
        }
    }


    fun removeStartStatus() {
        driverSignupModel?.removeStartStatus()
    }


    fun getStatusDocs(bundle: Bundle?): ArrayList<DocsRealm> {
        val listDocsStatus: ArrayList<DocsRealm> = arrayListOf()

        for (i in 0..7) {
            val docsRealm = DocsRealm()
            docsRealm.isAccess = i % 2 == 0
            listDocsStatus.add(docsRealm)
        }
        if (bundle != null) {
            //get object with data
            //TODO
        }

        return listDocsStatus
    }


    fun getDocsDB(): ArrayList<DocsRealm> {
        //TODO with server get images from net
        val listDocs = driverSignupModel!!.getDocsDB()

        if (!listDocs.isNullOrEmpty()) {
            SignupMainData.listDocs = listDocs
        }

        return listDocs
    }


    fun setDocs(list: ArrayList<DocsRealm>) {
        val viewsImgDocs = getImgDocs()
        val viewsTitleDocs = getTitleDocs()
        val viewsTicsDocs = getTicsDocs()

        if (list.isNotEmpty()) {

            val mainHandler = Handler(Looper.getMainLooper())
            val myRunnable = Runnable {
                kotlin.run {
                    try {
                        for (i in 0 until list.size) {
                            if (list[i + 1].isAccess != null) {
                                if (list[i + 1].isAccess!!) {
                                    //docs is valid
                                    viewsImgDocs[i].isClickable = false
                                    viewsTitleDocs[i].setTextColor(Color.parseColor("#149319"))
                                    viewsTicsDocs[i].setImageDrawable(
                                        ContextCompat.getDrawable(
                                            root.context,
                                            R.drawable.ic_green_tick
                                        )
                                    )
                                } else {
                                    //docs not valid
                                    viewsTitleDocs[i].setTextColor(Color.parseColor("#D03131"))
                                    viewsTicsDocs[i].setImageDrawable(
                                        ContextCompat.getDrawable(
                                            root.context,
                                            R.drawable.ic_red_cross
                                        )
                                    )
                                }
                            } else {
                                //docs still is processing
                                viewsTitleDocs[i].setTextColor(Color.parseColor("#000000"))
                                viewsTicsDocs[i].visibility = View.GONE
                            }

                            Glide.with(root.context).load(list[i + 1].imgDocs).into(viewsImgDocs[i])
                        }
                    } catch (ex: IndexOutOfBoundsException) {
                        println(ex.message)
                    }
                }
            }

            mainHandler.post(myRunnable)
        }
    }


    fun saveDocs(list: ArrayList<DocsRealm>) {
        driverSignupModel?.saveDocs(list)
    }


    fun getReshootBottomSheet(idDoc: Int) {
        SignupMainData.idStep = idDoc
        tableDocsView.reshootBottomSheet!!.state = BottomSheetBehavior.STATE_EXPANDED
    }


    fun hideReshootBottomSheet() {
        tableDocsView.reshootBottomSheet!!.state = BottomSheetBehavior.STATE_COLLAPSED
    }


    fun getCamera(fragment: Fragment) {
        Permissions.access(Constants.STORAGE_PERMISSION_REQUEST, fragment)
    }


    fun onSlideBottomSheet(slideOffset: Float) {
        val onMapView = root.on_view_table

        if (slideOffset > 0) {
            onMapView?.alpha = slideOffset * 0.8f
        }
    }


    fun onStateChangedBottomSheet(newState: Int) {
        val onMapView = root.on_view_table

        if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
            onMapView?.visibility = View.GONE
        } else {
            onMapView?.visibility = View.VISIBLE
        }
    }


    fun getImgDocs(): Array<ImageView> {
        return arrayOf(
            root.passport,
            root.self_passport,
            root.passport_address,
            root.driver_doc_front,
            root.driver_doc_back,
            root.sts_front,
            root.sts_back
        )
    }


    fun getTitleDocs(): Array<TextView> {
        return arrayOf(
            root.text_passport,
            root.text_self_passport,
            root.text_passport_address,
            root.text_driver_doc_front,
            root.text_driver_doc_back,
            root.text_sts_front,
            root.text_sts_back
        )
    }


    fun getTicsDocs(): Array<ImageView> {
        return arrayOf(
            root.status_passport,
            root.status_self_passport,
            root.status_passport_address,
            root.status_driver_doc_front,
            root.status_driver_doc_back,
            root.status_sts_front,
            root.status_sts_back
        )
    }


    fun finish(activity: DriverSignupActivity) {
        activity.finish()
    }
}