package bonch.dev.presentation.modules.driver.signup.carinfo.presenter

import android.content.Intent
import androidx.fragment.app.Fragment
import bonch.dev.R
import bonch.dev.presentation.base.BasePresenter
import bonch.dev.presentation.modules.driver.signup.carinfo.view.ICarInfoView
import bonch.dev.presentation.modules.driver.signup.suggest.view.SuggestView
import bonch.dev.route.MainRouter


class CarInfoPresenter : BasePresenter<ICarInfoView>(), ICarInfoPresenter {

    private val BOOL_DATA = "BOOL_DATA"
    private val STRING_DATA = "STRING_DATA"


    override fun startSetDocs() {
        MainRouter.showView(R.id.show_signup_step_view, getView()?.getNavHost(), null)
    }


    override fun showSuggest(fragment: Fragment, isCarName: Boolean) {
        var carName: String? = null
        val context = fragment.context

        if (!isCarName) {
            carName = getView()?.getData()?.carName
        }

        val intent = Intent(context, SuggestView::class.java)
        intent.putExtra(BOOL_DATA, isCarName)
        carName?.let { intent.putExtra(STRING_DATA, it) }
        fragment.startActivityForResult(intent, 1)
    }


    override fun instance(): CarInfoPresenter {
        return this
    }
}