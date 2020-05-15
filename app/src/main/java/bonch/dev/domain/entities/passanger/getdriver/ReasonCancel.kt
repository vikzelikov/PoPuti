package bonch.dev.domain.entities.passanger.getdriver

enum class ReasonCancel(val reason: Int) {
    DRIVER_CANCEL(1),
    WAIT_LONG(2),
    MISTAKE(3),
    OTHER(4)
}