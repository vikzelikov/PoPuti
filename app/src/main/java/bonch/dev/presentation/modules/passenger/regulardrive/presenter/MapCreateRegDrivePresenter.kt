package bonch.dev.presentation.modules.passenger.regulardrive.presenter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import bonch.dev.App
import bonch.dev.R
import bonch.dev.presentation.base.BasePresenter
import bonch.dev.presentation.modules.passenger.regulardrive.view.ContractView
import bonch.dev.presentation.modules.passenger.regulardrive.view.CreateRegularDriveView
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.user_location.UserLocationLayer

class MapCreateRegDrivePresenter : BasePresenter<ContractView.IMapCreateRegularDrive>(),
    ContractPresenter.IMapCreateRegDrivePresenter {


    private var childCreateRegularDrive: ContractView.ICreateRegularDriveView? = null


    //Child fragments
    override fun attachCreateRegularDrive(fm: FragmentManager) {
        val childFragment = CreateRegularDriveView()

        //pass callback
//        childFragment.locationLayer = { getUserLocation() }
//        childFragment.moveMapCamera = { getView()?.moveCamera(it) }
//        childFragment.mapView = { getView()?.getMap() }

        this.childCreateRegularDrive = childFragment
        fm.beginTransaction()
            .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
            .replace(
                R.id.frame_container,
                childFragment,
                CreateRegularDriveView::class.java.simpleName
            )
            .commit()
    }


    override fun getUserLocation(): UserLocationLayer? {
        return getView()?.getUserLocation()
    }


    override fun requestGeocoder(point: Point?) {
        //childCreateRegularDrive?.requestGeocoder(point)
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


    override fun instance(): MapCreateRegDrivePresenter {
        return this
    }


}