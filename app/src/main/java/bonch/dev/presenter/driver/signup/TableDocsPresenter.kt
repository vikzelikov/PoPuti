package bonch.dev.presenter.driver.signup

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.View
import androidx.preference.PreferenceManager
import bonch.dev.model.driver.signup.DriverSignupModel
import bonch.dev.model.driver.signup.pojo.DocsRealm
import bonch.dev.model.driver.signup.pojo.SignupStep
import bonch.dev.utils.Camera
import bonch.dev.utils.Constants
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


    fun checkStatusSignup(){
        setStatus()
    }


    private fun setStatus() {
        val activity = tableDocsView.activity as DriverSignupActivity
        val pref = PreferenceManager.getDefaultSharedPreferences(activity.applicationContext)
        val editor = pref.edit()
        editor.putBoolean(Constants.DRIVER_SIGNUP_PROCESS.toString(), true)
        editor.apply()
    }


    fun getDocs() {
        val list: ArrayList<Bitmap> = arrayListOf()
        val listDocs = driverSignupModel?.getDocs()

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


    fun saveDocs(list: ArrayList<Bitmap>) {
        val listDocs: ArrayList<DocsRealm> = arrayListOf()

        for (i in 0 until list.size) {
            val byteArray = getByteArray(list[i])
            listDocs.add(DocsRealm(byteArray))
        }

        driverSignupModel?.saveDocs(listDocs)
    }


    fun setDocs(list: ArrayList<Bitmap>) {
        //TODO
        if (list.isNotEmpty()) {
            try {
                Glide.with(root.context).load(list[1]).into(root.doc1)
                Glide.with(root.context).load(list[2]).into(root.doc2)
                Glide.with(root.context).load(list[3]).into(root.doc3)
                Glide.with(root.context).load(list[4]).into(root.doc4)
                Glide.with(root.context).load(list[5]).into(root.doc5)
                Glide.with(root.context).load(list[6]).into(root.doc6)
                Glide.with(root.context).load(list[7]).into(root.doc7)
            } catch (ex: IndexOutOfBoundsException) {
                println(ex.message)
            }
        }
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
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)

        return stream.toByteArray()
    }


    fun finish(activity: DriverSignupActivity) {
        activity.finish()
    }
}