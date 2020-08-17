package bonch.dev.poputi.domain.entities.common.ride

/**
 * 1 - search driver
 * 2 - wait for driver
 * 3 - driver wait for passanger
 * 4 - in way
 * 5 - passanger get place
 * 6 - ride cancel
 * 7 - REGULAR RIDE
 * */

enum class StatusRide(val status: Int) {
    SEARCH(1),
    WAIT_FOR_DRIVER(2),
    WAIT_FOR_PASSANGER(3),
    IN_WAY(4),
    GET_PLACE(5),
    CANCEL(6),
    REGULAR_RIDE(7)
}