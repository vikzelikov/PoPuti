package bonch.dev.poputi.presentation.modules.driver.signup.suggest.presenter

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader


class CSVFile(inputStr: InputStream) {

    private var inputStream: InputStream = inputStr

    fun read(): ArrayList<String>? {
        val resultList: ArrayList<String> = arrayListOf()
        val reader = BufferedReader(InputStreamReader(inputStream))
        try {
            var csvLine: String?
            while (reader.readLine().also { csvLine = it } != null) {
                val row = csvLine
                if (row != null)
                    resultList.add(row)
            }
        } catch (ex: IOException) {
            throw RuntimeException("Error in reading CSV file: $ex")
        } finally {
            try {
                inputStream.close()
            } catch (e: IOException) {
                throw RuntimeException("Error while closing input stream: $e")
            }
        }

        return resultList
    }
}