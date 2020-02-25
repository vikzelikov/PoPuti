package bonch.dev.presenter.getdriver

import bonch.dev.model.getdriver.GetDriverModel
import bonch.dev.model.getdriver.pojo.Ride
import bonch.dev.view.getdriver.AddressesListAdapter
import bonch.dev.view.getdriver.GetDriverView

class GetDriverPresenter(
    private val getDriverView: GetDriverView,
    private val addressesListAdapter: AddressesListAdapter
) {

    private var getDriverModel: GetDriverModel? = null

    fun requestSuggest(query: String) {

        if(query.length > 2){
            getDriverModel!!.requestSuggest(query, getDriverView.userLocationPoint())
        }else{
            clearRecyclerSuggest()
        }

    }


    fun setRecyclerSuggest(suggestResult: ArrayList<Ride>){
        addressesListAdapter.list = suggestResult
        addressesListAdapter.notifyDataSetChanged()
    }


    private fun clearRecyclerSuggest(){
        addressesListAdapter.list.clear()
        addressesListAdapter.notifyDataSetChanged()
    }


    init {
        if (getDriverModel == null) {
            getDriverModel = GetDriverModel(this)
        }
    }

}