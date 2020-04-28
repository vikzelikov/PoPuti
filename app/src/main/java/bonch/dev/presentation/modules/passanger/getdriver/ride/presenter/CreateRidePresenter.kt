package bonch.dev.presentation.modules.passanger.getdriver.ride.presenter

import android.os.Handler
import android.view.View
import bonch.dev.data.repository.passanger.getdriver.GetDriverRepository
import bonch.dev.data.repository.passanger.getdriver.pojo.Coordinate
import bonch.dev.data.repository.passanger.getdriver.pojo.Coordinate.fromAdr
import bonch.dev.data.repository.passanger.getdriver.pojo.Coordinate.toAdr
import bonch.dev.data.repository.passanger.getdriver.pojo.Ride
import bonch.dev.data.repository.passanger.getdriver.pojo.RidePoint
import bonch.dev.domain.interactor.passanger.getdriver.IGetDriverInteractor
import bonch.dev.domain.utils.Constants
import bonch.dev.presentation.base.BasePresenter
import bonch.dev.presentation.modules.passanger.getdriver.GetDriverComponent
import bonch.dev.presentation.modules.passanger.getdriver.ride.view.ContractView
import bonch.dev.presentation.modules.passanger.getdriver.ride.view.DetailRideView
import com.yandex.mapkit.geometry.Point
import io.realm.RealmResults
import javax.inject.Inject


