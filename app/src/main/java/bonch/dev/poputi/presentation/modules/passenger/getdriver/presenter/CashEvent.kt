package bonch.dev.poputi.presentation.modules.passenger.getdriver.presenter

import bonch.dev.poputi.domain.entities.common.ride.Address
import bonch.dev.poputi.domain.interactor.passenger.getdriver.IGetDriverInteractor

/**
 * Class works with cash request to Yandex MapKit server
 * and cashed suggest addresses (autocomplete)
 * */

class CashEvent(private val getDriverInteractor: IGetDriverInteractor) {

    private val CASH_VALUE_COUNT = 12

    var cashSuggests: ArrayList<Address>? = null


    fun getCashSuggest(): ArrayList<Address>? {
        if (cashSuggests == null) {
            val interactor = getDriverInteractor
            interactor.initRealm()
            cashSuggests = interactor.getCashSuggest()
        }

        return cashSuggests
    }


    fun saveCashSuggest(adr: Address) {
        val tempList = arrayListOf<Address>()
        val interactor = getDriverInteractor

        val suggest = cashSuggests

        if (!suggest.isNullOrEmpty()) {
            tempList.addAll(suggest)
            //sort by Realm-id
            tempList.sortBy {
                it.id
            }
        }

        if (!tempList.contains(adr)) {
            if (tempList.isEmpty()) {
                adr.id = tempList.size + 1
                tempList.add(adr)
            } else {
                //get id the last item > inc > init
                adr.id = tempList.last().id + 1
                tempList.add(adr)
            }

            //clear cash if it has too much memory
            if (suggest != null && tempList.size > CASH_VALUE_COUNT) {
                for (i in 0 until suggest.size) {
                    if (suggest[i] == tempList[0]) {
                        val rideDel = suggest[i]
                        interactor.deleteCashSuggest(rideDel)
                        tempList.removeAt(0)
                        break
                    }
                }
            }

            //update or save cash
            interactor.saveCashSuggest(tempList)
            //clear and get again
            cashSuggests = null
            getCashSuggest()
        }
    }
}