package bonch.dev.poputi.presentation.modules.driver.getpassenger.presenter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import bonch.dev.poputi.App
import bonch.dev.poputi.R
import bonch.dev.poputi.presentation.base.BasePresenter
import bonch.dev.poputi.presentation.modules.common.ride.rate.view.IRateRideView
import bonch.dev.poputi.presentation.modules.common.ride.rate.view.RateRideView
import bonch.dev.poputi.presentation.modules.driver.getpassenger.view.ContractView
import bonch.dev.poputi.presentation.modules.driver.getpassenger.view.DetailOrderView
import bonch.dev.poputi.presentation.modules.driver.getpassenger.view.TrackRideView

class MapOrderPresenter : BasePresenter<ContractView.IMapOrderView>(),
    ContractPresenter.IMapOrderPresenter {


    private var childDetailOrder: ContractView.IDetailOrderView? = null
    private var childTrackRide: ContractView.ITrackRideView? = null
    private var childRateRide: IRateRideView? = null


    override fun attachDetailOrder(fm: FragmentManager) {
        childTrackRide = null

        val childFragment = DetailOrderView()

        //pass callback
        childFragment.nextFragment = { attachTrackRide(it) }
        childFragment.finish = { getView()?.finishMapActivity(it) }
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
        childFragment.finishActivity = { getView()?.finishMapActivity() }
        childFragment.locationLayer = { getView()?.getUserLocation() }

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

        val childFragment = RateRideView()

        //pass callback
        childFragment.mapView = { getView()?.getMap() }
        childFragment.finishActivity = { getView()?.finishMapActivity(it) }

        //set type UI
        childFragment.isForPassenger = false

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
        childTrackRide?.onObjectUpdate()
    }


    override fun onBackPressed(): Boolean {
        var isBackPressed = true

        childDetailOrder?.let {
            isBackPressed = it.onBackPressed()
        }

        childTrackRide?.let {
            isBackPressed = it.onBackPressed()
        }

        childRateRide?.let {
            isBackPressed = it.onBackPressed()
        }

        return isBackPressed
    }


    override fun instance(): MapOrderPresenter {
        return this
    }

}