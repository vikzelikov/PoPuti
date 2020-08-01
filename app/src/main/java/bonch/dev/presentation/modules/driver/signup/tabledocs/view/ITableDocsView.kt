package bonch.dev.presentation.modules.driver.signup.tabledocs.view

import android.widget.ImageView
import android.widget.TextView
import bonch.dev.domain.entities.driver.signup.Step
import bonch.dev.presentation.interfaces.IBaseView

interface ITableDocsView : IBaseView {

    fun loadPhoto()

    fun getImgDocs(): Array<ImageView?>

    fun getTitleDocs(): Array<TextView?>

    fun getTicsDocs(): Array<ImageView?>

    fun showLoadingPhoto(idDoc: Step)

    fun hideLoadingPhoto()

}