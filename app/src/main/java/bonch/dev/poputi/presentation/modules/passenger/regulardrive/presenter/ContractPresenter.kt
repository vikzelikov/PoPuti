package bonch.dev.poputi.presentation.modules.passenger.regulardrive.presenter

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import bonch.dev.poputi.domain.entities.common.banking.BankCard
import bonch.dev.poputi.domain.entities.common.ride.Address
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.user_location.UserLocationLayer

interface ContractPresenter {

    interface IRegularDrivePresenter {
        fun instance(): RegularDrivePresenter
        fun createRegularDrive()
    }


    interface IMapCreateRegDrivePresenter {
        fun instance(): MapCreateRegDrivePresenter
        fun attachCreateRegularDrive(fm: FragmentManager)
        fun getBitmap(drawableId: Int): Bitmap?
        fun getUserLocation(): UserLocationLayer?
        fun requestGeocoder(point: Point?)
        fun onObjectUpdate()
        fun onBackPressed()
    }


    interface ICreateRegularDrivePresenter {
        fun createRide()
        fun onObjectUpdate()
        fun removeTickSelected()
        fun createDone(): Boolean
        fun startProcessBlockRequest()
        fun requestGeocoder(point: Point?)
        fun requestSuggest(query: String)
        fun instance(): CreateRegularDrivePresenter
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
        fun getCashSuggest()
        fun clearSuggest()
        fun removeRoute()
        fun showMyPosition()
        fun submitRoute()
        fun showRoute()
        fun onDestroy()
    }

}