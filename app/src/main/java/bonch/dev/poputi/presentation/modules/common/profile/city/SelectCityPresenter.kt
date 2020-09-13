package bonch.dev.poputi.presentation.modules.common.profile.city

import bonch.dev.poputi.App
import bonch.dev.poputi.R
import bonch.dev.poputi.domain.entities.common.ride.Address
import bonch.dev.poputi.domain.entities.common.ride.AddressPoint
import bonch.dev.poputi.domain.interactor.common.profile.IProfileInteractor
import bonch.dev.poputi.domain.utils.Geo
import bonch.dev.poputi.presentation.base.BasePresenter
import bonch.dev.poputi.presentation.modules.common.profile.ContractPresenter
import bonch.dev.poputi.presentation.modules.common.profile.ContractView
import bonch.dev.presentation.modules.common.profile.ProfileComponent
import java.util.*
import javax.inject.Inject

class SelectCityPresenter : BasePresenter<ContractView.ISelectCityView>(),
    ContractPresenter.ISelectCityPresenter {

    @Inject
    lateinit var interactor: IProfileInteractor

    private var startSuggest: Collection<Address>? = null


    init {
        ProfileComponent.profileComponent?.inject(this)
    }


    override fun loadSuggest() {
        val res = App.appComponent.getContext().resources
        val inputStream = res.openRawResource(R.raw.city)
        val scoreList = CSVFile(inputStream).read()


        //filter duplicates and sort by Alph
        if (scoreList != null) {
            val startSugg = filterCity(scoreList)
            setSuggest(startSugg)
            startSuggest = startSugg
        }

    }


    override fun suggestDone(address: Address) {
        getView()?.hideKeyboard()

        address.address?.let {
            getView()?.suggestDone(it)
        }

        Geo.selectedCity = address

        interactor.saveMyCity(address)

        Geo.isPreferCityGeo = true
        Geo.isRequestUpdateCity = true
    }


    private fun setSuggest(list: Collection<Address>) {
        getView()?.getAdapter()?.list?.clear()
        getView()?.getAdapter()?.list?.addAll(list)
        getView()?.getAdapter()?.notifyDataSetChanged()
    }


    override fun clearSuggest() {
        val adapter = getView()?.getAdapter()
        adapter?.list?.clear()
        adapter?.notifyDataSetChanged()
    }


    override fun filterList(q: String) {
        val query = q.trim().toLowerCase(Locale.ENGLISH)
        val countLetters = query.length
        val filteredList = arrayListOf<Address>()

        startSuggest?.forEach {
            try {
                val resSearch = it.address?.substring(0, countLetters)?.toLowerCase(Locale("ru"))
                if (query == resSearch) {
                    filteredList.add(it)
                }
            } catch (ex: Exception) {
            }

        }

        setSuggest(filteredList)
    }


    private fun filterCity(scoreList: ArrayList<String>): Collection<Address> {
        val list = hashSetOf<Address>()

        scoreList.forEach {
            try {
                val city = it.split(",")[0]
                val lat = it.split(",")[1].toDouble()
                val lon = it.split(",")[2].toDouble()

                val address = Address()
                address.id = 0
                address.address = city
                address.point = AddressPoint(lat, lon)
                list.add(address)
            } catch (ex: Exception) {
            }
        }

        return list.sortedBy { it.address }
    }


    override fun instance() = this

}
