package bonch.dev.presentation.modules.passanger.getdriver.ride.view

import android.view.View
import androidx.fragment.app.FragmentManager
import bonch.dev.presentation.interfaces.IBaseView
import bonch.dev.presentation.modules.passanger.getdriver.ride.adapters.AddressesListAdapter
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.user_location.UserLocationLayer

interface ContractView {

    interface IMapView : IBaseView {
        fun getUserLocation(): UserLocationLayer?
        fun correctMapView()
        fun moveCamera(point: Point)
        fun getMap(): MapView
    }

    interface ICreateRideView : IBaseView {
        fun getAddressesAdapter(): AddressesListAdapter
        fun setAddressView(isFrom: Boolean, address: String)
        fun removeAddressesView(isFrom: Boolean)
        fun onSlideBottomSheet(bottomSheet: View, slideOffset: Float)
        fun dynamicReplaceViewChanged(showDetailRide: Boolean)
        fun onStateChangedBottomSheet(newState: Int)
        fun addressesMapViewChanged(isFrom: Boolean)
        fun expandedBottomSheet(isFrom: Boolean)
        fun requestGeocoder(point: Point?)
        fun getParentView(): IMapView?
        fun onBackPressed(): Boolean
        fun addressesDone(): Boolean
        fun onObjectUpdate()
        fun showStartUI()
    }

    interface IDetailRideView : IBaseView {

    }

}