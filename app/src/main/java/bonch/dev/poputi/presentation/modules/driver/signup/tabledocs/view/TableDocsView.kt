package bonch.dev.poputi.presentation.modules.driver.signup.tabledocs.view

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import bonch.dev.poputi.Permissions
import bonch.dev.poputi.R
import bonch.dev.poputi.domain.entities.common.media.Photo
import bonch.dev.poputi.domain.entities.driver.signup.SignupMainData
import bonch.dev.poputi.domain.entities.driver.signup.Step
import bonch.dev.domain.utils.Camera
import bonch.dev.domain.utils.Gallery
import bonch.dev.poputi.presentation.modules.driver.signup.DriverSignupActivity
import bonch.dev.poputi.presentation.modules.driver.signup.SignupComponent
import bonch.dev.poputi.presentation.modules.driver.signup.tabledocs.presenter.ITableDocsPresenter
import com.bumptech.glide.Glide
import com.ethanhua.skeleton.Skeleton
import com.ethanhua.skeleton.ViewSkeletonScreen
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.table_docs_fragment.*
import javax.inject.Inject

class TableDocsView : Fragment(), ITableDocsView {

    @Inject
    lateinit var tableDocsPresenter: ITableDocsPresenter

    private var reshootBottomSheet: BottomSheetBehavior<*>? = null

    private var imgDocsSkeletons: ArrayList<ViewSkeletonScreen> = arrayListOf()
    private var textDocsSkeletons: ArrayList<ViewSkeletonScreen> = arrayListOf()


    init {
        SignupComponent.driverSignupComponent?.inject(this)

        tableDocsPresenter.instance().attachView(this)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.table_docs_fragment, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        showSkeletonAnim()

        tableDocsPresenter.createDriver()

        loadPhoto()

        setBottomSheet()

        setListeners()

        super.onViewCreated(view, savedInstanceState)
    }


