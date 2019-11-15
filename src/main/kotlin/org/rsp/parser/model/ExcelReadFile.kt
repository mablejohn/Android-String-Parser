package org.rsp.parser.model

data class ExcelReadFile(override var excelFilePath: String) : IExcelFile {

    private var _columnKeyIndex = IExcelFile.COLUMN_KEY_INDEX
    private var _columnSuggestionIndex = IExcelFile.COLUMN_SUGGESTION_INDEX
    private var _columnValueIndex = IExcelFile.COLUMN_VALUE_INDEX
    private var _sheetIndex: Int = 0

    var columnKeyIndex: Int = IExcelFile.COLUMN_KEY_INDEX
        get() = _columnKeyIndex
        set(value) {
            field = value
            _columnKeyIndex = value
        }
    var columnSuggestionIndex: Int = IExcelFile.COLUMN_SUGGESTION_INDEX
        get() = _columnSuggestionIndex
        set(value) {
            field = value
            _columnSuggestionIndex = value
        }
    var columnValueIndex: Int = IExcelFile.COLUMN_VALUE_INDEX
        get() = _columnValueIndex
        set(value) {
            field = value
            _columnValueIndex = value
        }

    var sheetIndex: Int = 0
        get() = _sheetIndex
        set(value) {
            field = value
            _sheetIndex = value
        }
}