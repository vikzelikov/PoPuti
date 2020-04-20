package bonch.dev.data.repository.passanger.getdriver.pojo

data class Driver(
    var nameDriver: String? = null,
    var carName: String? = null,
    var carNumber: String? = null,
    var rating: Double? = null,
    var imgDriver: Int? = null,
    var price: Int? = null,
    var isArrived: Boolean = false,
    var timeLine: Int? = null
)

object DriverObject{
    var driver: Driver? = null
}