package bonch.dev.data.storage.driver.getpassenger

import bonch.dev.App

class GetPassengerStorage : IGetPassengerStorage {

    private val RIDE_ID = "RIDE_ID"


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

}