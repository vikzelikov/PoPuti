package bonch.dev.presenter.driver.signup

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.ContentFrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import bonch.dev.R
import bonch.dev.model.driver.signup.CarInfoSuggestModel
import bonch.dev.presenter.driver.signup.adapters.CarInfoSuggestAdapter
import bonch.dev.utils.Constants
import bonch.dev.utils.Keyboard
import bonch.dev.view.driver.signup.CarInfoSuggestView
import kotlinx.android.synthetic.main.signup_car_info_suggest.*
import java.io.InputStream
import java.lang.IndexOutOfBoundsException
import java.util.*
import kotlin.collections.ArrayList

class CarInfoSuggestPresenter(val carInfoSuggestView: CarInfoSuggestView) {

    private var carInfoSuggestModel: CarInfoSuggestModel? = null
    private var isCarNameSuggest: Boolean = false
    private var suggestAdapter: CarInfoSuggestAdapter? = null
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

        isCarNameSuggest = carInfoSuggestView.intent.getBooleanExtra(Constants.BOOL_DATA, true)
        val inputStream = carInfoSuggestView.resources.openRawResource(R.raw.cars_db)
        val scoreList = carInfoSuggestModel?.getCarsDB(inputStream)

        if (isCarNameSuggest) {
            //filter duplicates and sort by Alph
            if (scoreList != null) {
                startSuggest = filterCarNames(scoreList)
                setSuggest(startSuggest!!)
            }

        } else {
            val carName = carInfoSuggestView.intent.getStringExtra(Constants.STRING_DATA)

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
            carInfoSuggestView.findViewById<ContentFrameLayout>(android.R.id.content).rootView
        Keyboard.hideKeyboard(carInfoSuggestView, rootView)

        val intent = Intent()
        intent.putExtra(Constants.STRING_DATA, textSuggest)
        intent.putExtra(Constants.BOOL_DATA, isCarNameSuggest)
        carInfoSuggestView.setResult(AppCompatActivity.MODE_PRIVATE, intent)
        carInfoSuggestView.finish()
    }


    private fun initSuggestAdapter() {
        suggestAdapter = CarInfoSuggestAdapter(this, arrayListOf())
        suggestRecycler = carInfoSuggestView.recycler_suggest
        suggestRecycler?.layoutManager = LinearLayoutManager(carInfoSuggestView)
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