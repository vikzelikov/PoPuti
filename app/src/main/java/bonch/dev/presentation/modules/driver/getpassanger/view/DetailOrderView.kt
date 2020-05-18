package bonch.dev.presentation.modules.driver.getpassanger.view

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import bonch.dev.R
import bonch.dev.domain.entities.driver.getpassanger.Order
import bonch.dev.domain.entities.driver.getpassanger.SelectOrder
import bonch.dev.presentation.interfaces.ParentHandler
import bonch.dev.presentation.interfaces.ParentMapHandler
import bonch.dev.presentation.modules.driver.getpassanger.GetPassangerComponent
import bonch.dev.presentation.modules.driver.getpassanger.presenter.ContractPresenter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.user_location.UserLocationLayer
import kotlinx.android.synthetic.main.detail_order_layout.*
import javax.inject.Inject

class DetailOrderView : Fragment(), ContractView.IDetailOrderView {


    @Inject
    lateinit var detailOrderPresenter: ContractPresenter.IDetailOrderPresenter

    private var commentBottomSheet: BottomSheetBehavior<*>? = null
    private var timer: TimerToClose? = null
    lateinit var finish: ParentHandler<Int>
    lateinit var mapView: ParentMapHandler<MapView>
    lateinit var locationLayer: ParentMapHandler<UserLocationLayer>
    lateinit var nextFragment: ParentHandler<FragmentManager>

    private val TIME_EXPIRED = 2


    init {
        GetPassangerComponent.getPassangerComponent?.inject(this)

        detailOrderPresenter.instance().attachView(this)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.detail_order_layout, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        detailOrderPresenter.receiveOrder(SelectOrder.order)

        setListeners()

        setBottomSheet()

        correctMapView()
    }


    override fun setOrder(order: Order) {
        passanger_name.text = order.name
        passanger_rating.text = order.rating.toString()
        from_order.text = order.from
        to_order.text = order.to
        order_price.text = order.price.toString().plus(" ₽")

        Glide.with(img_passanger.context).load(order.img)
            .apply(RequestOptions().centerCrop().circleCrop())
            .into(img_passanger)

        if (order.comment == null) {
            line_comment_price.visibility = View.GONE
            comment.visibility = View.GONE
            val layoutParams: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            order_price.layoutParams = layoutParams
        } else {
            comment.text = order.comment
            comment_detail.text = order.comment
        }

        timer = TimerToClose(order.time.toLong())
        timer?.start()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == detailOrderPresenter.instance().OFFER_PRICE && resultCode == RESULT_OK) {
            val price = data?.getIntExtra(1.toString(), 0)
            //todo send price
            show_animation.visibility = View.VISIBLE

            Handler().postDelayed({
                val activity = activity as? MapOrderView
                activity?.let { nextFragment(it.supportFragmentManager) }
                show_animation.visibility = View.GONE
            }, 3000)
        }
    }


    override fun setListeners() {
        offer_price.setOnClickListener {
            context?.let {
                detailOrderPresenter.offerPrice(it, this)
            }
        }

        show_route.setOnClickListener {
            detailOrderPresenter.showRoute()
        }

        comment.setOnClickListener {
            commentBottomSheet?.state = BottomSheetBehavior.STATE_EXPANDED
        }

        on_view.setOnClickListener {
            commentBottomSheet?.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        confirm_with_price.setOnClickListener {
            val activity = activity as? MapOrderView
            activity?.let { nextFragment(it.supportFragmentManager) }
        }

        cancel_ride.setOnClickListener {
            SelectOrder.order = null
            finish(RESULT_OK)
        }

        back_btn.setOnClickListener {
            SelectOrder.order = null
            finish(RESULT_OK)
        }
    }


    private fun setBottomSheet() {
        commentBottomSheet = BottomSheetBehavior.from<View>(comment_detail_bottom_sheet)

        commentBottomSheet?.addBottomSheetCallback(object :
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
        if (slideOffset > 0 && slideOffset < 1) {
            on_view.alpha = slideOffset * 0.8f
            back_btn.alpha = 1 - slideOffset
            show_route.alpha = 1 - slideOffset
        }
    }


    private fun onStateChangedBottomSheet(newState: Int) {
        if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
            on_view.visibility = View.GONE
            main_info_layout.elevation = 30f
        } else {
            on_view.visibility = View.VISIBLE
            main_info_layout.elevation = 0f
        }
    }


    private fun correctMapView() {
        Thread(Runnable {
            while (true) {
                try {
                    val height = main_info_layout?.height
                    if (height in 100..1000) {
                        val mainHandler = Handler(Looper.getMainLooper())
                        val myRunnable = Runnable {
                            kotlin.run {
                                val layoutParams: RelativeLayout.LayoutParams =
                                    RelativeLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        LinearLayout.LayoutParams.MATCH_PARENT
                                    )
                                if (height != null) {
                                    layoutParams.setMargins(0, 0, 0, height)
                                }
                                getMap()?.layoutParams = layoutParams
                            }
                        }

                        mainHandler.post(myRunnable)

                        break
                    }
                } catch (ex: Exception) {
                    break
                }
            }
        }).start()
    }


    override fun getMap(): MapView? {
        return mapView()
    }


    override fun getNavHost(): NavController? {
        return (activity as? MapOrderView)?.getNavHost()
    }


    override fun hideKeyboard() {}


    override fun showNotification(text: String) {
        (activity as? MapOrderView)?.showNotification(text)
    }


    override fun getUserLocationLayer(): UserLocationLayer? {
        return locationLayer()
    }


    override fun onObjectUpdate() {
        detailOrderPresenter.onObjectUpdate()
    }


    override fun onBackPressed(): Boolean {
        var isBackPressed = true

        if (commentBottomSheet?.state != BottomSheetBehavior.STATE_COLLAPSED) {
            //hide
            commentBottomSheet?.state = BottomSheetBehavior.STATE_COLLAPSED
            isBackPressed = false
        } else {
            //finish
            SelectOrder.order = null
        }

        return isBackPressed
    }


    inner class TimerToClose(start: Long) : CountDownTimer(start * 1000, 1000) {
        override fun onFinish() {
            //close activity
            SelectOrder.order = null
            finish(TIME_EXPIRED)
        }

        override fun onTick(millisUntilFinished: Long) {}
    }


    override fun onDestroy() {
        timer = null
        detailOrderPresenter.instance().detachView()
        super.onDestroy()
    }

}