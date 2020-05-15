package bonch.dev.presentation.modules.driver.signup.tabledocs.presenter

import android.content.Intent
import androidx.fragment.app.Fragment
import bonch.dev.domain.entities.driver.signup.Step

interface ITableDocsPresenter {

    fun createDriver()

    fun instance(): TableDocsPresenter

    fun getCamera(fragment: Fragment)

    fun onActivityResult(fragment: Fragment, requestCode: Int, resultCode: Int, data: Intent?)

    fun onBackPressed(): Boolean

    fun getByValue(step: Int): Step?

    fun sortDocs()

}