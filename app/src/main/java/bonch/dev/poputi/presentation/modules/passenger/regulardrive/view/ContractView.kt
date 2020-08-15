package bonch.dev.poputi.presentation.modules.passenger.regulardrive.view

import androidx.fragment.app.Fragment
import bonch.dev.poputi.domain.entities.common.banking.BankCard
import bonch.dev.poputi.domain.entities.common.ride.Address
import bonch.dev.poputi.domain.entities.common.ride.RideInfo
import bonch.dev.poputi.presentation.interfaces.IBaseView
import bonch.dev.poputi.presentation.modules.passenger.regulardrive.adapters.AddressesListAdapter
import bonch.dev.poputi.presentation.modules.passenger.regulardrive.adapters.PaymentsListAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.user_location.UserLocationLayer

interface ContractView {

    interface IRegularDriveView : IBaseView {
        fun getFragment(): Fragment
    }


    interface IMapCreateRegularDrive : IBaseView {
        fun getUserLocation(): UserLocationLayer?
        fun getMap(): MapView
        fun moveCamera(point: Point)
        fun pressBack()
    }


    interface ICreateRegularDriveView : IBaseView {
        fun getTime()
        fun commentEditStart()
        fun hideAllBottomSheet()
        fun hideMainInfoLayout()
        fun showMainInfoLayout()
        fun getRideInfo(): RideInfo
        fun showStartUI(isShowBottomSheet: Boolean)
        fun setSelectedBankCard(bankCard: BankCard)
        fun offerPriceDone(price: Int, averagePrice: Int)
        fun setAddressView(isFrom: Boolean, address: String)
        fun getBottomSheet(bottomSheetBehavior: BottomSheetBehavior<*>)
        fun onClickItem(address: Address, isFromMapSearch: Boolean)
        fun expandedBottomSheet(isFrom: Boolean, isShowKeyboard: Boolean)
        fun getAddressesAdapter(): AddressesListAdapter
        fun getPaymentsAdapter(): PaymentsListAdapter
        fun getUserLocationLayer(): UserLocationLayer?
        fun addressesMapViewChanged(isFrom: Boolean)
        fun removeAddressesView(isFrom: Boolean)
        fun requestGeocoder(point: Point?)
        fun getActualFocus(): Boolean?
        fun isDataComplete(): Boolean
        fun moveCamera(point: Point)
        fun onBackPressed(): Boolean
        fun getDays(): BooleanArray
        fun removeTickSelected()
        fun getMap(): MapView?
        fun onObjectUpdate()
        fun hideMapMarker()
        fun showRouteBtn()
        fun hideRouteBtn()
    }
}