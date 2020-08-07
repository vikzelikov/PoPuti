package bonch.dev.poputi.presentation.interfaces

import android.app.Activity
import androidx.fragment.app.FragmentManager
import bonch.dev.poputi.presentation.interfaces.IBaseView

interface IMainActivity : IBaseView {

    fun pressBack()

    fun getFM(): FragmentManager

    fun getActivity(): Activity

    fun finishActivity()

    fun showFullLoading()

    fun hideFullLoading()

    fun changeInputMode()
}