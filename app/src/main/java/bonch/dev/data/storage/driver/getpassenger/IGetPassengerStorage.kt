package bonch.dev.data.storage.driver.getpassenger

interface IGetPassengerStorage {

    fun saveRideId(id: Int)

    fun getRideId(): Int

    fun removeRideId()

}