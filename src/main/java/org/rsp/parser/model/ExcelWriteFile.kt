package org.rsp.parser.model

data class ExcelWriteFile(
        override var excelFilePath: String,
        val data: List<ResourceString>)
    : IExcelFile {

    private var _colNames: Array<String>? = arrayOf("A", "B", "C")
    private var _sheetName: String? = "String Resource"

    /**
     * Optional field
     * Used to name the columns.
     */
    var colNames: Array<String>? = arrayOf("A", "B", "C")
        get() = _colNames
        set(value) {
            field = value
            _colNames = value
        }

    /**
     * Optional field
     * This is the name of the sheet
     */
    var sheetName: String? = "String Resource"
        get() = _sheetName
        set(value) {
            field = value
            _sheetName = value
        }
}