package bonch.dev.domain.entities.passenger.getdriver

import bonch.dev.presentation.modules.passenger.getdriver.presenter.DriverMainTimer

open class Driver(
    var nameDriver: String? = null,
    var carName: String? = null,
    var carNumber: String? = null,
    var rating: Double? = null,
    var imgDriver: Int? = null,
    var price: Int? = null,
    var timeLine: Double = DriverMainTimer.TIME_EXPIRED_ITEM
) {
    override fun equals(other: Any?): Boolean {
        return nameDriver == (other as Driver).nameDriver
                && carName == other.carName
                && carNumber == other.carNumber
                && rating == other.rating
                && imgDriver == other.imgDriver
    }

    override fun hashCode(): Int {
        var result = nameDriver?.hashCode() ?: 0
        result = 31 * result + (carName?.hashCode() ?: 0)
        result = 31 * result + (carNumber?.hashCode() ?: 0)
        result = 31 * result + (rating?.hashCode() ?: 0)
        result = 31 * result + (imgDriver ?: 0)
        result = 31 * result + (price ?: 0)
        result = 31 * result + timeLine.hashCode()
        return result
    }
}

object DriverObject {
    var driver: Driver? = null
}