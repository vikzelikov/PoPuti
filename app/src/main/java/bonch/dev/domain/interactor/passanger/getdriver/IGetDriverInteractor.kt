package bonch.dev.domain.interactor.passanger.getdriver

import bonch.dev.domain.entities.passanger.getdriver.Address
import bonch.dev.domain.entities.passanger.getdriver.Driver
import bonch.dev.domain.entities.passanger.getdriver.RideInfo
import bonch.dev.domain.entities.common.ride.StatusRide
import bonch.dev.presentation.interfaces.NotificationHandler
import com.yandex.mapkit.geometry.Point

typealias GeocoderHandler = (address: String?, point: Point) -> Unit
typealias SuggestHandler = (suggest: ArrayList<Address>) -> Unit
typealias NewDriver = (driver: Driver) -> Unit
typealias CommonHandler<T> = (data: T, error: String?) -> Unit

interface IGetDriverInteractor {

    fun initRealm()

    //Yandex MapKit
    fun requestGeocoder(point: Point, callback: GeocoderHandler)

    fun requestSuggest(query: String, userLocationPoint: Point?, callback: SuggestHandler)


    //Ride
    fun createRide(rideInfo: RideInfo, callback: NotificationHandler)

    fun updateRideStatus(status: StatusRide)

    fun linkDriverToRide(driverId: Int)

    fun getNewDriver(callback: NewDriver)

    fun saveDriver(driver: Driver)

    fun removeDriver()


    //Cash
    fun getCashRequest(query: String): ArrayList<Address>?

    fun getCashSuggest(): ArrayList<Address>?

    fun saveCashRequest(cashRequest: ArrayList<Address>)

    fun saveCashSuggest(cashAddresses: ArrayList<Address>)

    fun deleteCashSuggest(address: Address)

    fun closeRealm()

}