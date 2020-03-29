package bonch.dev.presenter.passanger.getdriver

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.FragmentActivity
import bonch.dev.R
import bonch.dev.model.passanger.getdriver.CreateRideModel
import bonch.dev.model.passanger.getdriver.pojo.Coordinate.fromAdr
import bonch.dev.model.passanger.getdriver.pojo.Coordinate.toAdr
import bonch.dev.model.passanger.getdriver.pojo.Ride
import bonch.dev.model.passanger.getdriver.pojo.RidePoint
import bonch.dev.presenter.passanger.getdriver.adapters.AddressesListAdapter
import bonch.dev.utils.ChangeOpacity.getOpacity
import bonch.dev.utils.Keyboard.hideKeyboard
import bonch.dev.utils.Keyboard.showKeyboard
import bonch.dev.view.passanger.getdriver.CreateRideView
import bonch.dev.view.passanger.getdriver.DetailRideView
import bonch.dev.view.passanger.getdriver.MBottomSheet
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.yandex.mapkit.geometry.Point
import kotlinx.android.synthetic.main.create_ride_fragment.*
import kotlinx.android.synthetic.main.create_ride_fragment.view.*
import kotlinx.android.synthetic.main.create_ride_layout.*
import kotlin.math.abs
import kotlin.math.floor


class CreateRidePresenter(private val createRideView: CreateRideView) {

    var root: View? = null
    private var view: View? = null
    private var createRideModel: CreateRideModel? = null
    var addressesListAdapter: AddressesListAdapter? = null
    var detailRideView: DetailRideView? = null
    var firstAddress: Ride? = null

    var isMapSearchGestures = true
    var isFromMapSearch = true

    private var expandedValue: Int? = null
    private val shape = GradientDrawable()
    private val corners = floatArrayOf(14f, 14f, 14f, 14f, 0f, 0f, 0f, 0f)


    init {
        if (createRideModel == null) {
            createRideModel = CreateRideModel(this)
        }
    }


    fun addressesDone() {
        if (fromAdr != null && toAdr != null) {
            showDetailRideView()
            isMapSearchGestures = false
        }
    }


    fun requestSuggest(query: String) {
        if (query.length > 2) {
            createRideModel!!.requestSuggest(query, getView().userLocationPoint())
        } else {
            clearRecyclerSuggest()
        }
    }


    fun setRecyclerSuggest(suggestResult: ArrayList<Ride>) {
        addressesListAdapter?.list = suggestResult
        addressesListAdapter?.notifyDataSetChanged()
    }


    private fun clearRecyclerSuggest() {
        addressesListAdapter?.list?.clear()
        addressesListAdapter?.notifyDataSetChanged()
    }


    fun requestGeocoder(point: Point) {
        if (isMapSearchGestures) {
            createRideModel?.requestGeocoder(point)
        }
    }


    fun responseGeocoder(address: String?, point: Point?) {
        val fromAddress = getView().from_adr
        val toAddress = getView().to_adr
        val addressMapText = getView().address_map_text
        val ride: Ride?

        if (address != null) {
            if (isFromMapSearch) {
                fromAddress.setText(address)

                if (point != null && addressesListAdapter != null) {
                    ride = Ride(address, null, null, RidePoint(point.latitude, point.longitude))
                    if (fromAdr == null) {
                        //write for set address in case empty field "FROM"
                        firstAddress = ride
                    }
                    fromAdr = ride
                }
            } else {
                toAddress.setText(address)

                if (point != null && addressesListAdapter != null) {
                    ride = Ride(address, null, null, RidePoint(point.latitude, point.longitude))
                    toAdr = ride
                }
            }

            addressMapText.text = address
        }
    }


