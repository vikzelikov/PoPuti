package bonch.dev.presenter.getdriver

import bonch.dev.model.getdriver.GetDriverModel
import bonch.dev.model.getdriver.pojo.Ride
import bonch.dev.view.getdriver.GetDriverView
import com.yandex.mapkit.geometry.Point

class GetDriverPresenter(val getDriverView: GetDriverView) {

    private var getDriverModel: GetDriverModel? = null
    private val addressesListAdapter = getDriverView.addressesListAdapter

    fun requestSuggest(query: String) {

        if (query.length > 2) {
            getDriverModel!!.requestSuggest(query, getDriverView.userLocationPoint()!!)
        } else {
            clearRecyclerSuggest()
        }

    }


    fun setRecyclerSuggest(suggestResult: ArrayList<Ride>) {
        if (addressesListAdapter != null) {
            addressesListAdapter.list = suggestResult
            addressesListAdapter.notifyDataSetChanged()
        }
    }


    private fun clearRecyclerSuggest() {
        if (addressesListAdapter != null) {
            addressesListAdapter.list.clear()
            addressesListAdapter.notifyDataSetChanged()
        }
    }


    fun requestGeocoder(point: Point) {
        getDriverModel!!.requestGeocoder(point)
    }


    fun responseGeocoder(address: String?, point: Point?) {

        var ride: Ride?

        if (address != null) {
            if (getDriverView.isFromMapSearch) {
                getDriverView.fromAddress.setText(address)

                if (point != null && addressesListAdapter != null) {
                    ride = Ride(address, "", null, point.longitude, point.latitude)
                    getDriverView.addressesListAdapter!!.fromAdr = ride
                }
            } else {
                getDriverView.toAddress.setText(address)

                if (point != null && addressesListAdapter != null) {
                    ride = Ride(address, "", null, point.longitude, point.latitude)
                    getDriverView.addressesListAdapter!!.toAdr = ride
                }
            }
        } else {
            getDriverView.fromAddress.setText("Address 1")
        }

    }


    init {
        if (getDriverModel == null) {
            getDriverModel = GetDriverModel(this)
        }
    }

}