package bonch.dev.presentation.modules.passanger.getdriver.ride.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import bonch.dev.domain.entities.passanger.getdriver.Address
import bonch.dev.domain.entities.passanger.getdriver.BankCard
import bonch.dev.domain.entities.passanger.getdriver.Driver
import bonch.dev.domain.entities.passanger.getdriver.RideInfo
import bonch.dev.presentation.interfaces.IBaseView
import bonch.dev.presentation.modules.passanger.getdriver.ride.adapters.AddressesListAdapter
import bonch.dev.presentation.modules.passanger.getdriver.ride.adapters.DriversListAdapter
import bonch.dev.presentation.modules.passanger.getdriver.ride.adapters.PaymentsListAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.user_location.UserLocationLayer

interface ContractView {

    interface IMapCreateRideView : IBaseView {
        fun correctMapView()
        fun getFM(): FragmentManager?
        fun getNavView(): BottomNavigationView?
        fun getUserLocation(): UserLocationLayer?
        fun moveCamera(point: Point)
        fun getMap(): MapView
    }


    interface ICreateRideView : IBaseView {
        fun getAddressesAdapter(): AddressesListAdapter
        fun setAddressView(isFrom: Boolean, address: String)
        fun removeAddressesView(isFrom: Boolean)
        fun onSlideBottomSheet(bottomSheet: View, slideOffset: Float)
        fun onClickItem(address: Address, isFromMapSearch: Boolean)
        fun getUserLocationLayer(): UserLocationLayer?
        fun onStateChangedBottomSheet(newState: Int)
        fun expandedBottomSheet(isFrom: Boolean)
        fun requestGeocoder(point: Point?)
        fun onBackPressed(): Boolean
        fun moveCamera(point: Point)
        fun getActualFocus(): Boolean
        fun addressesMapViewChanged()
        fun showDetailRide()
        fun onObjectUpdate()
        fun nextFragment()
        fun showStartUI()
        fun backEvent()
    }


    interface IDetailRideView : IBaseView {
        fun getInfoPrice()
        fun getMap(): MapView?
        fun removeTickSelected()
        fun hideAllBottomSheet()
        fun onSlideBottomSheet(slideOffset: Float)
        fun onStateChangedBottomSheet(newState: Int)
        fun getPaymentsAdapter(): PaymentsListAdapter
        fun setAddresses(fromAddress: String, toAddress: String)
        fun setSelectedBankCard(bankCard: BankCard)
        fun offerPriceDone(price: Int, averagePrice: Int)
        fun getBottomNav(): BottomNavigationView?
        fun isDataComplete(): Boolean
        fun onBackPressed(): Boolean
        fun getRideInfo(): RideInfo
        fun getPaymentMethods()
        fun commentEditStart()
    }


    interface IMapGetDriverView : IBaseView {
        fun getUserLocation(): UserLocationLayer?
        fun getArgs(): Bundle?
        fun getMap(): MapView
    }


    interface IGetDriverView : IBaseView {
        fun nextFragment()
        fun getMap(): MapView?
        fun removeBackground()
        fun getConfirmAccept()
        fun getExpiredTimeConfirm()
        fun onBackPressed(): Boolean
        fun getAdapter(): DriversListAdapter
        fun getUserLocationLayer(): UserLocationLayer?
        fun checkoutBackground(isShow: Boolean)
        fun getRecyclerView(): RecyclerView
        fun showNotification(text: String)
        fun startAnimSearch(point: Point)
        fun hideConfirmAccept()
        fun onObjectUpdated()
    }


    interface ITrackRideView : IBaseView {
        fun setInfoDriver(driver: Driver)
        fun onBackPressed(): Boolean
        fun showNotification(text: String)
        fun checkoutStatusView()
        fun getMap(): MapView?
        fun nextFragment()
    }

}