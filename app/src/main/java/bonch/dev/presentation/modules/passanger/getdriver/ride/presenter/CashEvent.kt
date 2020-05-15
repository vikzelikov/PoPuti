package bonch.dev.presentation.modules.passanger.getdriver.ride.presenter

import bonch.dev.domain.entities.passanger.getdriver.Address

/**
 * Class works with cash request to Yandex MapKit server
 * and cashed suggest addresses (autocomplete)
 * */

class CashEvent(private val presenter: CreateRidePresenter) {

    private val CASH_VALUE_COUNT = 12

    var cashSuggest: ArrayList<Address>? = null


    fun getCashSuggest() {
        if (cashSuggest == null) {
            val interactor = presenter.getDriverInteractor
            interactor.initRealm()
            cashSuggest = interactor.getCashSuggest()
        }

        val suggest = cashSuggest

        if (suggest != null) {
            val adapter = presenter.getView()?.getAddressesAdapter()
            adapter?.list?.clear()
            adapter?.list?.addAll(suggest)
            adapter?.notifyDataSetChanged()
        }
    }


    fun saveCashSuggest(adr: Address) {
        val tempList = arrayListOf<Address>()
        val interactor = presenter.getDriverInteractor

        val suggest = cashSuggest

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
            cashSuggest = null
            getCashSuggest()
        }
    }

}