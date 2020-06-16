package bonch.dev.presentation.modules.passenger.getdriver.presenter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import bonch.dev.App
import bonch.dev.R
import bonch.dev.domain.entities.passenger.getdriver.ReasonCancel
import bonch.dev.presentation.base.BasePresenter
import bonch.dev.presentation.modules.passenger.getdriver.view.ContractView
import bonch.dev.presentation.modules.passenger.getdriver.view.CreateRideView
import bonch.dev.presentation.modules.passenger.getdriver.view.DetailRideView
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.user_location.UserLocationLayer


class MapCreateRidePresenter : BasePresenter<ContractView.IMapCreateRideView>(),
    ContractPresenter.IMapCreateRidePresenter {


    private var childCreateRide: ContractView.ICreateRideView? = null
    private var childDetailRide: ContractView.IDetailRideView? = null

    private val REASON = "REASON"

    override fun isUserCoordinate(args: Bundle?) {
        val reason = args?.getInt(REASON)
        val fm = getView()?.getFM()

        if (reason == ReasonCancel.DRIVER_CANCEL.reason || reason == ReasonCancel.WAIT_LONG.reason) {
            //in case cancel ride
            //redirect user to next screens
            fm?.let {
                attachDetailRide(fm)
            }
        } else {
            fm?.let {
                attachCreateRide(fm)
            }
        }
    }

    //Child fragments
    override fun attachCreateRide(fm: FragmentManager) {
        childDetailRide = null

        val childFragment = CreateRideView()

        //pass callback
        childFragment.locationLayer = { getUserLocation() }
        childFragment.moveMapCamera = { getView()?.moveCamera(it) }
        childFragment.nextFragment = { attachDetailRide(it) }

        this.childCreateRide = childFragment
        fm.beginTransaction()
            .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
            .replace(R.id.frame_container, childFragment, CreateRideView::class.java.simpleName)
            .commit()

        getView()?.getNavView()?.visibility = View.VISIBLE
        getView()?.correctMapView()
    }


    override fun attachDetailRide(fm: FragmentManager) {
        childCreateRide = null

        val childFragment = DetailRideView()

        //pass callback
        childFragment.backHandler = { backFragment(it) }
        childFragment.mapView = { getView()?.getMap() }
        childFragment.bottomNavView = getView()?.getNavView()

        this.childDetailRide = childFragment
        fm.beginTransaction()
            .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
            .replace(R.id.frame_container, childFragment, DetailRideView::class.java.simpleName)
            .commit()
    }


    override fun getUserLocation(): UserLocationLayer? {
        return getView()?.getUserLocation()
    }


    override fun onObjectUpdate() {
        childCreateRide?.onObjectUpdate()
    }


    override fun instance(): MapCreateRidePresenter {
        return this
    }


    override fun requestGeocoder(point: Point?) {
        childCreateRide?.requestGeocoder(point)
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


    private fun backFragment(fm: FragmentManager) {
        attachCreateRide(fm)
        childCreateRide?.backEvent()
    }

}