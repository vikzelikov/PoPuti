package bonch.dev.presentation.modules.driver.signup.tabledocs.view

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import bonch.dev.Permissions
import bonch.dev.R
import bonch.dev.domain.entities.driver.signup.Docs
import bonch.dev.domain.entities.driver.signup.SignupMainData
import bonch.dev.domain.entities.driver.signup.Step
import bonch.dev.domain.utils.Camera
import bonch.dev.domain.utils.Gallery
import bonch.dev.presentation.modules.driver.signup.DriverSignupActivity
import bonch.dev.presentation.modules.driver.signup.SignupComponent
import bonch.dev.presentation.modules.driver.signup.tabledocs.presenter.ITableDocsPresenter
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.table_docs_fragment.*
import javax.inject.Inject

class TableDocsView : Fragment(), ITableDocsView {

    @Inject
    lateinit var tableDocsPresenter: ITableDocsPresenter

    private var reshootBottomSheet: BottomSheetBehavior<*>? = null
    private var blockHandler: Handler? = null
    private var isBlock = false
    private val DRIVER_CREATED = "DRIVER_CREATED"


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
        super.onViewCreated(view, savedInstanceState)

        startProcessBlock()

        val driverCreated = arguments?.getBoolean(DRIVER_CREATED, false)
        if (driverCreated != null && driverCreated) {
            //driver already created
            tableDocsPresenter.instance().isDriverCreated = true
        } else {
            //create driver on background
            Thread(Runnable {
                tableDocsPresenter.createDriver()
            }).start()
        }

        tableDocsPresenter.sortDocs()

        loadPhoto()

        setBottomSheet()

        setListeners()
    }


    override fun loadPhoto() {
        Thread(Runnable {
            val listDocs: ArrayList<Docs> = SignupMainData.listDocs
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
    }


    private fun setDocs(list: ArrayList<Docs>) {
        val viewsImgDocs = getImgDocs()
        val viewsTitleDocs = getTitleDocs()
        val viewsTicsDocs = getTicsDocs()

        if (list.isNotEmpty()) {
            val mainHandler = Handler(Looper.getMainLooper())
            val myRunnable = Runnable {
                kotlin.run {
                    try {
                        for (i in 0 until list.size) {
                            val isVerify = list[i].isVerify

                            if (isVerify != null) {
                                if (isVerify) {
                                    //docs is valid
                                    viewsImgDocs[i].isClickable = false
                                    viewsTitleDocs[i].setTextColor(Color.parseColor("#149319"))
                                    viewsTicsDocs[i].setImageDrawable(
                                        ContextCompat.getDrawable(
                                            viewsTicsDocs[i].context,
                                            R.drawable.ic_green_tick
                                        )
                                    )
                                } else {
                                    //docs not valid
                                    status_driver_signup.text = resources.getText(R.string.someDocsNotValid)
                                    status_driver_signup.setTextColor(Color.parseColor("#D03131"))
                                    viewsTitleDocs[i].setTextColor(Color.parseColor("#D03131"))
                                    viewsTicsDocs[i].setImageDrawable(
                                        ContextCompat.getDrawable(
                                            viewsTicsDocs[i].context,
                                            R.drawable.ic_red_cross
                                        )
                                    )
                                }
                            } else {
                                //docs still is processing
                                viewsTitleDocs[i].setTextColor(Color.parseColor("#000000"))
                                viewsTicsDocs[i].visibility = View.GONE

                                //set deafult status
                                status_driver_signup.text = resources.getText(R.string.weCheckDocs)
                                status_driver_signup.setTextColor(Color.parseColor("#1152FD"))
                            }

                            val image = if (list[i].imgDocs != null) {
                                list[i].imgDocs
                            } else {
                                list[i].imgUrl
                            }

                            Glide.with(viewsImgDocs[i].context).load(image)
                                .into(viewsImgDocs[i])

                        }
                    } catch (ex: IndexOutOfBoundsException) {
                        println(ex.message)
                    }
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
            viewsImgDocs[i].setOnClickListener {
                val id = tableDocsPresenter.getByValue(i)
                if (id != null) {
                    getReshootBottomSheet(id)
                }
            }
        }

        on_view_table.setOnClickListener {
            hideReshootBottomSheet()
        }

        make_photo.setOnClickListener {
            if (!isBlock) {
                tableDocsPresenter.getCamera(this)
                isBlock = true
            }
        }

        clip_photo.setOnClickListener {
            if (!isBlock) {
                Gallery.getPhoto(this)
                isBlock = true
            }
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


    override fun getImgDocs(): Array<ImageView> {
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


    override fun getTitleDocs(): Array<TextView> {
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


    override fun getTicsDocs(): Array<ImageView> {
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


    private fun startProcessBlock() {
        if (blockHandler == null) {
            blockHandler = Handler()
        }

        blockHandler?.postDelayed(object : Runnable {
            override fun run() {
                isBlock = false
                blockHandler?.postDelayed(this, 2500)
            }
        }, 0)
    }


    override fun showNotification(text: String) {
        (activity as? DriverSignupActivity)?.showNotification(text)
    }


    fun onBackPressed(): Boolean {
        return if (tableDocsPresenter.onBackPressed()) {
            true
        } else {
            showNotification(resources.getString(R.string.photoLoading))
            false
        }
    }


    override fun onDestroy() {
        blockHandler?.removeCallbacksAndMessages(null)
        tableDocsPresenter.instance().detachView()
        super.onDestroy()
    }
}