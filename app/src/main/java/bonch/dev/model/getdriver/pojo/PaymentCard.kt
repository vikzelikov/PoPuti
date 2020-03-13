package bonch.dev.model.getdriver.pojo

data class PaymentCard(
    var numberCard: String? = null,
    var validUntil: String? = null,
    var cvc: String? = null,
    var img: Int? = null,
    var isSelect: Boolean? = null
)