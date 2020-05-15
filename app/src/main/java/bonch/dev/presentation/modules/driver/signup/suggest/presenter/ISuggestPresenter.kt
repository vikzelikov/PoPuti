package bonch.dev.presentation.modules.driver.signup.suggest.presenter

interface ISuggestPresenter {

    fun loadSuggest(isCarNameSuggest: Boolean, carName: String?)

    fun instance(): SuggestPresenter

    fun filterList(q: String)

    fun suggestDone(textSuggest: String)

}