package org.rsp.parser.gui.adapter

import com.intellij.structuralsearch.UnsupportedPatternException
import org.rsp.parser.gui.constant.Constant
import org.rsp.parser.model.ResourceString
import javax.swing.table.AbstractTableModel


class ResTableModel(
        private var resourceList: List<ResourceString>,
        private var columnNames: List<String>? = null
) : AbstractTableModel() {

    override fun getColumnName(column: Int): String {
        return this.columnNames?.get(column) ?: ""
    }

    /**
     * Returns `Object.class` regardless of `columnIndex`.
     *
     * @param columnIndex  the column being queried
     * @return the Object.class
     */
    override fun getColumnClass(columnIndex: Int): Class<*> {
        return getValueAt(0, columnIndex).javaClass
    }

    /**
     * Returns the number of columns in the model. A
     * `JTable` uses this method to determine how many columns it
     * should create and display by default.
     *
     * @return the number of columns in the model
     * @see .getRowCount
     */
    override fun getColumnCount(): Int {
        return this.columnNames?.size ?: MIN_COL_COUNT
    }

    /**
     * Returns the number of rows in the model. A
     * `JTable` uses this method to determine how many rows it
     * should display.  This method should be quick, as it
     * is called frequently during rendering.
     *
     * @return the number of rows in the model
     * @see .getColumnCount
     */
    override fun getRowCount(): Int {
        return resourceList.size
    }

    /**
     * Returns the value for the cell at `columnIndex` and
     * `rowIndex`.
     *
     * @param   rowIndex        the row whose value is to be queried
     * @param   columnIndex     the column whose value is to be queried
     * @return  the value Object at the specified cell
     */
    override fun getValueAt(rowIndex: Int, columnIndex: Int): Any {
        val resItem = resourceList[rowIndex]
        return when (columnIndex) {
            0 -> resItem.isSelected
            1 -> resItem.name
            2 -> resItem.data
            else -> throw UnsupportedPatternException("Invalid column index found..!!")
        }
    }

    /**
     * Returns false.  This is the default implementation for all cells.
     *
     * @param  rowIndex  the row being queried
     * @param  columnIndex the column being queried
     * @return false
     */
    override fun isCellEditable(rowIndex: Int, columnIndex: Int): Boolean {
        return columnIndex == 0
    }

    fun removeAll() {
        this.resourceList = listOf()
    }

    fun setModel(resourceList: List<ResourceString>) {
        this.resourceList = resourceList
    }

    /*
     * Don't need to implement this method unless your table's
     * data can change.
     */
    override fun setValueAt(value: Any?, row: Int, col: Int) {
        if (col == Constant.TABLE_COLUMN_SELECTED) {
            if (value is ResourceString) {
                resourceList[row].isSelected = value.isSelected
                fireTableCellUpdated(row, col)
            } else if (value is Boolean) {
                resourceList[row].isSelected = value
                fireTableCellUpdated(row, col)
            }
        }
    }

    init {
        if (null == this.columnNames) {
            this.columnNames = arrayOf(
                    COLUMN_NUMBER,
                    COLUMN_KEY,
                    COLUMN_VALUE
            ).toMutableList()
        }
    }

    companion object {
        const val COLUMN_KEY = "Key"
        const val COLUMN_NUMBER = "No"
        const val COLUMN_VALUE = "Value"
        const val MIN_COL_COUNT = 3
    }

    interface ColumnDataChanged {
        fun onColumnDataChanged(checked: Boolean, row: Int, column: Int)
    }
}