package bonch.dev.data.repository.driver.getpassanger

import bonch.dev.R
import bonch.dev.domain.entities.driver.getpassanger.Order
import bonch.dev.domain.entities.passanger.getdriver.AddressPoint
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
                "Метро площадь Ленина, Финляндский вокзал, Санкт-Петербург",
                "Парнас Сити",
                12.2 + i * 9,
                "Со мной будет собака, а еще можно проветрить автомобиль до моего приезда пожалуйта хочу ехать в проветренном автомобиле",
                59.953859, 30.354942,
                60.065902, 30.339715

            )
        } else {
            Order(
                "Александр $i",
                R.drawable.ava1,
                412 + i * 5,
                3.9,
                "ТРЦ Галерея, Лиговский проспект",
                "Дворцовая площадь, Palace Square, Saint Petersburg",
                3.4 + i * 9,
                null,
                59.928005, 30.360806,
                59.938738, 30.314848
            )
        }

        if (i < 17)
            callback(order)

        i++
    }

}