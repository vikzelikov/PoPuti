package bonch.dev.poputi.data.storage.driver.getpassenger

interface IGetPassengerStorage {

    fun saveRideId(id: Int)

    fun getRideId(): Int

    fun removeRideId()

    fun saveWaitTimestamp()

    fun getWaitTimestamp(): Long

}