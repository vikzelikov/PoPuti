package bonch.dev.presentation.modules.passanger.regulardrive.presenter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.core.content.ContextCompat
import bonch.dev.App
import bonch.dev.presentation.base.BasePresenter
import bonch.dev.presentation.modules.passanger.regulardrive.view.ContractView

class CreateRegularDrivePresenter : BasePresenter<ContractView.ICreateRegularDriveView>(),
    ContractPresenter.ICreateRegularDrivePresenter {



    override fun getBitmap(drawableId: Int): Bitmap? {
        val context = App.appComponent.getContext()
        return context.getBitmapFromVectorDrawable(drawableId)
    }


    private fun Context.getBitmapFromVectorDrawable(drawableId: Int): Bitmap? {
        val drawable = ContextCompat.getDrawable(this, drawableId) ?: return null

        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        ) ?: return null
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)

        return bitmap
    }


    override fun instance(): CreateRegularDrivePresenter {
        return this
    }


}