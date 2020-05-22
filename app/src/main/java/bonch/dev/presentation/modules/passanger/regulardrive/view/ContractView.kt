package bonch.dev.presentation.modules.passanger.regulardrive.view

import androidx.fragment.app.Fragment
import bonch.dev.domain.entities.common.banking.BankCard
import bonch.dev.presentation.interfaces.IBaseView
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
        fun hideAllBottomSheet()
        fun getBottomSheet(bottomSheetBehavior: BottomSheetBehavior<*>)
        fun offerPriceDone(price: Int, averagePrice: Int)
        fun setSelectedBankCard(bankCard: BankCard)
        fun commentEditStart()
        fun removeTickSelected()
        fun getPaymentsAdapter(): PaymentsListAdapter
        fun isDataComplete(): Boolean
        fun getMap(): MapView?
    }

}