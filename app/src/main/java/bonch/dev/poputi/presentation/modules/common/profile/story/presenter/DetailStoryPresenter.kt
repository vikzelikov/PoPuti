package bonch.dev.poputi.presentation.modules.common.profile.story.presenter

import bonch.dev.poputi.domain.entities.common.ride.RideInfo
import bonch.dev.poputi.domain.interactor.common.profile.IProfileInteractor
import bonch.dev.poputi.presentation.base.BasePresenter
import bonch.dev.poputi.presentation.modules.common.profile.ContractPresenter
import bonch.dev.poputi.presentation.modules.common.profile.ContractView
import bonch.dev.poputi.presentation.modules.common.ride.routing.Routing
import bonch.dev.presentation.modules.common.profile.ProfileComponent
import com.yandex.mapkit.geometry.Point
import javax.inject.Inject

class DetailStoryPresenter : BasePresenter<ContractView.IDetailStoryView>(),
    ContractPresenter.IDetailStoryPresenter {


    @Inject
    lateinit var profileInteractor: IProfileInteractor

    @Inject
    lateinit var routing: Routing


    init {
        ProfileComponent.profileComponent?.inject(this)
    }


    override fun onReceiveRide(ride: RideInfo) {
        getView()?.setRideInfo(ride)

        Routing.mapObjects = null
        Routing.mapObjectsDriver = null

        val map = getView()?.getMap()
        val fromLat = ride.fromLat
        val fromLng = ride.fromLng
        val toLat = ride.toLat
        val toLng = ride.toLng

        //check
        val fromPoint = if (fromLat != null && fromLng != null) Point(fromLat, fromLng)
        else null

        //check
        val toPoint = if (toLat != null && toLng != null) Point(toLat, toLng)
        else null

        //set directions
        if (fromPoint != null && toPoint != null && map != null) {
            //set routes
            routing.submitRequestStory(fromPoint, toPoint, map)
        }
    }


    override fun instance() = this

}