package bonch.dev.domain.entities.passenger.getdriver

/**
 * 1 - Передумал
 * 2 - Водитель попросил отменить
 * 3 - Слишком долго ждать
 * 4 - Заказал по ошибке
 * 5 - Другая причина
 * */

enum class ReasonCancel(val reason: Int) {
    CHANGE_MIND(1),
    DRIVER_CANCEL(2),
    WAIT_LONG(3),
    MISTAKE_ORDER(4),
    OTHER_REASON(5)
}