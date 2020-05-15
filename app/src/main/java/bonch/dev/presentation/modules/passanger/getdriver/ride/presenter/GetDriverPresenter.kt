package bonch.dev.presentation.modules.passanger.getdriver.ride.presenter

import android.os.Bundle
import android.os.Handler
import bonch.dev.App
import bonch.dev.R
import bonch.dev.domain.entities.passanger.getdriver.*
import bonch.dev.domain.entities.passanger.getdriver.Coordinate.toAdr
import bonch.dev.domain.interactor.passanger.getdriver.IGetDriverInteractor
import bonch.dev.domain.utils.Vibration
import bonch.dev.presentation.base.BasePresenter
import bonch.dev.presentation.modules.passanger.getdriver.GetDriverComponent
import bonch.dev.presentation.modules.passanger.getdriver.ride.view.ContractView
import bonch.dev.route.MainRouter
import com.yandex.mapkit.Animation
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import javax.inject.Inject


class GetDriverPresenter : BasePresenter<ContractView.IGetDriverView>(),
    ContractPresenter.IGetDriverPresenter {

    @Inject
    lateinit var getDriverInteractor: IGetDriverInteractor

    private var mainHandler: Handler? = null
    private var isAnimaionSearching = false
    private var isVibration = false

    private val REASON = "REASON"

    init {
        GetDriverComponent.getDriverComponent?.inject(this)
    }


    override fun startSearchDrivers() {
        //set new status ride
        RideStatus.status = StatusRide.SEARCH


        //TODO replace to searching with server
        mainHandler = Handler()
        mainHandler?.postDelayed(object : Runnable {
            override fun run() {
                getDriverInteractor.getNewDriver {
                    setNewDriverOffer(it)
                }
                mainHandler?.postDelayed(this, 3000)
            }
        }, 10000)
    }


    //create ride with SERVER
    override fun createRide(rideInfo: RideInfo) {
        val ride = RideInfo()
        ride.position = rideInfo.fromAdr?.address
        ride.fromLat = rideInfo.fromAdr?.point?.latitude
        ride.fromLng = rideInfo.fromAdr?.point?.longitude
        ride.destination = rideInfo.toAdr?.address
        ride.toLat = rideInfo.toAdr?.point?.latitude
        ride.toLng = rideInfo.toAdr?.point?.longitude
        ride.comment = "Temp comment"
        ride.price = rideInfo.price


        getDriverInteractor.createRide(ride) { isSuccess ->
            if (!isSuccess) {
                val res = App.appComponent.getContext().resources
                getView()?.showNotification(res.getString(R.string.errorSystem))
            }
        }
    }


    private fun selectDriverDone() {
        getView()?.removeBackground()

        //stop getting new driver
        clearData()

        //locale new status
        RideStatus.status = StatusRide.WAIT_FOR_DRIVER

        //remote new status
        getDriverInteractor.updateRideStatus(StatusRide.WAIT_FOR_DRIVER)

        //remote linking driver to ride
        //TODO get actual driver id
        getDriverInteractor.linkDriverToRide(9)

        //next step
        getView()?.nextFragment()
    }


    private fun setNewDriverOffer(driver: Driver) {
        getView()?.getAdapter()?.setNewDriver(driver)

        if (isAnimaionSearching) {
            //animation off
            val zoom = getView()?.getMap()?.map?.cameraPosition?.zoom
            val userPoint = getUserPoint()

            if (zoom != null && userPoint != null) {
                moveCamera(zoom, userPoint)
            }
        }


        if (!isVibration) {
            val context = App.appComponent.getContext()
            Vibration.start(context)

            isVibration = true
        }
    }


    override fun confirmAccept() {
        //stop main timer
        val driver = DriverObject.driver

        if (driver != null) {
            DriverMainTimer.getInstance()?.cancel()
            selectDriverDone()
        }
    }


    override fun moveCamera(zoom: Float, point: Point) {
        getView()?.getMap()?.map?.move(
            CameraPosition(point, zoom, 0.0f, 0.0f),
            Animation(Animation.Type.SMOOTH, 30f),
            null
        )
    }


    override fun cancelDone(reasonID: ReasonCancel) {
        //cancel ride remote
        getDriverInteractor.updateRideStatus(StatusRide.CANCEL)

        //stop getting new driver
        clearData()
        DriverObject.driver = null

        if (reasonID == ReasonCancel.MISTAKE || reasonID == ReasonCancel.OTHER) {
            toAdr = null
        }

        val res = App.appComponent.getContext().resources
        getView()?.showNotification(res.getString(R.string.rideCancel))

        val bundle = Bundle()
        bundle.putInt(REASON, reasonID.reason)
        MainRouter.showView(R.id.show_back_view, getView()?.getNavHost(), bundle)

    }


    override fun cancelDoneOtherReason(comment: String) {
        val res = App.appComponent.getContext().resources

        if (comment.trim().isEmpty()) {
            getView()?.showNotification(res.getString(R.string.writeYourProblemComment))
        } else {
            //TODO send reason to server

            getView()?.hideKeyboard()

            cancelDone(ReasonCancel.OTHER)
        }
    }


    override fun timeExpiredOk() {
        //clear data and redirect
        clearData()
        DriverObject.driver = null

        MainRouter.showView(R.id.show_back_view, getView()?.getNavHost(), null)
    }


    private fun clearData() {
        mainHandler?.removeCallbacksAndMessages(null)
        DriverMainTimer.getInstance()?.cancel()
        DriverMainTimer.deleteInstance()
    }


    private fun getUserPoint(): Point? {
        val userLocation = getView()?.getUserLocationLayer()
        return userLocation?.cameraPosition()?.target
    }


    override fun onUserLocationAttach() {
        //start animation searching
        val userPoint = getUserPoint()

        if (userPoint != null && !isAnimaionSearching) {
            getView()?.startAnimSearch(
                Point(
                    userPoint.latitude,
                    userPoint.longitude
                )
            )

            isAnimaionSearching = true
        }
    }


    override fun instance(): GetDriverPresenter {
        return this
    }

}
