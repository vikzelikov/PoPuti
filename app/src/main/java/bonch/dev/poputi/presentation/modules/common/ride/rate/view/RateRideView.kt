package bonch.dev.poputi.presentation.modules.common.ride.rate.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RatingBar.OnRatingBarChangeListener
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import bonch.dev.poputi.domain.utils.Keyboard
import bonch.dev.poputi.App
import bonch.dev.poputi.MainActivity
import bonch.dev.poputi.R
import bonch.dev.poputi.di.component.common.DaggerCommonComponent
import bonch.dev.poputi.di.module.common.CommonModule
import bonch.dev.poputi.domain.entities.common.ride.ActiveRide
import bonch.dev.poputi.presentation.interfaces.ParentEmptyHandler
import bonch.dev.poputi.presentation.interfaces.ParentHandler
import bonch.dev.poputi.presentation.interfaces.ParentMapHandler
import bonch.dev.poputi.presentation.modules.common.CommonComponent
import bonch.dev.poputi.presentation.modules.common.ride.rate.presenter.IRateRidePresenter
import bonch.dev.poputi.presentation.modules.driver.getpassenger.view.MapOrderView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.yandex.mapkit.mapview.MapView
import kotlinx.android.synthetic.main.rate_layout.*
import javax.inject.Inject


/**
 * Common class for set rate after ride
 * (default UI set for driver, call setViewForPassanger() for checkout view)
 * */


class RateRideView : Fragment(), IRateRideView {

    @Inject
    lateinit var rateRidePresenter: IRateRidePresenter

    private var commentBottomSheet: BottomSheetBehavior<*>? = null

    lateinit var mapView: ParentMapHandler<MapView>
    lateinit var backView: ParentEmptyHandler
    lateinit var finishActivity: ParentHandler<Int>


    var isForPassenger = true


    init {
        initDI()

        CommonComponent.commonComponent?.inject(this)

        rateRidePresenter.instance().attachView(this)
    }


    //first build DI component
    private fun initDI() {
        if (CommonComponent.commonComponent == null) {
            CommonComponent.commonComponent = DaggerCommonComponent
                .builder()
                .commonModule(CommonModule())
                .appComponent(App.appComponent)
                .build()
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.rate_layout, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        onViewCreatedAnimation()

        correctMapView()

        setListenerRatingBar()

        setListeners()

        setBottomSheet()

        setPriceForRide()

        //todo
        wating_fee.visibility = View.GONE
        plus_wating_fee.visibility = View.GONE

        if (isForPassenger)
            setViewForPassenger()
    }


    private fun setViewForPassenger() {
        rating_title.text = resources.getString(R.string.youGetPlace)
        subtitle_rating.text = resources.getString(R.string.rateRide)
    }


    private fun setPriceForRide() {
        val activeRide = ActiveRide.activeRide
        if (activeRide != null) {
            try {
                if (activeRide.price != null)
                    price_for_ride?.text = activeRide.price.toString().plus(" ₽")

                plus_wating_fee.visibility = View.VISIBLE
                wating_fee.visibility = View.VISIBLE

                //todo get waiting fee
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    wating_fee.text = Html.fromHtml(
                        ("<b>21 ₽</b> ${resources.getString(R.string.forWatingPassanger)}"),
                        Html.FROM_HTML_MODE_COMPACT
                    )

                } else {
                    wating_fee.text = Html.fromHtml(
                        ("<b>21 ₽</b> ${resources.getString(R.string.forWatingPassanger)}")
                    )
                }

            } catch (ex: NumberFormatException) {
            }
        } else showNotification(getString(R.string.errorSystem))
    }


    private fun setListenerRatingBar() {
        rating_bar.onRatingBarChangeListener =
            OnRatingBarChangeListener { _, rating, _ ->
                if (rating < 3) {
                    status_rating.text = resources.getString(R.string.whatHappens)
                    comment_text_label.visibility = View.VISIBLE
                } else {
                    comment_text_label.visibility = View.GONE
                }

                if (rating == 4.0f || rating == 3.0f) {
                    status_rating.text = resources.getString(R.string.good)
                }

                if (rating == 5.0f) {
                    status_rating.text = resources.getString(R.string.perfect)
                }
            }
    }


