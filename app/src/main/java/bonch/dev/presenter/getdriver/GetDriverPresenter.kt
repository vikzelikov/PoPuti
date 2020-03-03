package bonch.dev.presenter.getdriver

import android.os.Bundle
import bonch.dev.Constant
import bonch.dev.Coordinator
import bonch.dev.MainActivity
import bonch.dev.model.getdriver.GetDriverModel
import bonch.dev.model.getdriver.pojo.Ride
import bonch.dev.view.getdriver.GetDriverView
import com.yandex.mapkit.geometry.Point

class GetDriverPresenter(val getDriverView: GetDriverView) {

    private var getDriverModel: GetDriverModel? = null
    private val addressesListAdapter = getDriverView.addressesListAdapter
    var fromAdr: Ride? = null
    var toAdr: Ride? = null
    private val FROM = "FROM"
    private val TO = "TO"

    fun addressesDone() {
//TODO
        fromAdr = addressesListAdapter!!.fromAdr
        toAdr = addressesListAdapter.toAdr

        if (fromAdr != null && toAdr != null) {
            val bundle = Bundle()
            bundle.putParcelable(FROM, fromAdr)
            bundle.putParcelable(TO, toAdr)

            val fm = (getDriverView.activity as MainActivity).supportFragmentManager
            Coordinator.replaceFragment(Constant.DETAIL_RIDE_VIEW, bundle, fm)
        }
    }


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

        val ride: Ride?

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

            getDriverView.addressMapEditText.text = address

        }

    }


    init {
        if (getDriverModel == null) {
            getDriverModel = GetDriverModel(this)
        }
    }

}