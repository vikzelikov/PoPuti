package bonch.dev.poputi.presentation.modules.passenger.regular.ride.presenter

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import bonch.dev.poputi.domain.entities.common.banking.BankCard
import bonch.dev.poputi.domain.entities.common.ride.Address
import bonch.dev.poputi.domain.entities.common.ride.RideInfo
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.user_location.UserLocationLayer

interface ContractPresenter {

    interface IRegularDrivePresenter {
        fun instance(): RegularRidesPresenter
        fun openActivity()
    }


    interface IMapCreateRegDrivePresenter {
        fun instance(): MapCreateRegRidePresenter
        fun attachCreateRegularDrive(fm: FragmentManager)
        fun getBitmap(drawableId: Int): Bitmap?
        fun getUserLocation(): UserLocationLayer?
        fun requestGeocoder(point: Point?)
        fun onObjectUpdate()
        fun onBackPressed()
    }


    interface ICreateRegularDrivePresenter {
        fun createRide()
        fun updateRide()
        fun onObjectUpdate()
        fun removeTickSelected()
        fun createDone(): Boolean
        fun startProcessBlockRequest()
        fun requestGeocoder(point: Point?)
        fun requestSuggest(query: String)
        fun instance(): CreateRegularRidePresenter
        fun setSelectedBankCard(bankCard: BankCard)
        fun addBankCard(context: Context, fragment: Fragment)
        fun checkAddressPoints(fromAddress: Address, toAddress: Address)
        fun offerPrice(context: Context, fragment: Fragment)
        fun addBankCardDone(data: Intent?)
        fun onClickItem(address: Address)
        fun offerPriceDone(data: Intent?)
        fun touchAddressMapMarkerBtn()
        fun touchMapBtn(isFrom: Boolean)
        fun touchAddress(isFrom: Boolean, isShowKeyboard: Boolean)
        fun touchCrossAddress(isFrom: Boolean)
        fun checkOnEditRide()
        fun getCashSuggest()
        fun clearSuggest()
        fun removeRoute()
        fun showMyPosition()
        fun submitRoute()
        fun showRoute()
        fun onDestroy()
    }


    interface IActiveRidesPresenter {
        fun getActiveRides()
        fun onClickItem(ride: RideInfo)
        fun instance(): ActiveRidesPresenter
        fun onRideDone(ride: RideInfo)
        fun archive()
        fun edit()
    }


    interface IArchiveRidesPresenter {
        fun getArchiveRides()
        fun onClickItem(ride: RideInfo)
        fun instance(): ArchiveRidesPresenter
        fun restore()
        fun delete()
    }

}