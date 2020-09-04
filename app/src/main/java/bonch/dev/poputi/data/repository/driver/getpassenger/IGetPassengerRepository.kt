package bonch.dev.poputi.data.repository.driver.getpassenger

import bonch.dev.poputi.data.repository.IMainRepository
import bonch.dev.poputi.domain.entities.common.ride.Offer
import bonch.dev.poputi.domain.entities.common.ride.RideInfo
import bonch.dev.poputi.presentation.interfaces.DataHandler
import bonch.dev.poputi.presentation.interfaces.SuccessHandler

interface IGetPassengerRepository : IMainRepository {

    fun connectSocketOrders(token: String, callback: SuccessHandler)

    fun subscribeOnGetOrders(callback: DataHandler<String?>)

    fun subscribeOnDeleteOrder(callback: DataHandler<String?>)

    fun offerPrice(price: Int, rideId: Int, userId: Int, token: String, callback: DataHandler<Offer?>)

    fun getNewOrders(callback: DataHandler<ArrayList<RideInfo>?>)

}