package bonch.dev.presenter.getdriver

import android.view.View
import bonch.dev.model.getdriver.GetDriverModel
import bonch.dev.model.getdriver.pojo.Ride
import bonch.dev.view.getdriver.DetailRideView
import bonch.dev.view.getdriver.GetDriverView
import com.yandex.mapkit.geometry.Point

class GetDriverPresenter(val getDriverView: GetDriverView, val root: View) {

    private val addressesListAdapter = getDriverView.addressesListAdapter

    private var getDriverModel: GetDriverModel? = null
    var detailRideView: DetailRideView? = null
    var fromAdr: Ride? = null
    var toAdr: Ride? = null


    fun addressesDone() {
        fromAdr = addressesListAdapter!!.fromAdr
        toAdr = addressesListAdapter.toAdr

        if (fromAdr != null && toAdr != null) {
            showDetailRide(root)
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
                    ride = Ride(address, null, null, point)
                    getDriverView.addressesListAdapter!!.fromAdr = ride
                }
            } else {
                getDriverView.toAddress.setText(address)

                if (point != null && addressesListAdapter != null) {
                    ride = Ride(address, null, null, point)
                    getDriverView.addressesListAdapter!!.toAdr = ride
                }
            }

            getDriverView.addressMapText.text = address

        }

    }


    private fun showDetailRide(root: View) {
        //hide BottomNavController
        getDriverView.navView.visibility = View.GONE
        getDriverView.detailRideLayout.visibility = View.VISIBLE
        getDriverView.getDriverLayout.visibility = View.GONE
        getDriverView.onMapView.visibility = View.GONE

        detailRideView = DetailRideView(getDriverView, root)
        detailRideView!!.onCreateView(fromAdr!!, toAdr!!)
    }


    fun clickBackGetDriver() {
        if (detailRideView != null) {
            detailRideView!!.removeRoute()
        }
    }


    init {
        if (getDriverModel == null) {
            getDriverModel = GetDriverModel(this)
        }
    }

}