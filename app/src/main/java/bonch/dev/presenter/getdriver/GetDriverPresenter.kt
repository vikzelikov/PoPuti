package bonch.dev.presenter.getdriver

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.FragmentActivity
import bonch.dev.MainActivity
import bonch.dev.MainActivity.Companion.hideKeyboard
import bonch.dev.MainActivity.Companion.showKeyboard
import bonch.dev.R
import bonch.dev.model.getdriver.GetDriverModel
import bonch.dev.model.getdriver.pojo.Ride
import bonch.dev.presenter.getdriver.adapters.AddressesListAdapter
import bonch.dev.view.getdriver.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.yandex.mapkit.geometry.Point
import kotlinx.android.synthetic.main.get_driver_fragment.*
import kotlinx.android.synthetic.main.get_driver_layout.*
import kotlin.math.abs
import kotlin.math.floor


class GetDriverPresenter(private val getDriverView: GetDriverView) {

    private var getDriverModel: GetDriverModel? = null
    private var view: View? = null
    var addressesListAdapter: AddressesListAdapter? = null
    var detailRideView: DetailRideView? = null
    var fromAdr: Ride? = null
    var toAdr: Ride? = null

    var isMapSearchGestures = true
    var isFromMapSearch = true

    private var expandedValue: Int? = null
    private val shape = GradientDrawable()
    private val corners = floatArrayOf(14f, 14f, 14f, 14f, 0f, 0f, 0f, 0f)


    init {
        if (getDriverModel == null) {
            getDriverModel = GetDriverModel(this)
        }
    }


    fun addressesDone() {
        if (fromAdr != null && toAdr != null) {
            showDetailView()
            isMapSearchGestures = false
        }
    }


    fun requestSuggest(query: String) {
        if (query.length > 2) {
            getDriverModel!!.requestSuggest(query, getView().userLocationPoint())
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
            getDriverModel!!.requestGeocoder(point)
        }
    }


    fun responseGeocoder(address: String?, point: Point?) {
        val ride: Ride?

        if (address != null) {
            if (isFromMapSearch) {
                getView().from_adr.setText(address)

                if (point != null && addressesListAdapter != null) {
                    ride = Ride(address, null, null, point)
                    fromAdr = ride
                }
            } else {
                getView().to_adr.setText(address)

                if (point != null && addressesListAdapter != null) {
                    ride = Ride(address, null, null, point)
                    toAdr = ride
                }
            }

            getView().address_map_text.text = address

        }

    }


    private fun showDetailView() {
        //dynamic add layout
        view = getView().view!!.findViewById<CoordinatorLayout>(R.id.get_driver_layout)
        val parent = view!!.parent as ViewGroup
        val index = parent.indexOfChild(view)
        view!!.visibility = View.GONE
        view = getDriverView.layoutInflater.inflate(R.layout.detail_ride_layout, parent, false)
        parent.addView(view, index)

        getDriverView.navView.visibility = View.GONE
        getDriverView.on_map_view.visibility = View.GONE

        detailRideView = DetailRideView(getDriverView, getView().view!!)
        detailRideView!!.onCreateView(fromAdr!!, toAdr!!)
    }


    private fun clearMapObjects() {
        if (detailRideView != null) {
            detailRideView!!.removeRoute()
        }
    }


    fun touchFromAddress() {
        if (getView().to_adr.isFocused) {
            addressesListAdapter?.list?.clear()
            addressesListAdapter?.notifyDataSetChanged()
        }

        getView().bottomSheetBehavior!!.state = BottomSheetBehavior.STATE_EXPANDED

        if (!getView().from_adr.isFocused) {
            getView().from_adr.requestFocus()
            showKeyboard(getActivity())
        }

        getView().btn_map_from.visibility = View.VISIBLE
        getView().btn_map_to.visibility = View.GONE
        getView().to_cross.visibility = View.GONE

        if (getView().from_adr.text.isNotEmpty()) {
            getView().from_cross.visibility = View.VISIBLE
        } else {
            getView().from_cross.visibility = View.GONE
        }
    }


