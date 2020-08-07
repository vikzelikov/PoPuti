package bonch.dev.poputi.data.storage.driver.getpassenger

import bonch.dev.poputi.App

class GetPassengerStorage : IGetPassengerStorage {

    private val RIDE_ID = "RIDE_ID"

    private val WAITING_TIME = "WAITING_TIME"


    override fun saveRideId(id: Int) {
        val pref = App.appComponent.getSharedPref()
        val editor = pref.edit()
        editor.putInt(RIDE_ID, id)
        editor.apply()
    }


    override fun getRideId(): Int {
        val pref = App.appComponent.getSharedPref()
        return pref.getInt(RIDE_ID, -1)
    }


    override fun removeRideId() {
        val pref = App.appComponent.getSharedPref()
        val editor = pref.edit()
        editor.remove(RIDE_ID)
        editor.apply()
    }


    override fun saveWaitTimestamp() {
        val pref = App.appComponent.getSharedPref()
        val editor = pref.edit()

        val waitingTime = System.currentTimeMillis()
        editor.putLong(WAITING_TIME, waitingTime)
        editor.apply()
    }


    override fun getWaitTimestamp(): Long {
        val pref = App.appComponent.getSharedPref()
        return pref.getLong(WAITING_TIME, -1)
    }

}