    override fun setListeners() {
        send.setOnClickListener {
            val rating = rating_bar.rating.toInt()
            rateRidePresenter.rateDone(rating, comment_text.text.toString())
        }

        close.setOnClickListener {
            rateRidePresenter.closeRate()
        }

        comment_text_label.setOnClickListener {
            getComment()
        }

        comment_done.setOnClickListener {
            val comment = comment_text.text.toString().trim()
            comment_text_label.text = comment

            commentDone()
        }

        comment_back_btn.setOnClickListener {
            commentDone()
        }
    }


    private fun commentDone() {
        commentBottomSheet?.state = BottomSheetBehavior.STATE_COLLAPSED
        comment_text.clearFocus()
        hideKeyboard()
    }


    private fun setBottomSheet() {
        commentBottomSheet = BottomSheetBehavior.from<View>(comment_bottom_sheet)

        commentBottomSheet?.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                onSlideCancelReason(slideOffset)
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                onChangedStateCancelReason(newState)
            }
        })
    }


    override fun showNotification(text: String) {
        //for passanger
        (activity as? MainActivity)?.showNotification(text)

        //for driver
        (activity as? MapOrderView)?.showNotification(text)
    }


    override fun showLoading() {
        //for passanger
        (activity as? MainActivity)?.showLoading()

        //for driver
        (activity as? MapOrderView)?.showLoading()
    }


    override fun hideLoading() {
        //for passanger
        (activity as? MainActivity)?.hideLoading()

        //for driver
        (activity as? MapOrderView)?.hideLoading()
    }


    private fun getComment() {
        commentBottomSheet?.state = BottomSheetBehavior.STATE_EXPANDED

        if (!comment_text.isFocused) {
            comment_text.requestFocus()
            //set a little timer to open keyboard
            Handler().postDelayed({
                //for passanger
                (activity as? MainActivity)?.let {
                    Keyboard.showKeyboard(it)
                }

                //for driver
                (activity as? MapOrderView)?.let {
                    Keyboard.showKeyboard(it)
                }
            }, 200)
        }
    }


    private fun onSlideCancelReason(slideOffset: Float) {
        if (slideOffset > 0) {
            on_view.alpha = slideOffset * 0.8f
        }
    }


    private fun onChangedStateCancelReason(newState: Int) {
        if (newState == BottomSheetBehavior.STATE_DRAGGING) {
            hideKeyboard()
            comment_text.clearFocus()
        }

        if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
            on_view.visibility = View.GONE
            main_info_layout.elevation = 30f
        } else {
            on_view.visibility = View.VISIBLE
            main_info_layout.elevation = 0f
        }
    }


    private fun correctMapView() {
        val layoutParams: RelativeLayout.LayoutParams = RelativeLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        mapView()?.layoutParams = layoutParams
    }


    override fun getNavHost(): NavController? {
        return (activity as? MainActivity)?.navController
    }


    override fun hideKeyboard() {
        //for passanger
        (activity as? MainActivity)?.let {
            Keyboard.hideKeyboard(it, this.rate_container)
        }

        //for driver
        (activity as? MapOrderView)?.let {
            Keyboard.hideKeyboard(it, this.rate_container)
        }
    }


    override fun isPassanger(): Boolean {
        return isForPassenger
    }


    override fun finish(resultCode: Int) {
        finishActivity(resultCode)
    }


    override fun onBackPressed(): Boolean {
        var isBackPressed = true

        if (commentBottomSheet?.state != BottomSheetBehavior.STATE_COLLAPSED) {
            //hide
            commentBottomSheet?.state = BottomSheetBehavior.STATE_COLLAPSED
            isBackPressed = false
        }

        return isBackPressed
    }


    private fun onViewCreatedAnimation() {
        rate_container?.alpha = 0.0f
        rate_container?.animate()?.alpha(1f)?.duration = 200
    }


    override fun backViewPassenger() {
        rate_container?.alpha = 1.0f
        rate_container?.animate()
            ?.alpha(0f)
            ?.setDuration(150)
            ?.setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    backView()
                }
            })
    }


    override fun onDestroy() {
        rateRidePresenter.instance().detachView()
        ActiveRide.activeRide = null
        CommonComponent.commonComponent = null
        super.onDestroy()
    }

}