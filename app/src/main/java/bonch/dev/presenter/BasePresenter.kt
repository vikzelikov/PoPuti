package bonch.dev.presenter

import android.content.Context
import bonch.dev.MainActivity
import bonch.dev.model.passanger.BaseModel
import bonch.dev.model.passanger.getdriver.pojo.Driver

class BasePresenter (val mainActivity: MainActivity) {

    private var baseModel: BaseModel? = null

    init {
        if(baseModel == null){
            baseModel = BaseModel()
        }
    }

    fun getToken(context: Context): String? {
        return baseModel?.getToken(context)
    }


    fun getDriverData(context: Context): Driver? {
        return baseModel?.getDriverData(context)
    }
}