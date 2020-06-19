package bonch.dev.presentation.modules.passenger.getdriver.presenter

import android.os.Bundle
import android.os.Handler
import bonch.dev.App
import bonch.dev.R
import bonch.dev.domain.entities.common.ride.*
import bonch.dev.domain.entities.common.ride.Coordinate.toAdr
import bonch.dev.domain.entities.passenger.getdriver.ReasonCancel
import bonch.dev.domain.interactor.passenger.getdriver.IGetDriverInteractor
import bonch.dev.domain.utils.Vibration
import bonch.dev.presentation.base.BasePresenter
import bonch.dev.presentation.modules.passenger.getdriver.GetDriverComponent
import bonch.dev.presentation.modules.passenger.getdriver.view.ContractView
import bonch.dev.route.MainRouter
import com.google.gson.Gson
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
        val res = App.appComponent.getContext().resources
        //set new status ride
        RideStatus.status = StatusRide.SEARCH

        getDriverInteractor.listenerOnChangeRideStatus { data, error ->
            if (error != null || data == null) {
                getView()?.showNotification(res.getString(R.string.errorSystem))
            } else {
                val ride = Gson().fromJson(data, Ride::class.java)?.ride
                if (ride == null) {
                    getView()?.showNotification(res.getString(R.string.errorSystem))
                } else {
                    ActiveRide.activeRide = ride
                    getDriverDone(false)
                }
            }
        }

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


    private fun getDriverDone(isAcceptByPassenger: Boolean) {
        val res = App.appComponent.getContext().resources

        getView()?.removeBackground()

        //stop getting new driver
        clearData()

        //locale new status
        val status = ActiveRide.activeRide?.statusId
        status?.let {
            getByValue(it)?.let { status ->

                if(status == StatusRide.WAIT_FOR_DRIVER){
                    RideStatus.status = status

                    if (isAcceptByPassenger) {
                        //remote new status
                        //TODO get actual driver id
                        getDriverInteractor.setDriverInRide(1) { isSuccess ->
                            if (isSuccess) {
                                //next step
                                getView()?.nextFragment()
                            } else getView()?.showNotification(res.getString(R.string.errorSystem))
                        }
                    } else getView()?.nextFragment()
                }
            }
        }
    }


    private fun getByValue(status: Int) = StatusRide.values().firstOrNull { it.status == status }


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
            getDriverDone(true)
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
        getDriverInteractor.updateRideStatus(StatusRide.CANCEL) {}

        //stop getting new driver
        clearData()
        DriverObject.driver = null
        ActiveRide.activeRide = null

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


    override fun timeExpired() {
        getView()?.hideKeyboard()

        cancelDone(ReasonCancel.MISTAKE)
    }


    private fun clearData() {
        getDriverInteractor.disconnectSocket()
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
