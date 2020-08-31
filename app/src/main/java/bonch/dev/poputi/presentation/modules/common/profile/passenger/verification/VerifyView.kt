package bonch.dev.poputi.presentation.modules.common.profile.passenger.verification

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import bonch.dev.poputi.domain.utils.Camera
import bonch.dev.poputi.domain.utils.Gallery
import bonch.dev.poputi.Permissions
import bonch.dev.poputi.R
import bonch.dev.poputi.domain.entities.common.media.Photo
import bonch.dev.poputi.domain.entities.common.profile.verification.VerifyData
import bonch.dev.poputi.domain.entities.common.profile.verification.VerifyStep
import bonch.dev.poputi.presentation.modules.common.profile.ContractPresenter
import bonch.dev.poputi.presentation.modules.common.profile.ContractView
import bonch.dev.presentation.modules.common.profile.ProfileComponent
import com.bumptech.glide.Glide
import com.ethanhua.skeleton.Skeleton
import com.ethanhua.skeleton.ViewSkeletonScreen
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.verify_activity.*
import javax.inject.Inject

class VerifyView : AppCompatActivity(), ContractView.IVerifyView {

    @Inject
    lateinit var presenter: ContractPresenter.IVerifyPresenter

    private var shootBottomSheet: BottomSheetBehavior<*>? = null
    private var infoBottomSheet: BottomSheetBehavior<*>? = null

    private var imgDocsSkeletons: ArrayList<ViewSkeletonScreen> = arrayListOf()
    private var textDocsSkeletons: ArrayList<ViewSkeletonScreen> = arrayListOf()

    private var handlerAnimation: Handler? = null

