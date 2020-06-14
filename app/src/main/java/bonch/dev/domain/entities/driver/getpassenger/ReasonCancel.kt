package bonch.dev.domain.entities.driver.getpassenger

enum class ReasonCancel(val reason: Int) {
    PROBLEM_WITH_CAR(1),
    PASSANGER_WITH_CHILD(2),
    PASSANGER_ERROR(3),
    PASSANGER_GO_OUT(4),
    OTHER(5)
}