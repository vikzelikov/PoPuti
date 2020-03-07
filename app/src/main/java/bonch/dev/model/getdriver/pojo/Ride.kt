package bonch.dev.model.getdriver.pojo

import com.yandex.mapkit.geometry.Point

data class Ride(
    var address: String? = null,
    var city: String? = null,
    var uri: String? = null,
    var point: Point? = null
)