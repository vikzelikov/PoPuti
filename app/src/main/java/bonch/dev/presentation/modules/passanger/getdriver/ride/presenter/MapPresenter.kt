package bonch.dev.presentation.modules.passanger.getdriver.ride.presenter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import bonch.dev.App
import bonch.dev.R
import bonch.dev.data.repository.passanger.getdriver.pojo.ReasonCancel
import bonch.dev.domain.utils.Constants
import bonch.dev.presentation.base.BasePresenter
import bonch.dev.presentation.modules.passanger.getdriver.ride.view.ContractView
import bonch.dev.presentation.modules.passanger.getdriver.ride.view.CreateRideView
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.user_location.UserLocationLayer


class MapPresenter : BasePresenter<ContractView.IMapView>(),
    ContractPresenter.IMapPresenter {


    var childFragment: ContractView.ICreateRideView? = null


    override fun isUserCoordinate() {
        if (ReasonCancel.reasonID == Constants.REASON1 || ReasonCancel.reasonID == Constants.REASON2) {
            //in case cancel ride
            //redirect user to next screens
            childFragment?.addressesDone()
            ReasonCancel.reasonID = null
        }
    }

    override fun onObjectUpdate() {
        childFragment?.onObjectUpdate()
    }


    override fun instance(): MapPresenter {
        return this
    }


    override fun requestGeocoder(point: Point?) {
        childFragment?.requestGeocoder(point)
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


    override fun attachChildView(fm: FragmentManager) {
        val childFragment = CreateRideView()
        this.childFragment = childFragment
        fm.beginTransaction().add(R.id.frame_container, childFragment, null).commit()
    }


    override fun getUserLocation(): UserLocationLayer? {
        return getView()?.getUserLocation()
    }


}