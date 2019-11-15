@file:Suppress("unused")

package org.rsp.parser.model

interface IExcelFile {
    var excelFilePath: String

    companion object {
        const val COLUMN_KEY_INDEX = 0
        const val COLUMN_SUGGESTION_INDEX = 2
        const val COLUMN_VALUE_INDEX = 1
    }
}