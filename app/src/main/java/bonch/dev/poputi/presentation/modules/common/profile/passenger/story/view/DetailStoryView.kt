package bonch.dev.poputi.presentation.modules.common.profile.passenger.story.view

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import bonch.dev.poputi.R
import bonch.dev.poputi.domain.entities.common.ride.ActiveRide
import bonch.dev.poputi.domain.entities.common.ride.RideInfo
import bonch.dev.poputi.domain.utils.Constants
import bonch.dev.poputi.presentation.modules.common.profile.ContractPresenter
import bonch.dev.poputi.presentation.modules.common.profile.ContractView
import bonch.dev.presentation.modules.common.profile.ProfileComponent
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.directions.DirectionsFactory
import com.yandex.mapkit.logo.Alignment
import com.yandex.mapkit.logo.HorizontalAlignment
import com.yandex.mapkit.logo.VerticalAlignment
import com.yandex.mapkit.map.CameraListener
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.user_location.UserLocationLayer
import kotlinx.android.synthetic.main.detail_story_activity.*
import kotlinx.android.synthetic.main.map_order_activity.*
import kotlinx.android.synthetic.main.map_order_activity.map
import kotlinx.android.synthetic.main.story_activity.*
import kotlinx.android.synthetic.main.story_activity.back_btn
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
        date?.text = "29 мая"
        time_in_way?.text = "9 минут"
        from?.text = ride.position
        to?.text = ride.destination
        price?.text = ride.price?.toString()?.plus(" ₽")

        driver_name?.text = ride.driver?.firstName?.plus(" ")?.plus(ride.driver?.lastName)
        phone?.text = ride.driver?.phone

        ride.driver?.car?.let {
            car_name_and_number?.text = it.name
                ?.plus(" ")
                ?.plus(it.model)?.plus(" ")
                ?.plus(it.number)
        }
    }


    override fun setListeners() {
        back_btn.setOnClickListener {
            finish()
        }

        support.setOnClickListener {

        }

        copy_phone.setOnClickListener {
            Toast.makeText(this, getString(R.string.copyToBuffer), Toast.LENGTH_SHORT).show()
        }

        check.setOnClickListener {

        }
    }


    override fun getNavHost(): NavController? = null


    override fun hideKeyboard() {}


    override fun showNotification(text: String) {}


    override fun showLoading() {

    }

    override fun hideLoading() {

    }


}