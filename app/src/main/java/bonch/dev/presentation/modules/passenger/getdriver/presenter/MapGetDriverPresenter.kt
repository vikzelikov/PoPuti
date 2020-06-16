package bonch.dev.presentation.modules.passenger.getdriver.presenter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import bonch.dev.App
import bonch.dev.R
import bonch.dev.presentation.base.BasePresenter
import bonch.dev.presentation.modules.common.ride.rate.view.IRateRideView
import bonch.dev.presentation.modules.passenger.getdriver.view.ContractView
import bonch.dev.presentation.modules.passenger.getdriver.view.GetDriverView
import bonch.dev.presentation.modules.common.ride.rate.view.RateRideView
import bonch.dev.presentation.modules.passenger.getdriver.GetDriverComponent
import bonch.dev.presentation.modules.passenger.getdriver.view.TrackRideView

class MapGetDriverPresenter : BasePresenter<ContractView.IMapGetDriverView>(),
    ContractPresenter.IMapGetDriverPresenter {


    private var childGetDriverView: ContractView.IGetDriverView? = null
    private var childTrackRideView: ContractView.ITrackRideView? = null
    private var childRateRideView: IRateRideView? = null


    override fun attachGetDriver(fm: FragmentManager) {
        childTrackRideView = null

        val childFragment = GetDriverView()
        childFragment.arguments = getView()?.getArgs()

        //pass callback
        childFragment.locationLayer = { getView()?.getUserLocation() }
        childFragment.mapView = { getView()?.getMap() }
        childFragment.nextFragment = { attachTrackRide(it) }

        this.childGetDriverView = childFragment

        fm.beginTransaction()
            .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
            .replace(R.id.frame_container, childFragment, GetDriverView::class.java.simpleName)
            .commit()
    }


    override fun attachTrackRide(fm: FragmentManager) {
        childGetDriverView = null

        val childFragment = TrackRideView()

        //pass callback
        childFragment.mapView = { getView()?.getMap() }
        childFragment.nextFragment = { attachRateRide(it) }

        this.childTrackRideView = childFragment
        fm.beginTransaction()
            .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
            .replace(R.id.frame_container, childFragment, TrackRideView::class.java.simpleName)
            .commit()
    }


    override fun attachRateRide(fm: FragmentManager) {
        //all clear
        childGetDriverView = null
        childTrackRideView = null
        GetDriverComponent.getDriverComponent = null

        val childFragment = RateRideView()

        //pass callback
        childFragment.mapView = { getView()?.getMap() }

        //set type UI
        childFragment.isForPassenger = true

        this.childRateRideView = childFragment
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


    override fun onBackPressed(): Boolean {
        var isBackPressed = true
        val getDriver = childGetDriverView
        val trackRide = childTrackRideView

        if (getDriver != null) {
            isBackPressed = getDriver.onBackPressed()
        }

        if (trackRide != null) {
            isBackPressed = trackRide.onBackPressed()
        }

        return isBackPressed
    }


    override fun instance(): MapGetDriverPresenter {
        return this
    }


    override fun onObjectUpdated() {
        childGetDriverView?.onObjectUpdated()
    }


}