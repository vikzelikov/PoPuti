package bonch.dev.domain.entities.common.ride

import bonch.dev.presentation.modules.passenger.getdriver.presenter.OffersMainTimer
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

open class OfferPrice(
    @SerializedName("offer")
    @Expose
    val offerPrice: Offer
)


data class Offer(
    @SerializedName("id")
    @Expose
    var offerId: Int? = null,

    @SerializedName("price")
    @Expose
    var price: Int? = null,

    @SerializedName("created_at")
    @Expose
    var createDate: String? = null,

    @SerializedName("driver_id")
    @Expose
    var driver: Driver? = null,

    var timeLine: Long = OffersMainTimer.TIME_EXPIRED_ITEM
)