package bonch.dev.poputi.presentation.modules.driver.signup.suggest.view

import bonch.dev.poputi.presentation.interfaces.IBaseView
import bonch.dev.poputi.presentation.modules.driver.signup.suggest.adapters.SuggestAdapter

interface ISuggestView : IBaseView {

    fun getAdapter(): SuggestAdapter

    fun suggestDone(textSuggest: String, isCarNameSuggest: Boolean)

}