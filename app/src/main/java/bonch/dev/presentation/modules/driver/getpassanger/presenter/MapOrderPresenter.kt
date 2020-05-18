package bonch.dev.presentation.modules.driver.getpassanger.presenter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import bonch.dev.App
import bonch.dev.R
import bonch.dev.presentation.base.BasePresenter
import bonch.dev.presentation.modules.common.rate.view.IRateRideView
import bonch.dev.presentation.modules.common.rate.view.RateRideView
import bonch.dev.presentation.modules.driver.getpassanger.GetPassangerComponent
import bonch.dev.presentation.modules.driver.getpassanger.view.ContractView
import bonch.dev.presentation.modules.driver.getpassanger.view.DetailOrderView
import bonch.dev.presentation.modules.driver.getpassanger.view.TrackRideView

class MapOrderPresenter : BasePresenter<ContractView.IMapOrderView>(),
    ContractPresenter.IMapOrderPresenter {


    private var childDetailOrder: ContractView.IDetailOrderView? = null
    private var childTrackRide: ContractView.ITrackRideView? = null
    private var childRateRide: IRateRideView? = null


    override fun attachDetailOrder(fm: FragmentManager) {
        val childFragment = DetailOrderView()

        //pass callback
        childFragment.nextFragment = { attachTrackRide(it) }
        childFragment.finish = { getView()?.finishActivity() }
        childFragment.locationLayer = { getView()?.getUserLocation() }
        childFragment.mapView = { getView()?.getMap() }

        this.childDetailOrder = childFragment

        fm.beginTransaction()
            .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
            .replace(R.id.frame_container, childFragment, DetailOrderView::class.java.simpleName)
            .commit()
    }


    override fun attachTrackRide(fm: FragmentManager) {
        childDetailOrder = null

        val childFragment = TrackRideView()

        //pass callback
        childFragment.mapView = { getView()?.getMap() }
        childFragment.nextFragment = { attachRateRide(it) }
        childFragment.finishActivity = { getView()?.finishActivity() }


        this.childTrackRide = childFragment
        fm.beginTransaction()
            .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
            .replace(R.id.frame_container, childFragment, TrackRideView::class.java.simpleName)
            .commit()
    }


    override fun attachRateRide(fm: FragmentManager) {
        //all clear
        childDetailOrder = null
        childTrackRide = null
        GetPassangerComponent.getPassangerComponent = null

        val childFragment = RateRideView()

        //pass callback
        childFragment.mapView = { getView()?.getMap() }
        childFragment.finishActivity = { getView()?.finishActivity() }

        //set type UI
        childFragment.isForPassanger = false

        this.childRateRide = childFragment
        fm.beginTransaction()
            .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
            .replace(R.id.frame_container, childFragment, RateRideView::class.java.simpleName)
            .commit()
    }


    override fun getBitmap(drawableId: Int): Bitmap? {
        val context = App.appComponent.getContext()
        return context.getBitmapFromVectorDrawable(drawableId)
    }


    private fun Context.getBitmapFromVectorDrawable(drawableId: Int): Bitmap? {
        val drawable = ContextCompat.getDrawable(this, drawableId) ?: return null

        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        ) ?: return null
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)

        return bitmap
    }


    override fun onObjectUpdate() {
        childDetailOrder?.onObjectUpdate()
    }


    override fun onBackPressed(): Boolean {
        var isBackPressed = true

        childDetailOrder?.let {
            isBackPressed = it.onBackPressed()
        }

        return isBackPressed
    }


    override fun instance(): MapOrderPresenter {
        return this
    }

}