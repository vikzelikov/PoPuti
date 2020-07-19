package bonch.dev.presentation.interfaces

import android.app.Activity
import androidx.fragment.app.FragmentManager

interface IMainActivity : IBaseView {

    fun pressBack()

    fun getFM(): FragmentManager

    fun getActivity(): Activity

    fun finishActivity()

    fun showFullLoading()

    fun hideFullLoading()

    fun changeInputMode()
}