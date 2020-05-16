package bonch.dev.domain.entities.driver.getpassanger

import bonch.dev.presentation.modules.driver.getpassanger.presenter.OrdersTimer

open class Order (
    var name: String? = null,
    var img: Int? = null,
    var price: Int? = null,
    var rating: Double? = null,
    var from: String? = null,
    var to: String? = null,
    var userDistance: Double? = null,
    var time: Int = OrdersTimer.TIME_EXPIRED_ITEM
)