    fun touchToAddress() {
        if (getView().to_adr.isFocused) {
            addressesListAdapter?.list?.clear()
            addressesListAdapter?.notifyDataSetChanged()
        }

        getView().bottomSheetBehavior!!.state = BottomSheetBehavior.STATE_EXPANDED

        if (!getView().to_adr.isFocused) {
            getView().to_adr.requestFocus()
            showKeyboard(getActivity())
        }

        getView().btn_map_from.visibility = View.GONE
        getView().btn_map_to.visibility = View.VISIBLE
        getView().to_cross.visibility = View.GONE

        if (getView().to_adr.text.isNotEmpty()) {
            getView().to_cross.visibility = View.VISIBLE
        } else {
            getView().to_cross.visibility = View.GONE
        }
    }


    fun touchFromMapBtn() {
        getView().main_addresses_layout.visibility = View.GONE
        getView().address_map_marker_layout.visibility = View.VISIBLE

        isFromMapSearch = true
        getView().address_map_text.isSelected = true
        getView().center_position.setImageResource(R.drawable.ic_map_marker)
        getView().circle_marker.setImageResource(R.drawable.ic_input_marker_from)
        getView().address_map_marker_btn.setBackgroundResource(R.drawable.bg_btn_blue)

        getView().bottomSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
        getView().from_adr.clearFocus()
        hideKeyboard(getActivity(), getView().view!!)
    }


    fun touchToMapBtn() {
        getView().main_addresses_layout.visibility = View.GONE
        getView().address_map_marker_layout.visibility = View.VISIBLE

        isFromMapSearch = false
        getView().address_map_text.isSelected = true
        getView().center_position.setImageResource(R.drawable.ic_map_marker_black)
        getView().circle_marker.setImageResource(R.drawable.ic_input_marker_to)
        getView().address_map_marker_btn.setBackgroundResource(R.drawable.bg_btn_black)

        getView().bottomSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
        getView().to_adr.clearFocus()
        hideKeyboard(getActivity(), getView().view!!)
    }


    fun touchAddressMapMarkerBtn() {
        if (fromAdr != null && toAdr != null) {
            //addresses filled
            addressesDone()
        } else {
            getView().center_position.setImageResource(R.drawable.ic_map_marker)
            getView().main_addresses_layout.visibility = View.VISIBLE
            getView().address_map_marker_layout.visibility = View.GONE
            getView().bottomSheetBehavior!!.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }


    fun onSlideBottomSheet(bottomSheet: View, slideOffset: Float) {
        shape.cornerRadii = corners

        if (slideOffset > 0) {
            expandedValue = floor((abs(slideOffset)) * 180).toInt()
            shape.setColor(Color.parseColor("#${MainActivity.getOpacity(expandedValue!!)}FFFFFF"))
            bottomSheet.background = shape

            getView().my_pos.alpha = 1 - slideOffset * 3
            getView().center_position.alpha = 1 - slideOffset * 3
            getView().on_map_view.alpha = slideOffset * 0.8f
            getView().addresses_list.alpha = slideOffset
            getView().bottom_sheet_arrow.alpha = slideOffset
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
                getView().btn_map_from.visibility = View.GONE
                getView().btn_map_to.visibility = View.GONE
                getView().from_cross.visibility = View.GONE
                getView().to_cross.visibility = View.GONE
                getView().on_map_view.visibility = View.GONE
                (getView().bottomSheetBehavior as MBottomSheet<*>).swipeEnabled =
                    false
            }
        } else
            getView().on_map_view.visibility = View.VISIBLE

    }


    fun backPressed(): Boolean {
        var isBackPressed = true

        if (getView().bottomSheetBehavior!!.state == BottomSheetBehavior.STATE_EXPANDED) {
            getView().bottomSheetBehavior!!.state = BottomSheetBehavior.STATE_COLLAPSED
            isBackPressed = false
        }

        if (getView().address_map_marker_layout.visibility == View.VISIBLE) {
            getView().center_position.setImageResource(R.drawable.ic_map_marker)
            getView().main_addresses_layout.visibility = View.VISIBLE
            getView().address_map_marker_layout.visibility = View.GONE
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
                view = getView().view!!.findViewById<CoordinatorLayout>(R.id.get_driver_layout)
                view!!.visibility = View.VISIBLE


                getView().navView.visibility = View.VISIBLE
                getView().on_map_view.visibility = View.GONE
                getView().to_adr.setText("")
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


    private fun getView(): GetDriverView {
        return getDriverView
    }


    private fun getActivity(): FragmentActivity {
        return getView().activity!!
    }

}