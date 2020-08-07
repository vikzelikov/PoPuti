package bonch.dev.poputi.presentation.modules.driver.signup.tabledocs.view

import android.widget.ImageView
import android.widget.TextView
import bonch.dev.poputi.domain.entities.driver.signup.Step
import bonch.dev.poputi.presentation.interfaces.IBaseView

interface ITableDocsView : IBaseView {

    fun loadPhoto()

    fun getImgDocs(): Array<ImageView?>

    fun getTitleDocs(): Array<TextView?>

    fun getTicsDocs(): Array<ImageView?>

    fun showLoadingPhoto(idDoc: Step)

    fun hideLoadingPhoto()

}