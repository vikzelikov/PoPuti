package bonch.dev.presenter.passanger.getdriver

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Handler
import android.view.View
import bonch.dev.MainActivity
import bonch.dev.R
import bonch.dev.model.passanger.getdriver.CreateRideModel
import bonch.dev.model.passanger.getdriver.pojo.Coordinate.fromAdr
import bonch.dev.model.passanger.getdriver.pojo.Coordinate.toAdr
import bonch.dev.model.passanger.getdriver.pojo.Ride
import bonch.dev.model.passanger.getdriver.pojo.RidePoint
import bonch.dev.presenter.passanger.getdriver.adapters.AddressesListAdapter
import bonch.dev.utils.ChangeOpacity.getOpacity
import bonch.dev.utils.Constants
import bonch.dev.utils.Constants.BLOCK_REQUEST_GEOCODER
import bonch.dev.utils.Keyboard.hideKeyboard
import bonch.dev.utils.Keyboard.showKeyboard
import bonch.dev.view.passanger.getdriver.CreateRideView
import bonch.dev.view.passanger.getdriver.DetailRideView
import bonch.dev.view.passanger.getdriver.MBottomSheet
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.yandex.mapkit.Animation
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import io.realm.RealmResults
import kotlinx.android.synthetic.main.create_ride_fragment.*
import kotlinx.android.synthetic.main.create_ride_layout.*
import kotlin.math.abs
import kotlin.math.floor


class CreateRidePresenter(val createRideView: CreateRideView) {

    private var createRideModel: CreateRideModel? = null
    private var blockRequestHandler: Handler? = null
    var addressesListAdapter: AddressesListAdapter? = null
    private var cashSuggest: RealmResults<Ride>? = null
    var detailRideView: DetailRideView? = null
    var isBlockRequest = true
    var isFromMapSearch = true

    private val shape = GradientDrawable()
    private val corners = floatArrayOf(14f, 14f, 14f, 14f, 0f, 0f, 0f, 0f)


    init {
        if (createRideModel == null) {
            createRideModel = CreateRideModel(this)
        }
    }


