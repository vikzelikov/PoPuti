package bonch.dev.data.driver.signup

import java.io.InputStream

class CarInfoSuggestModel {

    fun getCarsDB(inputStr: InputStream): ArrayList<String>? {
        return CSVFile(inputStr).read()
    }

}