    override fun loadPhoto() {
        Thread(Runnable {
            tableDocsPresenter.sortDocs()

            val listDocs: ArrayList<Photo> = SignupMainData.listDocs
            setDocs(listDocs)
        }).start()
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        context?.let {
            if (Permissions.isAccess(Permissions.STORAGE_PERMISSION, it)) {
                SignupMainData.imgUri = Camera.getCamera(this).toString()
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        tableDocsPresenter.onActivityResult(this, requestCode, resultCode, data)

        hideReshootBottomSheet()

        enableButtons()
    }


    private fun setDocs(list: ArrayList<Photo>) {
        val viewsImgDocs = getImgDocs()
        val viewsTitleDocs = getTitleDocs()
        val viewsTicsDocs = getTicsDocs()

        val arrTics = arrayListOf<Int>()

        if (list.isNotEmpty()) {
            val mainHandler = Handler(Looper.getMainLooper())
            val myRunnable = Runnable {
                kotlin.run {
                    try {
                        for (i in 0 until list.size) {
                            val isVerify = list[i].isVerify

                            if (isVerify != 0) {

                                val context = viewsTicsDocs[i]?.context
                                arrTics.add(i)

                                if (isVerify == 1) {
                                    //docs is valid
                                    viewsImgDocs[i]?.isClickable = false
                                    viewsTitleDocs[i]?.setTextColor(Color.parseColor("#149319"))
                                    context?.let {
                                        viewsTicsDocs[i]?.setImageDrawable(
                                            ContextCompat.getDrawable(
                                                it,
                                                R.drawable.ic_green_tick
                                            )
                                        )
                                    }
                                } else {
                                    //docs not valid
                                    status_driver_signup?.text =
                                        resources.getText(R.string.someDocsNotValid)
                                    status_driver_signup?.setTextColor(Color.parseColor("#D03131"))
                                    viewsTitleDocs[i]?.setTextColor(Color.parseColor("#D03131"))
                                    context?.let {
                                        viewsTicsDocs[i]?.setImageDrawable(
                                            ContextCompat.getDrawable(
                                                it,
                                                R.drawable.ic_red_cross
                                            )
                                        )
                                    }
                                }
                            } else {
                                //docs still is processing
                                viewsTitleDocs[i]?.setTextColor(Color.parseColor("#000000"))
                                viewsTicsDocs[i]?.visibility = View.GONE

                                //set deafult status
                                status_driver_signup?.text = resources.getText(R.string.weCheckDocs)
                                status_driver_signup?.setTextColor(Color.parseColor("#1152FD"))
                            }

                            val image = if (list[i].imgDocs != null) {
                                list[i].imgDocs
                            } else {
                                list[i].imgUrl
                            }

                            view?.context?.let { context ->
                                viewsImgDocs[i]?.let {
                                    Glide.with(context).load(image)
                                        .into(it)
                                }
                            }
                        }
                    } catch (ex: IndexOutOfBoundsException) {

                    }

                    Handler().postDelayed({
                        hideSkeletonAnim(arrTics)
                    }, 2000)
                }
            }

            mainHandler.post(myRunnable)
        }
    }


    private fun setBottomSheet() {
        reshootBottomSheet = BottomSheetBehavior.from<View>(reshoot_bottom_sheet)

        reshootBottomSheet?.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                onSlideBottomSheet(slideOffset)
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                onStateChangedBottomSheet(newState)
            }
        })
    }


    override fun setListeners() {
        val viewsImgDocs = getImgDocs()

        for (i in viewsImgDocs.indices) {
            viewsImgDocs[i]?.setOnClickListener {
                val id = tableDocsPresenter.getByValue(i)
                if (id != null && !tableDocsPresenter.isBlockBack()) {
                    getReshootBottomSheet(id)
                }
            }
        }

        on_view_table.setOnClickListener {
            hideReshootBottomSheet()
        }

        make_photo.setOnClickListener {
            tableDocsPresenter.getCamera(this)
            blockButtons()
        }

        clip_photo.setOnClickListener {
            Gallery.getPhoto(this)
            blockButtons()
        }

        back_btn.setOnClickListener {
            (activity as? DriverSignupActivity)?.onBackPressed()
        }
    }


    private fun onSlideBottomSheet(slideOffset: Float) {
        if (slideOffset > 0) {
            on_view_table.alpha = slideOffset * 0.8f
        }
    }


    private fun onStateChangedBottomSheet(newState: Int) {
        if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
            on_view_table.visibility = View.GONE
        } else {
            on_view_table.visibility = View.VISIBLE
        }
    }


    override fun getNavHost(): NavController? {
        return (activity as? DriverSignupActivity)?.navController
    }


    override fun hideKeyboard() {}


    private fun hideReshootBottomSheet() {
        reshootBottomSheet?.state = BottomSheetBehavior.STATE_COLLAPSED
    }


    private fun getReshootBottomSheet(idDoc: Step) {
        SignupMainData.idStep = idDoc
        reshootBottomSheet?.state = BottomSheetBehavior.STATE_EXPANDED
    }


    override fun getImgDocs(): Array<ImageView?> {
        return arrayOf(
            passport,
            self_passport,
            passport_address,
            driver_doc_front,
            driver_doc_back,
            sts_front,
            sts_back
        )
    }


    override fun getTitleDocs(): Array<TextView?> {
        return arrayOf(
            text_passport,
            text_self_passport,
            text_passport_address,
            text_driver_doc_front,
            text_driver_doc_back,
            text_sts_front,
            text_sts_back
        )
    }


    override fun getTicsDocs(): Array<ImageView?> {
        return arrayOf(
            status_passport,
            status_self_passport,
            status_passport_address,
            status_driver_doc_front,
            status_driver_doc_back,
            status_sts_front,
            status_sts_back
        )
    }


    override fun showNotification(text: String) {
        (activity as? DriverSignupActivity)?.showNotification(text)
    }


    override fun showLoading() {
        (activity as? DriverSignupActivity)?.showLoading()
    }


    override fun hideLoading() {
        (activity as? DriverSignupActivity)?.hideLoading()
    }


    private fun blockButtons() {
        make_photo?.isClickable = false
        make_photo?.isFocusable = false

        clip_photo?.isClickable = false
        clip_photo?.isFocusable = false
    }


    private fun enableButtons() {
        make_photo?.isClickable = true
        make_photo?.isFocusable = true

        clip_photo?.isClickable = true
        clip_photo?.isFocusable = true
    }


    fun onBackPressed(): Boolean {
        return if (!tableDocsPresenter.isBlockBack()) {
            true
        } else {
            showNotification(resources.getString(R.string.photoProfileLoading))
            false
        }
    }


    override fun showLoadingPhoto(idDoc: Step) {
        val mainHandler = Handler(Looper.getMainLooper())
        val myRunnable = Runnable {
            kotlin.run {
                val photo: Pair<View, View> = when (idDoc) {
                    Step.USER_PHOTO -> Pair(loading_passport, passport)
                    Step.PASSPORT_PHOTO -> Pair(loading_passport, passport)
                    Step.SELF_PHOTO_PASSPORT -> Pair(loading_self_passport, self_passport)
                    Step.PASSPORT_ADDRESS_PHOTO -> Pair(loading_address, passport_address)
                    Step.DRIVER_DOC_FRONT -> Pair(loading_driver_doc_front, driver_doc_front)
                    Step.DRIVER_DOC_BACK -> Pair(loading_driver_doc_back, driver_doc_back)
                    Step.STS_DOC_FRONT -> Pair(loading_sts_front, sts_front)
                    Step.STS_DOC_BACK -> Pair(loading_sts_back, sts_back)
                }

                photo.first.visibility = View.VISIBLE
                photo.second.alpha = 0.7f
            }
        }

        mainHandler.post(myRunnable)
    }


    override fun hideLoadingPhoto() {
        val mainHandler = Handler(Looper.getMainLooper())
        val myRunnable = Runnable {
            kotlin.run {
                loading_passport?.visibility = View.GONE
                loading_self_passport?.visibility = View.GONE
                loading_address?.visibility = View.GONE
                loading_driver_doc_front?.visibility = View.GONE
                loading_driver_doc_back?.visibility = View.GONE
                loading_sts_front?.visibility = View.GONE
                loading_sts_back?.visibility = View.GONE

                getImgDocs().forEach { it?.alpha = 1.0f }
            }
        }

        mainHandler.post(myRunnable)
    }


    private fun showSkeletonAnim() {
        getImgDocs().forEach { imgDocs ->
            val skeletonDocs = Skeleton.bind(imgDocs)
                .load(R.layout.skeleton_layout)
                .show()
            imgDocsSkeletons.add(skeletonDocs)
        }

        getTitleDocs().forEach { textDocs ->
            val layoutParams = textDocs?.layoutParams
            layoutParams?.height = 30
            textDocs?.layoutParams = layoutParams

            val skeletonText = Skeleton.bind(textDocs)
                .load(R.layout.skeleton_layout)
                .show()
            textDocsSkeletons.add(skeletonText)
        }
    }


    private fun hideSkeletonAnim(arrTics: ArrayList<Int>) {
        imgDocsSkeletons.forEach { it.hide() }
        textDocsSkeletons.forEach { it.hide() }

        getTitleDocs().forEach { textDocs ->
            val layoutParams = textDocs?.layoutParams
            layoutParams?.height = LinearLayout.LayoutParams.WRAP_CONTENT
            textDocs?.layoutParams = layoutParams
        }

        val viewTics = getTicsDocs()
        arrTics.forEach { i ->
            viewTics.forEachIndexed { index, imageView ->
                if (i == index) imageView?.visibility = View.VISIBLE
            }
        }
    }


    override fun onResume() {
        super.onResume()
        enableButtons()
    }


    override fun onDestroy() {
        tableDocsPresenter.instance().detachView()
        super.onDestroy()
    }
}