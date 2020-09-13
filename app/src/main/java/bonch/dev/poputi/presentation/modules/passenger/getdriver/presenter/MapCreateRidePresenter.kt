package bonch.dev.poputi.presentation.modules.passenger.getdriver.presenter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import bonch.dev.poputi.App
import bonch.dev.poputi.R
import bonch.dev.poputi.domain.entities.common.ride.Address
import bonch.dev.poputi.domain.interactor.common.profile.IProfileInteractor
import bonch.dev.poputi.domain.interactor.common.profile.ProfileInteractor
import bonch.dev.poputi.domain.utils.Geo
import bonch.dev.poputi.presentation.base.BasePresenter
import bonch.dev.poputi.presentation.modules.passenger.getdriver.view.ContractView
import bonch.dev.poputi.presentation.modules.passenger.getdriver.view.CreateRideView
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition


class MapCreateRidePresenter : BasePresenter<ContractView.IMapCreateRideView>(),
    ContractPresenter.IMapCreateRidePresenter {


    private var childCreateRide: ContractView.ICreateRideView? = null
    private var profileInteractor: IProfileInteractor? = null


    override fun attachCreateRide(fm: FragmentManager) {
        val childFragment = CreateRideView()

        //pass callback
        childFragment.userPoint = { getView()?.getUserLocation() }
        childFragment.moveMapCamera = { getView()?.moveCamera(it) }
        childFragment.attachDetalRide = { getView()?.attachDetailRide() }
        childFragment.mapView = { getView()?.getMap() }
        childFragment.zoomMap = { getView()?.zoomMap(it) }
        childFragment.myCityCallback = {
            getView()?.getMyCityCall()?.let { call -> call(it) }
        }

        this.childCreateRide = childFragment
        fm.beginTransaction()
            .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
            .replace(R.id.frame_container, childFragment, CreateRideView::class.java.simpleName)
            .commit()
    }


    override fun getUserLocation(): Point? {
        return getView()?.getUserLocation()
    }


    override fun onObjectUpdate() {
        childCreateRide?.onObjectUpdate()
    }


    override fun instance() = this


    override fun requestGeocoder(cameraPosition: CameraPosition, isUp: Boolean) {
        childCreateRide?.requestGeocoder(cameraPosition, isUp)
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


    override fun saveMyCity(address: Address) {
        if (profileInteractor == null)
            profileInteractor = ProfileInteractor()

        if (Geo.selectedCity?.address != address.address) {
            Geo.selectedCity = address
            profileInteractor?.saveMyCity(address)
        }
    }


    override fun getMyCity(): Address? {
        if (profileInteractor == null)
            profileInteractor = ProfileInteractor()

        return profileInteractor?.getMyCity()
    }
}