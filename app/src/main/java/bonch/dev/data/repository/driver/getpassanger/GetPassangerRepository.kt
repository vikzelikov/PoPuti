package bonch.dev.data.repository.driver.getpassanger

import bonch.dev.R
import bonch.dev.domain.entities.driver.getpassanger.Order
import bonch.dev.domain.interactor.driver.getpassanger.NewOrder

class GetPassangerRepository : IGetPassangerRepository {

    var i = 0
    override fun getNewOrder(callback: NewOrder) {
        val order = if (i % 2 == 0) {
            Order(
                "Костя $i",
                R.drawable.ava,
                344 + i * 9,
                4.3,
                "Вокзальная улица, 71",
                "Бонч-Бруевича на Дыбенко около метро кофе",
                12.2 + i * 9
            )
        } else {
            Order(
                "Александр $i",
                R.drawable.ava1,
                412 + i * 5,
                3.9,
                "Бонч-Бруевича на Дыбенко около метро кофе",
                "Вокзальная улица, 96А",
                3.4 + i * 9
            )
        }

        if (i < 17)
            callback(order)

        i++
    }

}