    private fun showDetailRideView() {
        val onMapView = root!!.on_map_view
        //dynamic add layout
        view = root?.findViewById<CoordinatorLayout>(R.id.create_ride_layout)
        val parent = view!!.parent as ViewGroup
        val index = parent.indexOfChild(view)
        view!!.visibility = View.GONE
        view = getView().layoutInflater.inflate(R.layout.detail_ride_layout, parent, false)
        parent.addView(view, index)

        println("!!!!!!!!!!!!!!")
        println(getView().navView)
        println("!!!!!!!!!!!!!!")

        getView().navView?.visibility = View.GONE
        root!!.back_btn.visibility = View.VISIBLE
        onMapView.visibility = View.GONE

        //create next screen
        detailRideView = DetailRideView(getView())
        detailRideView!!.onCreateView(fromAdr!!, toAdr!!)
    }


    private fun clearMapObjects() {
        detailRideView?.removeRoute()
    }


    fun touchCrossFrom(isFrom: Boolean) {
        if (isFrom) {
            getView().from_adr.setText("")
            fromAdr = null
        } else {
            getView().to_adr.setText("")
            toAdr = null
        }
    }


    fun touchAddress(isFrom: Boolean) {
        val fromAddress = getView().from_adr
        val toAddress = getView().to_adr
        val btnMapFrom = getView().btn_map_from
        val btnMapTo = getView().btn_map_to
        val toCross = getView().to_cross
        val fromCross = getView().from_cross

        if (isFrom) {
            if (toAddress.isFocused) {
                addressesListAdapter?.list?.clear()
                addressesListAdapter?.notifyDataSetChanged()
                fromAddress.requestFocus()
                showKeyboard(getActivity())
            }

            btnMapFrom.visibility = View.VISIBLE
            btnMapTo.visibility = View.GONE
            toCross.visibility = View.GONE

            if (fromAddress.text.isNotEmpty()) {
                fromCross.visibility = View.VISIBLE
            } else {
                fromCross.visibility = View.GONE
            }
        } else {
            if (fromAddress.isFocused) {
                addressesListAdapter?.list?.clear()
                addressesListAdapter?.notifyDataSetChanged()
                toAddress.requestFocus()
                showKeyboard(getActivity())
            }

            btnMapFrom.visibility = View.GONE
            btnMapTo.visibility = View.VISIBLE
            fromCross.visibility = View.GONE

            if (toAddress.text.isNotEmpty()) {
                toCross.visibility = View.VISIBLE
            } else {
                toCross.visibility = View.GONE
            }
        }

        getView().bottomSheetBehavior!!.state = BottomSheetBehavior.STATE_EXPANDED
    }


    fun touchMapBtn(isFrom: Boolean) {
        val mainAddressesLayout = getView().main_addresses_layout
        val addressMapMarkerLayout = getView().address_map_marker_layout
        val addressMapMarkerBtn = getView().address_map_marker_btn
        val circleMarker = getView().circle_marker
        val centerPosition = getView().center_position
        val addressMapText = getView().address_map_text

        mainAddressesLayout.visibility = View.GONE
        addressMapMarkerLayout.visibility = View.VISIBLE

        if (isFrom) {
            isFromMapSearch = true
            addressMapText.isSelected = true
            centerPosition.setImageResource(R.drawable.ic_map_marker)
            circleMarker.setImageResource(R.drawable.ic_input_marker_from)
            addressMapMarkerBtn.setBackgroundResource(R.drawable.bg_btn_blue)
        } else {
            //set to address
            if (fromAdr == null) {
                fromAdr = firstAddress
                getView().from_adr.setText(firstAddress?.address)
            }

            isFromMapSearch = false
            addressMapText.isSelected = true
            centerPosition.setImageResource(R.drawable.ic_map_marker_black)
            circleMarker.setImageResource(R.drawable.ic_input_marker_to)
            addressMapMarkerBtn.setBackgroundResource(R.drawable.bg_btn_black)
        }

        getView().bottomSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
        hideKeyboard(getActivity(), getView().view!!)
    }

    //TODO ask DANIL

