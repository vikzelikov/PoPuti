package bonch.dev.presentation.modules.driver.signup.suggest.presenter

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.ContentFrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import bonch.dev.R
import bonch.dev.data.driver.signup.CarInfoSuggestModel
import bonch.dev.presentation.modules.driver.signup.suggest.adapters.SuggestAdapter
import bonch.dev.domain.utils.Constants
import bonch.dev.domain.utils.Keyboard
import bonch.dev.presentation.driver.signup.SuggestView
import kotlinx.android.synthetic.main.signup_car_info_suggest.*
import java.lang.IndexOutOfBoundsException
import java.util.*
import kotlin.collections.ArrayList

class SuggestPresenter(val suggestView: SuggestView) {

    private var carInfoSuggestModel: CarInfoSuggestModel? = null
    private var isCarNameSuggest: Boolean = false
    private var suggestAdapter: SuggestAdapter? = null
    private var suggestRecycler: RecyclerView? = null
    private var startSuggest: Collection<String>? = null


    init {
        if (carInfoSuggestModel == null) {
            carInfoSuggestModel = CarInfoSuggestModel()
        }
    }


    fun loadSuggest() {
        if (suggestAdapter == null) {
            initSuggestAdapter()
        }

        isCarNameSuggest = suggestView.intent.getBooleanExtra(Constants.BOOL_DATA, true)
        val inputStream = suggestView.resources.openRawResource(R.raw.cars_db)
        val scoreList = carInfoSuggestModel?.getCarsDB(inputStream)

        if (isCarNameSuggest) {
            //filter duplicates and sort by Alph
            if (scoreList != null) {
                startSuggest = filterCarNames(scoreList)
                setSuggest(startSuggest!!)
            }

        } else {
            val carName = suggestView.intent.getStringExtra(Constants.STRING_DATA)

            if (scoreList != null && carName != null) {
                startSuggest = filterCarModels(scoreList, carName)
                setSuggest(startSuggest!!)
            }

        }
    }


    private fun setSuggest(list: Collection<String>) {
        suggestAdapter?.list?.clear()
        suggestAdapter?.list?.addAll(list)
        suggestAdapter?.notifyDataSetChanged()
    }


    fun suggestDone(textSuggest: String) {
        val rootView =
            suggestView.findViewById<ContentFrameLayout>(android.R.id.content).rootView
        Keyboard.hideKeyboard(suggestView, rootView)

        val intent = Intent()
        intent.putExtra(Constants.STRING_DATA, textSuggest)
        intent.putExtra(Constants.BOOL_DATA, isCarNameSuggest)
        suggestView.setResult(AppCompatActivity.MODE_PRIVATE, intent)
        suggestView.finish()
    }


    private fun initSuggestAdapter() {
        suggestAdapter =
            SuggestAdapter(
                this,
                arrayListOf()
            )
        suggestRecycler = suggestView.recycler_suggest
        suggestRecycler?.layoutManager = LinearLayoutManager(suggestView)
        suggestRecycler?.adapter = suggestAdapter
    }


    fun filterList(q: String) {
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
}