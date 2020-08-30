package bonch.dev.poputi.presentation.modules.common.profile.story.view

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import bonch.dev.poputi.R
import bonch.dev.poputi.domain.entities.common.ride.ActiveRide
import bonch.dev.poputi.domain.entities.common.ride.RideInfo
import bonch.dev.poputi.domain.utils.Constants
import bonch.dev.poputi.presentation.modules.common.profile.ContractPresenter
import bonch.dev.poputi.presentation.modules.common.profile.ContractView
import bonch.dev.poputi.presentation.modules.common.profile.support.SupportView
import bonch.dev.presentation.modules.common.profile.ProfileComponent
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.directions.DirectionsFactory
import com.yandex.mapkit.logo.Alignment
import com.yandex.mapkit.logo.HorizontalAlignment
import com.yandex.mapkit.logo.VerticalAlignment
import com.yandex.mapkit.mapview.MapView
import kotlinx.android.synthetic.main.detail_story_activity.*
import kotlinx.android.synthetic.main.map_order_activity.map
import kotlinx.android.synthetic.main.story_activity.back_btn
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


class DetailStoryView : AppCompatActivity(), ContractView.IDetailStoryView {

    @Inject
    lateinit var presenter: ContractPresenter.IDetailStoryPresenter

    private lateinit var mapView: MapView


    init {
        ProfileComponent.profileComponent?.inject(this)

        presenter.instance().attachView(this)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //init map
        MapKitFactory.setApiKey(Constants.API_KEY)
        MapKitFactory.initialize(this)
        DirectionsFactory.initialize(this)

        setContentView(R.layout.detail_story_activity)

        mapView = map as MapView

        mapView.map?.isRotateGesturesEnabled = false
        mapView.map?.isScrollGesturesEnabled = false
        mapView.map?.isTiltGesturesEnabled = false
        mapView.map?.isZoomGesturesEnabled = false


        mapView.map?.apply {
            val alignment = Alignment(HorizontalAlignment.RIGHT, VerticalAlignment.TOP)
            logo.setAlignment(alignment)
        }

        setListeners()

        ActiveRide.activeRide?.let {
            presenter.onReceiveRide(it)
        }
    }


    override fun setRideInfo(ride: RideInfo) {
        val key = "IS_PASSENGER"
        val isForPassenger = intent.getBooleanExtra(key, false)

        if (isForPassenger) {
            man_name?.text = ride.driver?.firstName
                ?.plus(" ")
                ?.plus(ride.driver?.lastName)

            ride.driver?.car?.let {
                car_name_and_number?.text = it.name
                    ?.plus(" ")
                    ?.plus(it.model)?.plus(" ")
                    ?.plus(it.number)
            }

            phone?.text = ride.driver?.phone
        } else {
            name_of_partner?.text = getString(R.string.passenger)
            car_name_and_number?.visibility = View.GONE

            man_name?.text = ride.passenger?.firstName
                ?.plus(" ")
                ?.plus(ride.driver?.lastName)

            phone?.text = ride.passenger?.phone
        }


        try {
            val startAt = ride.startAt
            val finishAt = ride.finishAt

            val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale("ru"))
            if (startAt != null) {
                val dateStartAt = format.parse(startAt)
                dateStartAt?.let {
                    val calendar = Calendar.getInstance(Locale("ru"))
                    calendar.time = it
                    val day = calendar.get(Calendar.DAY_OF_MONTH)
                    val mounth = calendar.getDisplayName(
                        Calendar.MONTH,
                        Calendar.LONG, Locale("ru")
                    )

                    var hours = calendar.get(Calendar.HOUR_OF_DAY).toString()
                    if (hours.length == 1) hours = "0".plus(hours)

                    var min = calendar.get(Calendar.MINUTE).toString()
                    if (min.length == 1) min = "0".plus(min)

                    date?.text = "$day"
                        .plus(" $mounth")
                        .plus(", ${getString(R.string.in1)} ")
                        .plus("$hours:$min")

                    if (finishAt != null) {
                        val dateFinishAt = format.parse(finishAt)
                        dateFinishAt?.let {
                            val start = dateStartAt.time
                            val finish = dateFinishAt.time
                            val delta = (finish - start) / 1000

                            val hours = delta / 3600
                            val mins = 1 + (delta - hours * 3600) / 60

                            if (hours > 0) {
                                time_in_way?.text = "$hours"
                                    .plus(" ч ")
                                    .plus("$mins")
                                    .plus(" мин в пути")
                            } else {
                                time_in_way?.text = "$mins".plus(" мин в пути")
                            }
                        }
                    }
                }
            }

        } catch (e: ParseException) {
        }

        from?.text = ride.position
        to?.text = ride.destination
        price?.text = ride.price?.toString()?.plus(" ₽")
    }


    override fun setListeners() {
        back_btn.setOnClickListener {
            finish()
        }

        support.setOnClickListener {
            val intent = Intent(this, SupportView::class.java)
            startActivity(intent)
        }

        copy_phone.setOnClickListener {
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
            val phone = phone.text.toString()
            val clip = ClipData.newPlainText("Phone", phone)
            clipboard?.setPrimaryClip(clip)

            Toast.makeText(this, getString(R.string.copyToBuffer), Toast.LENGTH_SHORT).show()
        }

        check.setOnClickListener {

        }
    }


    override fun getMap() = mapView


    override fun getNavHost(): NavController? = null


    override fun hideKeyboard() {}


    override fun showNotification(text: String) {}


    override fun showLoading() {

    }

    override fun hideLoading() {

    }


    override fun onStop() {
        super.onStop()
        MapKitFactory.getInstance().onStop()
        mapView.onStop()
    }


    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        mapView.onStart()
    }


}