package bonch.dev.presentation.modules.passanger.regulardrive.view

import androidx.fragment.app.Fragment
import bonch.dev.domain.entities.common.banking.BankCard
import bonch.dev.domain.entities.common.ride.Address
import bonch.dev.presentation.interfaces.IBaseView
import bonch.dev.presentation.modules.passanger.regulardrive.adapters.AddressesListAdapter
import bonch.dev.presentation.modules.passanger.regulardrive.adapters.PaymentsListAdapter
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
    }


    interface ICreateRegularDriveView : IBaseView {
        fun showStartUI()
        fun commentEditStart()
        fun hideAllBottomSheet()
        fun setSelectedBankCard(bankCard: BankCard)
        fun offerPriceDone(price: Int, averagePrice: Int)
        fun setAddressView(isFrom: Boolean, address: String)
        fun getBottomSheet(bottomSheetBehavior: BottomSheetBehavior<*>)
        fun onClickItem(address: Address, isFromMapSearch: Boolean)
        fun getAddressesAdapter(): AddressesListAdapter
        fun getPaymentsAdapter(): PaymentsListAdapter
        fun expandedBottomSheet(isFrom: Boolean)
        fun removeAddressesView(isFrom: Boolean)
        fun getActualFocus(): Boolean
        fun isDataComplete(): Boolean
        fun addressesMapViewChanged()
        fun removeTickSelected()
        fun getMap(): MapView?
    }

}