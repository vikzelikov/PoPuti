package bonch.dev.domain.entities.passanger.getdriver

/**
 * 1 - search driver
 * 2 - wait for driver
 * 3 - driver wait for passanger
 * 4 - in way
 * 5 - passanger get place
 * 6 - ride cancel
 * */

object RideStatus {
    var status: StatusRide = StatusRide.SEARCH
}

enum class StatusRide(val status: Int) {
    SEARCH(1),
    WAIT_FOR_DRIVER(2),
    WAIT_FOR_PASSANGER(3),
    IN_WAY(4),
    GET_PLACE(5),
    CANCEL(6)
}