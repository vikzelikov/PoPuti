package bonch.dev.poputi.domain.entities.driver.getpassenger


/**
 * 1 - Передумал
 * 2 - Форс-мажор
 * 3 - Пассажир с ребенком
 * 4 - Некорректное поведение пассажира
 * 5 - Пассажир не вышел
 * 6 - Другая причина
 * */

enum class ReasonCancel(val reason: Int) {
    CHANGE_MIND(1),
    FORCE_MAJEURE(2),
    PASSENGER_WITH_CHILD(3),
    PASSENGER_ERROR(4),
    PASSENGER_GO_OUT(5),
    OTHER_REASON(6)
}