    fun addressesDone(): Boolean {
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


    fun requestSuggest(query: String) {
        if (query.length > 2) {
            createRideModel?.initRealmCash()
            //first check cash, then go to net
            //try to get data from cash
            if (!isBlockRequest) {
                val cashRequest = createRideModel?.getCashRequest(query)

                if (!cashRequest.isNullOrEmpty()) {
                    //set in view
                    addressesListAdapter?.list?.clear()
                    addressesListAdapter?.list?.addAll(cashRequest)
                    addressesListAdapter?.notifyDataSetChanged()
                    isBlockRequest = true

                } else {
                    createRideModel!!.requestSuggest(query, getUserPoint())
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
        createRideModel?.saveCashRequest(suggestResult)

        //set in view
        addressesListAdapter?.list = suggestResult
        addressesListAdapter?.notifyDataSetChanged()
    }


    private fun clearSuggest() {
        addressesListAdapter?.list?.clear()
        addressesListAdapter?.notifyDataSetChanged()
    }


    fun requestGeocoder(point: Point?) {
        if (!isBlockRequest && point != null) {
            //send request and set block for several seconds
            createRideModel?.requestGeocoder(point)
            isBlockRequest = true
        }
    }


    private fun saveCashSuggest(address: Ride) {
        val tempList = arrayListOf<Ride>()

        //copy object
        val adr = Ride()
        adr.id = address.id
        adr.address = address.address
        adr.description = address.description
        adr.uri = address.uri
        adr.point = address.point
        adr.isCashed = address.isCashed

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
                for (i in 0 until cashSuggest!!.size) {
                    if (cashSuggest!![i]!! == tempList[0]) {
                        rideDel = cashSuggest!![i]!!
                        break
                    }
                }

                createRideModel?.deleteCashSuggest(rideDel)
                tempList.removeAt(0)
            }

            //update or save cash
            createRideModel?.saveCashSuggest(tempList)
            //clear and get again
            cashSuggest = null
            getCashSuggest()
        }
    }


    fun getCashSuggest() {
        if (cashSuggest == null) {
            createRideModel?.initRealm()
            cashSuggest = createRideModel?.getCashSuggest()
        }

        if (cashSuggest != null) {
            addressesListAdapter?.list?.clear()
            addressesListAdapter?.list?.addAll(cashSuggest!!)
            addressesListAdapter?.notifyDataSetChanged()
        }
    }


    fun responseGeocoder(address: String?, point: Point?) {
        val fromAddress = getView().from_adr
        val toAddress = getView().to_adr
        val addressMapText = getView().address_map_text

        if (address != null && point != null) {
            if (isFromMapSearch) {
                fromAddress.setText(address)
                fromAdr = Ride(
                    0,
                    address,
                    Constants.NOT_DESCRIPTION,
                    null,
                    RidePoint(point.latitude, point.longitude)
                )
            } else {
                toAddress.setText(address)
                toAdr = Ride(
                    0,
                    address,
                    Constants.NOT_DESCRIPTION,
                    null,
                    RidePoint(point.latitude, point.longitude)
                )
            }

            addressMapText.text = address
        }
    }


    private fun showDetailRideView() {
        //dynamic add layout
        dynamicReplaceView(true)

        //create next screen
        detailRideView = DetailRideView(getView())
        detailRideView!!.onCreateView()
    }


    private fun clearMapObjects() {
        detailRideView?.removeRoute()
    }


    fun touchCrossFrom(isFrom: Boolean) {
        if (isFrom) {
            getView().from_adr.setText("")
//            not allow user to remove 'from' address
//            fromAdr = null
        } else {
            getView().to_adr.setText("")
            toAdr = null
        }
    }


    fun touchAddress(isFrom: Boolean) {
        val r = getView()
        val activity = getView().activity as MainActivity

        if (isFrom) {
            if (r.to_adr.isFocused) {
                addressesListAdapter?.list?.clear()
                addressesListAdapter?.notifyDataSetChanged()
                r.from_adr.requestFocus()
                showKeyboard(activity)
            }

            r.btn_map_from.visibility = View.VISIBLE
            r.btn_map_to.visibility = View.GONE
            r.to_cross.visibility = View.GONE

            if (r.from_adr.text.isNotEmpty()) {
                r.from_cross.visibility = View.VISIBLE
            } else {
                r.from_cross.visibility = View.GONE
            }
        } else {
            if (r.from_adr.isFocused) {
                addressesListAdapter?.list?.clear()
                addressesListAdapter?.notifyDataSetChanged()
                r.to_adr.requestFocus()
                showKeyboard(activity)
            }

            r.btn_map_from.visibility = View.GONE
            r.btn_map_to.visibility = View.VISIBLE
            r.from_cross.visibility = View.GONE

            if (r.to_adr.text.isNotEmpty()) {
                r.to_cross.visibility = View.VISIBLE
            } else {
                r.to_cross.visibility = View.GONE
            }
        }

        getView().bottomSheetBehavior!!.state = BottomSheetBehavior.STATE_EXPANDED
    }


    fun touchMapBtn(isFrom: Boolean) {
        val r = getView()
        val activity = getView().activity as MainActivity

        r.main_addresses_layout.visibility = View.GONE
        r.address_map_marker_layout.visibility = View.VISIBLE

        if (isFrom) {
            isFromMapSearch = true
            r.address_map_text.isSelected = true
            r.center_position.setImageResource(R.drawable.ic_map_marker)
            r.circle_marker.setImageResource(R.drawable.ic_input_marker_from)
            r.address_map_marker_btn.setBackgroundResource(R.drawable.bg_btn_blue)
        } else {
            isFromMapSearch = false
            r.address_map_text.isSelected = true
            r.center_position.setImageResource(R.drawable.ic_map_marker_black)
            r.circle_marker.setImageResource(R.drawable.ic_input_marker_to)
            r.address_map_marker_btn.setBackgroundResource(R.drawable.bg_btn_black)
        }

        getView().bottomSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
        hideKeyboard(activity, getView().view!!)
    }


    fun touchAddressMapMarkerBtn() {
        val res = addressesDone()

        if (!res) {
            showStartUI()
        }
    }


    fun showMyPosition() {
        val userPoint = getUserPoint()

        userPoint?.let {
            getView().mapView!!.map.move(
                CameraPosition(it, 35.0f, 0.0f, 0.0f),
                Animation(Animation.Type.SMOOTH, 1f),
                null
            )
        }
    }


    fun getUserPoint(): Point? {
        var point: Point? = null
        val userLocation = createRideView.userLocationLayer
        if (userLocation?.cameraPosition() != null) {
            point = userLocation.cameraPosition()!!.target
        }

        return point
    }


    fun onSlideBottomSheet(bottomSheet: View, slideOffset: Float) {
        shape.cornerRadii = corners

        if (slideOffset > 0) {
            val r = getView()
            val expandedValue = floor((abs(slideOffset)) * 180).toInt()
            shape.setColor(Color.parseColor("#${getOpacity(expandedValue)}FFFFFF"))
            bottomSheet.background = shape
            r.my_pos.alpha = 1 - slideOffset * 3
            r.center_position.alpha = 1 - slideOffset * 3
            r.on_map_view.alpha = slideOffset * 0.8f
            r.addresses_list.alpha = slideOffset
            r.bottom_sheet_arrow.alpha = slideOffset
        }
    }


    fun onStateChangedBottomSheet(newState: Int) {
        val r = getView()

        if (newState == BottomSheetBehavior.STATE_DRAGGING) {
            val activity = r.activity as MainActivity
            hideKeyboard(activity, r.view!!)
            r.from_adr.clearFocus()
            r.to_adr.clearFocus()
        }
        if (newState == BottomSheetBehavior.STATE_EXPANDED) {
            if (r.bottomSheetBehavior is MBottomSheet<*>) {
                (r.bottomSheetBehavior as MBottomSheet<*>).swipeEnabled = true
            }
        } else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
            if (r.bottomSheetBehavior is MBottomSheet<*>) {
                r.btn_map_from.visibility = View.GONE
                r.btn_map_to.visibility = View.GONE
                r.from_cross.visibility = View.GONE
                r.to_cross.visibility = View.GONE
                r.on_map_view.visibility = View.GONE
                (r.bottomSheetBehavior as MBottomSheet<*>).swipeEnabled = false
            }
        } else {
            r.on_map_view.visibility = View.VISIBLE
        }
    }


    fun backPressed(): Boolean {
        var isBackPressed = true
        val r = getView()

        if (r.bottomSheetBehavior!!.state == BottomSheetBehavior.STATE_EXPANDED) {
            r.bottomSheetBehavior!!.state = BottomSheetBehavior.STATE_COLLAPSED
            isBackPressed = false
        }

        if (r.address_map_marker_layout.visibility == View.VISIBLE) {
            showStartUI()
            isBackPressed = false
        }

        if (detailRideView != null) {
            val cardsBottomSheetBehavior = detailRideView!!.cardsBottomSheetBehavior
            val commentBottomSheetBehavior = detailRideView!!.commentBottomSheetBehavior

            if (cardsBottomSheetBehavior!!.state == BottomSheetBehavior.STATE_EXPANDED || commentBottomSheetBehavior!!.state == BottomSheetBehavior.STATE_EXPANDED) {
                cardsBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                commentBottomSheetBehavior!!.state = BottomSheetBehavior.STATE_COLLAPSED
            } else {
                //dynamic remove layout
                dynamicReplaceView(false)

                //clear routing from map
                clearMapObjects()

                //block too often request
                startProcessBlockRequest()

                //set cash suggest again
                getCashSuggest()

                r.from_adr.setText(fromAdr?.address)
                r.to_adr.setText("")
                toAdr = null
                detailRideView = null
            }

            isBackPressed = false
        }

        return isBackPressed
    }


    private fun showStartUI() {
        val r = getView()
        r.center_position.setImageResource(R.drawable.ic_map_marker)
        r.main_addresses_layout.visibility = View.VISIBLE
        r.address_map_marker_layout.visibility = View.GONE
        getView().bottomSheetBehavior!!.state = BottomSheetBehavior.STATE_EXPANDED
        isFromMapSearch = true
    }


    private fun dynamicReplaceView(showDetailRide: Boolean) {
        val r = getView()

        if (showDetailRide) {
            r.container_create_ride.visibility = View.GONE
            r.container_detail_ride.visibility = View.VISIBLE

            r.navView?.visibility = View.GONE
            r.back_btn.visibility = View.VISIBLE
        } else {
            r.container_create_ride.visibility = View.VISIBLE
            r.container_detail_ride.visibility = View.GONE

            r.navView?.visibility = View.VISIBLE
            r.back_btn.visibility = View.GONE
        }

        r.on_map_view.visibility = View.GONE
    }


    fun startProcessBlockRequest() {
        if (blockRequestHandler == null) {
            blockRequestHandler = Handler()
        }

        blockRequestHandler?.postDelayed(object : Runnable {
            override fun run() {
                isBlockRequest = false
                if (fromAdr == null)
                    requestGeocoder(createRideView.mapView?.map?.cameraPosition?.target)
                blockRequestHandler?.postDelayed(this, BLOCK_REQUEST_GEOCODER / 2)
            }
        }, BLOCK_REQUEST_GEOCODER)
    }


    private fun stopProcessBlockRequest() {
        blockRequestHandler?.removeCallbacksAndMessages(null)
        isBlockRequest = true
    }


    fun onDestroy() {
        createRideModel?.realm?.close()
    }


    private fun getView(): CreateRideView {
        return createRideView
    }

}