package bonch.dev.presentation.modules.driver.getpassanger.orders.presenter

import bonch.dev.presentation.base.BasePresenter
import bonch.dev.presentation.interfaces.IBaseView

typealias SignUpErrorHandler<T> = (T) -> Unit

class OrdersPresenter : BasePresenter<IBaseView>() {


    fun testFunc() {
        //it = Boolean return variable
        onRequest(19, {onResponse(it)} )
    }


    fun onRequest(code: Int, myCallback: (Boolean) -> Unit) {
        Thread(Runnable {

            //Sleep Thread
            Thread.sleep(4000)

            if (code > 12) {
                myCallback(true)
            } else {
                myCallback(false)
            }

        }).start()
    }


    fun onResponse(isCorrect: Boolean) {
        //Println response
        println(isCorrect)
    }




}