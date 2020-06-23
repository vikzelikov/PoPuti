package bonch.dev.data.repository.driver.getpassenger

import bonch.dev.data.repository.IMainRepository
import bonch.dev.domain.entities.common.ride.RideInfo
import bonch.dev.domain.entities.common.ride.StatusRide
import bonch.dev.presentation.interfaces.DataHandler
import bonch.dev.presentation.interfaces.SuccessHandler

interface IGetPassengerRepository : IMainRepository {

    fun subscribeOnChangeRide(callback: DataHandler<String?>)

    fun offerPrice(price: Int, rideId: Int, userId: Int, token: String, callback: SuccessHandler)

    fun getNewOrders(callback: DataHandler<ArrayList<RideInfo>?>)

}