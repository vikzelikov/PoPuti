package bonch.dev.presentation.interfaces

import androidx.navigation.NavController

interface IBaseView {

    fun setListeners()

    fun getNavHost(): NavController?

    fun hideKeyboard()

    fun showNotification(text: String)

    fun showLoading()

    fun hideLoading()

}