    fun touchAddressMapMarkerBtn() {
        if (fromAdr != null && toAdr != null) {
            //addresses filled
            addressesDone()
        } else {
            getView().let {
                it.center_position.setImageResource(R.drawable.ic_map_marker)
                it.main_addresses_layout.visibility = View.VISIBLE
                it.address_map_marker_layout.visibility = View.GONE
                it.bottomSheetBehavior!!.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
    }


    fun onSlideBottomSheet(bottomSheet: View, slideOffset: Float) {
        shape.cornerRadii = corners

        if (slideOffset > 0) {
            expandedValue = floor((abs(slideOffset)) * 180).toInt()
            shape.setColor(Color.parseColor("#${getOpacity(expandedValue!!)}FFFFFF"))
            bottomSheet.background = shape
            getView().let {
                it.my_pos.alpha = 1 - slideOffset * 3
                it.center_position.alpha = 1 - slideOffset * 3
                it.on_map_view.alpha = slideOffset * 0.8f
                it.addresses_list.alpha = slideOffset
                it.bottom_sheet_arrow.alpha = slideOffset
            }
        }
    }


    fun onStateChangedBottomSheet(newState: Int) {
        if (newState == BottomSheetBehavior.STATE_DRAGGING) {
            hideKeyboard(getActivity(), getView().view!!)
            getView().from_adr.clearFocus()
            getView().to_adr.clearFocus()
        }
        if (newState == BottomSheetBehavior.STATE_EXPANDED) {
            if (getView().bottomSheetBehavior is MBottomSheet<*>) {
                (getView().bottomSheetBehavior as MBottomSheet<*>).swipeEnabled = true
            }
        } else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
            if (getView().bottomSheetBehavior is MBottomSheet<*>) {
                getView().let {
                    it.btn_map_from.visibility = View.GONE
                    it.btn_map_to.visibility = View.GONE
                    it.from_cross.visibility = View.GONE
                    it.to_cross.visibility = View.GONE
                    it.on_map_view.visibility = View.GONE
                    (it.bottomSheetBehavior as MBottomSheet<*>).swipeEnabled = false
                }
            }
        } else {
            getView().on_map_view.visibility = View.VISIBLE
        }
    }


    fun backPressed(): Boolean {
        var isBackPressed = true
        val addressesMapMarkerLayout = getView().address_map_marker_layout
        val centerPosition = getView().center_position
        val mainAddressesLayout = getView().main_addresses_layout
        val onMapView = getView().on_map_view
        val toAddress = getView().to_adr
        val navView = getView().navView
        val backBtn = getView().back_btn

        if (getView().bottomSheetBehavior!!.state == BottomSheetBehavior.STATE_EXPANDED) {
            getView().bottomSheetBehavior!!.state = BottomSheetBehavior.STATE_COLLAPSED
            isBackPressed = false
        }

        if (addressesMapMarkerLayout.visibility == View.VISIBLE) {
            centerPosition.setImageResource(R.drawable.ic_map_marker)
            mainAddressesLayout.visibility = View.VISIBLE
            addressesMapMarkerLayout.visibility = View.GONE
            getView().bottomSheetBehavior!!.state = BottomSheetBehavior.STATE_EXPANDED
            isFromMapSearch = true
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
                val parent = view!!.parent as ViewGroup
                parent.removeView(view)
                view = getView().view!!.findViewById<CoordinatorLayout>(R.id.create_ride_layout)
                view?.visibility = View.VISIBLE

                navView?.visibility = View.VISIBLE
                onMapView.visibility = View.GONE
                backBtn.visibility = View.GONE
                toAddress.setText("")
                //clear routing from map
                clearMapObjects()

                isMapSearchGestures = true
                toAdr = null
                detailRideView = null
            }

            isBackPressed = false
        }

        return isBackPressed
    }


    private fun getView(): CreateRideView {
        return createRideView
    }


    private fun getActivity(): FragmentActivity {
        return getView().activity!!
    }

}