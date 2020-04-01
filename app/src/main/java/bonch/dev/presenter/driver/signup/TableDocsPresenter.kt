package bonch.dev.presenter.driver.signup

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import bonch.dev.model.driver.signup.DriverSignupModel
import bonch.dev.model.driver.signup.pojo.DocsRealm
import bonch.dev.model.driver.signup.pojo.SignupStep
import bonch.dev.utils.Camera
import bonch.dev.view.driver.signup.DriverSignupActivity
import bonch.dev.view.driver.signup.TableDocsView
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.table_docs_fragment.view.*
import java.io.ByteArrayOutputStream


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


    fun setStatusCheckDocs(root: View, bundle: Bundle?) {
        if (bundle != null) {
            //get object with data

            tableDocsView.setStatusCheckDocs(root)
        }
    }


    fun getDocs() {
        //TODO with server get images from net
        val list: ArrayList<Bitmap> = arrayListOf()
        val listDocs = driverSignupModel?.getDocsDB()

        if (!listDocs.isNullOrEmpty()) {
            for (i in 0 until listDocs.size) {
                val bitmap =
                    BitmapFactory.decodeByteArray(
                        listDocs[i].imgDocs,
                        0,
                        listDocs[i].imgDocs!!.size
                    )

                list.add(bitmap)

            }

            SignupStep.listDocs = list

            setDocs(list)
        }
    }


    fun setDocs(list: ArrayList<Bitmap>) {
        val views: Array<ImageView> = arrayOf(
            root.passport,
            root.passport_address,
            root.self_passport,
            root.driver_doc_front,
            root.driver_doc_back,
            root.sts_front,
            root.sts_back
        )

        if (list.isNotEmpty()) {

            val mainHandler = Handler(Looper.getMainLooper())
            val myRunnable = Runnable {
                kotlin.run {
                    try {
                        for (i in 0 until list.size) {
                            Glide.with(root.context).load(list[i + 1]).into(views[i])
                        }
                    } catch (ex: IndexOutOfBoundsException) {
                        println(ex.message)
                    }
                }
            }

            mainHandler.post(myRunnable)
        }
    }


    fun saveDocs(list: ArrayList<Bitmap>) {
        val listDocs: ArrayList<DocsRealm> = arrayListOf()

        for (i in 0 until list.size) {
            val byteArray = getByteArray(list[i])
            listDocs.add(DocsRealm(i, byteArray))
        }

        driverSignupModel?.saveDocs(listDocs)
    }


    fun getReshootBottomSheet(idDoc: Int) {
        SignupStep.idStep = idDoc
        tableDocsView.reshootBottomSheet!!.state = BottomSheetBehavior.STATE_EXPANDED
    }


    fun hideReshootBottomSheet() {
        tableDocsView.reshootBottomSheet!!.state = BottomSheetBehavior.STATE_COLLAPSED
    }


    fun getCamera(activity: Activity) {
        SignupStep.imgUri = Camera.getCamera(activity)
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


    private fun getByteArray(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream)

        return stream.toByteArray()
    }


    fun finish(activity: DriverSignupActivity) {
        activity.finish()
    }
}