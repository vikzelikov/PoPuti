package bonch.dev.presentation.modules.driver.signup.suggest.view

import bonch.dev.presentation.interfaces.IBaseView
import bonch.dev.presentation.modules.driver.signup.suggest.adapters.SuggestAdapter

interface ISuggestView : IBaseView {

    fun getAdapter(): SuggestAdapter

    fun suggestDone(textSuggest: String, isCarNameSuggest: Boolean)

}