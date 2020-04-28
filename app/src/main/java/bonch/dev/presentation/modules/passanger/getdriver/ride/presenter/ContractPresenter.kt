package bonch.dev.presentation.modules.passanger.getdriver.ride.presenter

import android.graphics.Bitmap
import android.view.View
import androidx.fragment.app.FragmentManager
import bonch.dev.data.repository.passanger.getdriver.pojo.Ride
import bonch.dev.presentation.modules.passanger.getdriver.ride.adapters.AddressesListAdapter
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.user_location.UserLocationLayer

interface ContractPresenter {

    interface IMapPresenter {
        fun instance(): MapPresenter
        fun getBitmap(drawableId: Int): Bitmap?
        fun attachChildView(fm: FragmentManager)
        fun getUserLocation(): UserLocationLayer?
        fun requestGeocoder(point: Point?)
        fun isUserCoordinate()
        fun onObjectUpdate()
    }


    interface ICreateRidePresenter {
        fun getCashSuggest()
        fun clearMapObjects()
        fun requestGeocoder(point: Point?)
        fun instance(): CreateRidePresenter
        fun onSlideBottomSheet(bottomSheet: View, slideOffset: Float)
        fun onStateChangedBottomSheet(newState: Int)
        fun startProcessBlockRequest()
        fun touchCrossFrom(isFrom: Boolean)
        fun requestSuggest(query: String)
        fun touchAddress(isFrom: Boolean)
        fun touchMapBtn(isFrom: Boolean)
        fun touchAddressMapMarkerBtn()
        fun addressesDone(): Boolean
        fun onClickItem(ride: Ride)
        fun onObjectUpdate()
        fun showMyPosition()
        fun clearSuggest()
        fun onDestroy()
    }


    interface IDetailRidePresenter {

    }
}