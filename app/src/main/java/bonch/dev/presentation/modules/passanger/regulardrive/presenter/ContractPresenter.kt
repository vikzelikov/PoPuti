package bonch.dev.presentation.modules.passanger.regulardrive.presenter

import android.graphics.Bitmap

interface ContractPresenter {

    interface IRegularDrivePresenter {
        fun instance(): RegularDrivePresenter
        fun createRegularDrive()
    }


    interface ICreateRegularDrivePresenter {
        fun instance(): CreateRegularDrivePresenter
        fun getBitmap(drawableId: Int): Bitmap?
    }

}