class CreateRidePresenter : BasePresenter<ContractView.ICreateRideView>(),
    ContractPresenter.ICreateRidePresenter {

    @Inject
    lateinit var getDriverInteractor: IGetDriverInteractor

    private var getDriverRepository: GetDriverRepository? = null
    private var cashSuggest: RealmResults<Ride>? = null
    private var blockRequestHandler: Handler? = null
    private var detailRideView: DetailRideView? = null

    private var isBlockRequest = true
    var isFromMapSearch = true


    init {
        GetDriverComponent.getDriverComponent?.inject(this)
    }


    override fun addressesDone(): Boolean {
        var isSuccess = false

        if (fromAdr != null && toAdr != null) {
            //cash data (both of adr)
            saveCashSuggest(fromAdr!!)
            saveCashSuggest(toAdr!!)

            //go to the next screen
            showDetailRideView()

            stopProcessBlockRequest()
            isSuccess = true
        }

        return isSuccess
    }


    override fun requestGeocoder(point: Point?) {
        if (!isBlockRequest && point != null) {
            //send request and set block for several seconds
            getDriverInteractor.requestGeocoder(point)
            isBlockRequest = true
        }
    }


    fun responseGeocoder(address: String?, point: Point?) {
        if (address != null && point != null) {
            if (isFromMapSearch) {
                Coordinate.fromAdr = Ride(
                    0,
                    address,
                    Constants.NOT_DESCRIPTION,
                    null,
                    RidePoint(point.latitude, point.longitude)
                )
            } else {
                Coordinate.toAdr = Ride(
                    0,
                    address,
                    Constants.NOT_DESCRIPTION,
                    null,
                    RidePoint(point.latitude, point.longitude)
                )
            }

            getView()?.setAddressView(isFromMapSearch, address)

        }
    }


    override fun requestSuggest(query: String) {
        if (query.length > 2) {
            getDriverInteractor.initRealm()
            //first check cash, then go to net
            //try to get data from cash
            if (!isBlockRequest) {
                val cashRequest = getDriverInteractor.getCashRequest(query)

                if (!cashRequest.isNullOrEmpty()) {
                    //set in view
                    val adapter = getView()?.getAddressesAdapter()
                    adapter?.list?.clear()
                    adapter?.list?.addAll(cashRequest)
                    adapter?.notifyDataSetChanged()
                    isBlockRequest = true

                } else {
                    getDriverInteractor.requestSuggest(query, getUserPoint())
                }

                isBlockRequest = true
            }

        } else {
            clearSuggest()
        }

        if (query.isEmpty()) {
            getCashSuggest()
        }
    }


    fun responseSuggest(suggestResult: ArrayList<Ride>) {
        //cash request
        getDriverInteractor.saveCashRequest(suggestResult)

        //set in view
        val adapter = getView()?.getAddressesAdapter()
        adapter?.list = suggestResult
        adapter?.notifyDataSetChanged()
    }


    override fun clearSuggest() {
        val adapter = getView()?.getAddressesAdapter()
        adapter?.list?.clear()
        adapter?.notifyDataSetChanged()
    }


    private fun saveCashSuggest(addressRide: Ride) {
        val tempList = arrayListOf<Ride>()

        //copy object
        val adr = Ride()
        adr.apply {
            id = addressRide.id
            address = addressRide.address
            description = addressRide.description
            uri = addressRide.uri
            point = addressRide.point
            isCashed = addressRide.isCashed
        }


        if (!cashSuggest.isNullOrEmpty()) {
            tempList.addAll(cashSuggest!!)
            //sort by Realm-id
            tempList.sortBy {
                it.id
            }
        }

        if (!tempList.contains(adr)) {
            if (tempList.isEmpty()) {
                adr.id = tempList.size + 1
                tempList.add(adr)
            } else {
                //get id the last item > inc > init
                adr.id = tempList.last().id + 1
                tempList.add(adr)
            }

            //clear cash if it has too much memory
            if (tempList.size > Constants.CASH_VALUE_COUNT) {
                var rideDel = Ride()
//                for (i in 0 until cashSuggest!!.size) {
//                    if (cashSuggest[i] == tempList[0]) {
//                        rideDel = cashSuggest[i]
//                        break
//                    }
//                }

                getDriverInteractor.deleteCashSuggest(rideDel)
                tempList.removeAt(0)
            }

            //update or save cash
            getDriverInteractor.saveCashSuggest(tempList)
            //clear and get again
            cashSuggest = null
            getCashSuggest()
        }
    }


    override fun getCashSuggest() {
        if (cashSuggest == null) {
            getDriverInteractor.initRealm()
            cashSuggest = getDriverInteractor.getCashSuggest()
        }

        if (cashSuggest != null) {
            val adapter = getView()?.getAddressesAdapter()
            adapter?.list?.clear()
            adapter?.list?.addAll(cashSuggest!!)
            adapter?.notifyDataSetChanged()
        }
    }


    override fun onClickItem(ride: Ride) {
//        val fromAdrView = createRideView.from_adr
//        val toAdrView = createRideView.to_adr
//
//        if (isFromMapSearch) {
//            fromAdrView.setText(ride.address)
//            fromAdrView.setSelection(fromAdrView.text.length)
//            fromAdr = ride
//        } else {
//            toAdrView.setText(ride.address)
//            toAdrView.setSelection(toAdrView.text.length)
//            toAdr = ride
//        }

        addressesDone()
    }


    private fun showDetailRideView() {
        //dynamic add layout
        //dynamicReplaceView(true)

        //create next screen
//        detailRideView =
//            DetailRideView(
//                getView()
//
//            )
        detailRideView?.onCreateView()
    }


    override fun clearMapObjects() {
        detailRideView?.removeRoute()
    }


    override fun touchCrossFrom(isFrom: Boolean) {
        if (!isFrom) {
            toAdr = null
        }

        getView()?.removeAddressesView(isFrom)
    }


    override fun touchAddress(isFrom: Boolean) {

        getView()?.expandedBottomSheet(isFrom)

        //update current focus
        isFromMapSearch = isFrom
        //set favourite addresses
        getCashSuggest()
    }


    override fun touchMapBtn(isFrom: Boolean) {
        isFromMapSearch = isFrom

        getView()?.addressesMapViewChanged(isFrom)
    }


    override fun touchAddressMapMarkerBtn() {
        val res = addressesDone()

        if (!res) {
            getView()?.showStartUI()
        }
    }


    override fun showMyPosition() {
        val userPoint = getUserPoint()

        if (userPoint != null) {
            getView()?.getParentView()?.moveCamera(userPoint)
        }
    }


    private fun getUserPoint(): Point? {
        var point: Point? = null
        val userLocation = getView()?.getParentView()?.getUserLocation()
        if (userLocation?.cameraPosition() != null) {
            point = userLocation.cameraPosition()!!.target
        }

        return point
    }


    override fun startProcessBlockRequest() {
        if (blockRequestHandler == null) {
            blockRequestHandler = Handler()
        }

        blockRequestHandler?.postDelayed(object : Runnable {
            override fun run() {
                isBlockRequest = false
                if (fromAdr == null)
                    requestGeocoder(getUserPoint())
                blockRequestHandler?.postDelayed(this, Constants.BLOCK_REQUEST_GEOCODER / 2)
            }
        }, 0)
    }


    override fun onSlideBottomSheet(bottomSheet: View, slideOffset: Float) {
        if (slideOffset > 0) {
            getView()?.onSlideBottomSheet(bottomSheet, slideOffset)
        }
    }


    override fun onStateChangedBottomSheet(newState: Int) {
        getView()?.onStateChangedBottomSheet(newState)
    }


    override fun onObjectUpdate() {
        if (blockRequestHandler == null) {
            startProcessBlockRequest()
        }
    }


    private fun stopProcessBlockRequest() {
        blockRequestHandler?.removeCallbacksAndMessages(null)
        isBlockRequest = true
    }


    override fun onDestroy() {
        getDriverInteractor.closeRealm()
    }

    override fun instance(): CreateRidePresenter {
        return this
    }
}