package bonch.dev.poputi.presentation.modules.driver.signup.suggest.presenter

import bonch.dev.poputi.App
import bonch.dev.poputi.R
import bonch.dev.poputi.presentation.base.BasePresenter
import bonch.dev.poputi.presentation.modules.driver.signup.suggest.view.ISuggestView
import java.lang.IndexOutOfBoundsException
import java.util.*
import kotlin.collections.ArrayList

class SuggestPresenter : BasePresenter<ISuggestView>(), ISuggestPresenter {

    private var isCarNameSuggest: Boolean = false
    private var startSuggest: Collection<String>? = null


    override fun loadSuggest(isCarNameSuggest: Boolean, carName: String?) {
        val res = App.appComponent.getContext().resources
        val inputStream = res.openRawResource(R.raw.cars_db)
        val scoreList = CSVFile(inputStream).read()

        this.isCarNameSuggest = isCarNameSuggest

        if (isCarNameSuggest) {
            //filter duplicates and sort by Alph
            if (scoreList != null) {
                val startSugg = filterCarNames(scoreList)
                setSuggest(startSugg)
                startSuggest = startSugg
            }
        } else {
            if (scoreList != null && carName != null) {
                val startSugg = filterCarModels(scoreList, carName)
                setSuggest(startSugg)
                startSuggest = startSugg
            }
        }
    }


    private fun setSuggest(list: Collection<String>) {
        getView()?.getAdapter()?.list?.clear()
        getView()?.getAdapter()?.list?.addAll(list)
        getView()?.getAdapter()?.notifyDataSetChanged()
    }


    override fun suggestDone(textSuggest: String) {
        getView()?.hideKeyboard()

        getView()?.suggestDone(textSuggest, isCarNameSuggest)
    }



    override fun filterList(q: String) {
        val query = q.trim().toLowerCase(Locale.ENGLISH)
        val countLetters = query.length
        val filteredList = arrayListOf<String>()

        startSuggest?.forEach {
            try {
                val resSearch = it.substring(0, countLetters).toLowerCase(Locale.ENGLISH)
                if (query == resSearch) {
                    filteredList.add(it)
                }
            } catch (ex: IndexOutOfBoundsException) {
            }

        }

        setSuggest(filteredList)
    }


    private fun filterCarNames(scoreList: ArrayList<String>): Collection<String> {
        val list = hashSetOf<String>()

        scoreList.forEach {
            try {
                val t = it.split(";")[0]
                list.add(t)
            } catch (ex: IndexOutOfBoundsException) {
            }
        }

        return list.sortedBy { it }
    }


    private fun filterCarModels(scoreList: ArrayList<String>, carName: String): Collection<String> {
        val list = hashSetOf<String>()

        scoreList.forEach {
            try {
                val t = it.split(";")

                if (t[0] == carName) {
                    list.add(t[1])
                }
            } catch (ex: IndexOutOfBoundsException) {
            }
        }

        return list.sortedBy { it }
    }


    override fun instance(): SuggestPresenter {
        return this
    }
}