    init {
        ProfileComponent.profileComponent?.inject(this)

        presenter.instance().attachView(this)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(null)
        setContentView(R.layout.verify_activity)

        showSkeletonAnim()

        presenter.resetData()

        presenter.checkVerification()

        presenter.createVerification()

        presenter.loadPhoto()

        setBottomSheet()

        setListeners()
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        this.let {
            if (Permissions.isAccess(Permissions.STORAGE_PERMISSION, it)) {
                VerifyData.imgUri = Camera.getCamera(this).toString()
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        presenter.onActivityResult(this, requestCode, resultCode, data)

        hideAllBottomSheet()

        enableButtons()
    }


    /**
     * FOR FIRSTLY VERIFICATION
     */
    override fun setPhoto(step: VerifyStep, img: String) {
        val mainHandler = Handler(Looper.getMainLooper())
        val myRunnable = Runnable {
            kotlin.run {
                container_photos?.visibility = View.VISIBLE

                if (step == VerifyStep.SELF_PHOTO_PASSPORT) {
                    Glide.with(self_passport_min.context).load(img).into(self_passport_min)
                    self_passport_min?.visibility = View.VISIBLE

                    save?.visibility = View.VISIBLE

                } else if (step == VerifyStep.PASSPORT_ADDRESS_PHOTO) {
                    Glide.with(address_passport_min.context).load(img).into(address_passport_min)
                    address_passport_min?.visibility = View.VISIBLE

                    attach?.visibility = View.GONE
                }
            }
        }
        mainHandler.post(myRunnable)
    }


    /**
     * FOR REMAKE VERIFICATION
     */
    override fun setDocs(list: ArrayList<Photo>) {
        val viewsImgDocs = getImgDocs()
        val viewsTitleDocs = getTitleDocs()
        val viewsTicsDocs = getTicsDocs()

        val arrTics = arrayListOf<Int>()

        if (list.isNotEmpty()) {
            try {
                for (i in 0 until list.size) {
                    val isVerify = list[i].isVerify

                    if (isVerify != 0) {

                        arrTics.add(i)

                        if (isVerify == 1) {
                            //docs is valid
                            viewsImgDocs[i]?.isClickable = false
                            viewsTitleDocs[i]?.setTextColor(Color.parseColor("#149319"))
                            viewsTicsDocs[i]?.setImageDrawable(
                                ContextCompat.getDrawable(this, R.drawable.ic_green_tick)
                            )

                        } else {
                            //docs not valid
                            viewsTitleDocs[i]?.setTextColor(Color.parseColor("#D03131"))
                            viewsTicsDocs[i]?.setImageDrawable(
                                ContextCompat.getDrawable(this, R.drawable.ic_red_cross)
                            )
                        }
                    } else {
                        //docs still is processing
                        viewsTitleDocs[i]?.setTextColor(Color.parseColor("#000000"))
                        viewsTicsDocs[i]?.visibility = View.GONE
                    }

                    val image = if (list[i].imgDocs != null) list[i].imgDocs
                    else list[i].imgUrl

                    viewsImgDocs[i]?.let { Glide.with(it.context).load(image).into(it) }
                }
            } catch (ex: IndexOutOfBoundsException) {
            }

            Handler().postDelayed({
                hideSkeletonAnim(arrTics)
            }, 2000)
        }
    }


    override fun setListeners() {
        val viewsImgDocs = getImgDocs()

        for (i in viewsImgDocs.indices) {
            viewsImgDocs[i]?.setOnClickListener {
                val id = presenter.getByValue(i)
                if (id != null && !presenter.isBlockBack()) {
                    getShootBottomSheet(id)
                }
            }
        }

        attach.setOnClickListener {
            getShootBottomSheet(VerifyData.idStep)
        }

        save.setOnClickListener {
            if (VerifyData.listDocs.size == 2) {
                if (presenter.isBlockBack()) {
                    showNotification(resources.getString(R.string.photoProfileLoading))
                } else {
                    setResult(Activity.RESULT_OK)
                    finish()
                }
            } else {
                infoBottomSheet?.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }

        back_btn.setOnClickListener {
            onBackPressed()
        }

        back_text.setOnClickListener {
            onBackPressed()
        }

        make_photo.setOnClickListener {
            presenter.getCamera(this)
            blockButtons()
        }

        clip_photo.setOnClickListener {
            Gallery.getPhoto(this)
            blockButtons()
        }

        on_view_bottom_sheet.setOnClickListener {
            hideAllBottomSheet()
        }

        ok_btn.setOnClickListener {
            hideAllBottomSheet()
        }
    }


    private fun setBottomSheet() {
        shootBottomSheet = BottomSheetBehavior.from<View>(shoot_bottom_sheet)
        infoBottomSheet = BottomSheetBehavior.from<View>(info_bottom_sheet)

        shootBottomSheet?.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                onSlideBottomSheet(slideOffset)
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                onStateChangedBottomSheet(newState)
            }
        })

        infoBottomSheet?.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                onSlideBottomSheet(slideOffset)
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                onStateChangedBottomSheet(newState)
            }
        })
    }


    private fun onSlideBottomSheet(slideOffset: Float) {
        if (slideOffset > 0) {
            on_view_bottom_sheet?.alpha = slideOffset * 0.8f
        }
    }


    private fun onStateChangedBottomSheet(newState: Int) {
        if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
            on_view_bottom_sheet?.visibility = View.GONE
        } else {
            on_view_bottom_sheet?.visibility = View.VISIBLE
        }
    }


    override fun getNavHost(): NavController? = null


    override fun hideKeyboard() {}


    override fun showNotification(text: String) {
        val mainHandler = Handler(Looper.getMainLooper())
        val myRunnable = Runnable {
            kotlin.run {
                val view = general_notification

                view?.text = text
                handlerAnimation?.removeCallbacksAndMessages(null)
                handlerAnimation = Handler()
                view?.translationY = 0.0f
                view?.alpha = 0.0f

                view?.animate()
                    ?.setDuration(500L)
                    ?.translationY(100f)
                    ?.alpha(1.0f)
                    ?.setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator?) {
                            super.onAnimationEnd(animation)
                            handlerAnimation?.postDelayed({ hideNotifications() }, 2000)
                        }
                    })
            }
        }


        mainHandler.post(myRunnable)
    }


    private fun hideNotifications() {
        val view = general_notification

        view?.animate()
            ?.setDuration(500L)
            ?.translationY(-100f)
            ?.alpha(0.0f)
    }


    override fun showLoading() {
        val mainHandler = Handler(Looper.getMainLooper())
        val myRunnable = Runnable {
            kotlin.run {
                on_view?.alpha = 0.9f
                on_view?.visibility = View.VISIBLE
                progress_bar?.visibility = View.VISIBLE
                loading_text?.visibility = View.VISIBLE
            }
        }

        mainHandler.post(myRunnable)
    }


    override fun showFullLoading() {
        val mainHandler = Handler(Looper.getMainLooper())
        val myRunnable = Runnable {
            kotlin.run {
                on_view?.alpha = 1f
                on_view?.visibility = View.VISIBLE
                progress_bar?.visibility = View.VISIBLE
                loading_text?.visibility = View.GONE
            }
        }

        mainHandler.post(myRunnable)
    }


    override fun hideLoading() {
        val mainHandler = Handler(Looper.getMainLooper())
        val myRunnable = Runnable {
            kotlin.run {
                progress_bar?.visibility = View.GONE
                loading_text?.visibility = View.GONE
                on_view?.alpha = 0.9f
                on_view?.animate()
                    ?.alpha(0f)
                    ?.setDuration(500)
                    ?.setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator?) {
                            //go to the next screen
                            on_view?.visibility = View.GONE
                        }
                    })
            }
        }

        mainHandler.post(myRunnable)
    }


    override fun hideFullLoading() {
        val mainHandler = Handler(Looper.getMainLooper())
        val myRunnable = Runnable {
            kotlin.run {
                progress_bar?.visibility = View.GONE
                loading_text?.visibility = View.GONE
                on_view?.alpha = 1f
                on_view?.animate()
                    ?.alpha(0f)
                    ?.setDuration(300)
                    ?.setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator?) {
                            on_view?.visibility = View.GONE
                        }
                    })
            }
        }

        mainHandler.post(myRunnable)
    }


    private fun hideAllBottomSheet() {
        shootBottomSheet?.state = BottomSheetBehavior.STATE_COLLAPSED
        infoBottomSheet?.state = BottomSheetBehavior.STATE_COLLAPSED
    }


    private fun getShootBottomSheet(idDoc: VerifyStep) {
        VerifyData.idStep = idDoc
        shootBottomSheet?.state = BottomSheetBehavior.STATE_EXPANDED
    }


    override fun getImgDocs(): Array<ImageView?> {
        return arrayOf(
            passport_photo,
            passport_address_photo
        )
    }


    override fun getTitleDocs(): Array<TextView?> {
        return arrayOf(
            text_passport,
            text_passport_address
        )
    }


    override fun getTicsDocs(): Array<ImageView?> {
        return arrayOf(
            status_passport,
            status_passport_address
        )
    }


    override fun showLoadingPhoto(idDoc: VerifyStep) {
        val mainHandler = Handler(Looper.getMainLooper())
        val myRunnable = Runnable {
            kotlin.run {
                val photo: Pair<View, View> = when (idDoc) {
                    VerifyStep.SELF_PHOTO_PASSPORT -> Pair(
                        loading_passport,
                        passport_photo
                    )
                    VerifyStep.PASSPORT_ADDRESS_PHOTO -> Pair(
                        loading_passport_address,
                        passport_address_photo
                    )
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
                loading_passport_address?.visibility = View.GONE

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


    override fun onBackPressed() {
        return if (presenter.isBlockBack()) {
            showNotification(resources.getString(R.string.photoProfileLoading))
        } else {
            super.onBackPressed()
        }
    }


    override fun finishVerification() {
        finish()
    }


    override fun onResume() {
        super.onResume()
        enableButtons()
    }

}