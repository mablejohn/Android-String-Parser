package org.rsp.parser.gui.adapter

import org.rsp.parser.gui.constant.Constant
import javax.swing.DefaultComboBoxModel

class ColumnComboBoxModel(private val columnName: List<Char>) : DefaultComboBoxModel<String>() {
    /**
     * Returns the value at the specified index.
     * @param index the requested index
     * @return the value at `index`
     */
    override fun getElementAt(index: Int): String {
        return "${Constant.TITLE_COLUMN} ${columnName[index]}"
    }

    /**
     * Returns the length of the list.
     * @return the length of the list
     */
    override fun getSize(): Int {
        return columnName.size
    }
}