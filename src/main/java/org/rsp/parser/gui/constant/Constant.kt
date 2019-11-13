package org.rsp.parser.gui.constant


class Constant {
    companion object {

        const val TABLE_COLUMN_KEY = 1
        const val TABLE_COLUMN_KEY_WIDTH = 300
        const val TABLE_COLUMN_SELECTED = 0
        const val TABLE_COLUMN_SELECTED_WIDTH = 300
        const val TABLE_COLUMN_VALUE = 2
        const val TABLE_COLUMN_VALUE_WIDTH = 700

        const val EXCEL_OUT_PATH = "String.xls"

        const val TITLE_COLUMN = "Column"
        const val TITLE_SHEET = "Sheet"

        @JvmStatic
        @Suppress("SpellCheckingInspection")
        fun getColumnIndex(): List<Char> {
            return "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toList()
        }
    }

    enum class ActionMode(val index: Int) {
        EXPORT(0),
        IMPORT(1)
    }
}