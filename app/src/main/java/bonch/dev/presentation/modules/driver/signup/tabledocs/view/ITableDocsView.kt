package bonch.dev.presentation.modules.driver.signup.tabledocs.view

import android.widget.ImageView
import android.widget.TextView
import bonch.dev.presentation.interfaces.IBaseView

interface ITableDocsView : IBaseView {

    fun loadPhoto()

    fun getImgDocs(): Array<ImageView>

    fun getTitleDocs(): Array<TextView>

    fun getTicsDocs(): Array<ImageView>

    fun showNotification(text: String)

}