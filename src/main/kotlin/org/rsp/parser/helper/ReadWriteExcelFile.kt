@file:Suppress("unused")

package org.rsp.parser.helper

import org.apache.poi.hssf.usermodel.HSSFCell
import org.apache.poi.hssf.usermodel.HSSFRow
import org.apache.poi.hssf.usermodel.HSSFSheet
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.xssf.usermodel.XSSFCell
import org.apache.poi.xssf.usermodel.XSSFRow
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.rsp.parser.model.ExcelReadFile
import org.rsp.parser.model.ExcelWriteFile
import org.rsp.parser.model.IExcelFile.Companion.COLUMN_KEY_INDEX
import org.rsp.parser.model.IExcelFile.Companion.COLUMN_SUGGESTION_INDEX
import org.rsp.parser.model.IExcelFile.Companion.COLUMN_VALUE_INDEX
import org.rsp.parser.model.ResourceString
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

class ReadWriteExcelFile {

    @Throws(IOException::class)
    fun readXLSFile(excelData: ExcelReadFile): List<ResourceString> {

        val inputStream: InputStream = FileInputStream(excelData.excelFilePath)
        inputStream.use { ioStream ->
            val poiDocument = HSSFWorkbook(inputStream)
            val sheet: HSSFSheet = poiDocument.getSheetAt(excelData.sheetIndex)
            val rows: Iterator<*> = sheet.rowIterator()
            val resultList = arrayListOf<ResourceString>()
            while (rows.hasNext()) {
                val row = rows.next() as Row
                parseCell(row, excelData)
                        .takeIf { it.name.isNotEmpty() && it.data.isNotEmpty() }
                        ?.let { resultList.add(it) }
            }

            ioStream.close()
            return resultList
        }
    }

    @Throws(IOException::class)
    fun readXLSXFile(excelData: ExcelReadFile): List<ResourceString> {

        val inputStream: InputStream = FileInputStream(excelData.excelFilePath)
        inputStream.use { ioStream ->
            val wb = XSSFWorkbook(inputStream)
            val sheet: XSSFSheet = wb.getSheetAt(excelData.sheetIndex)
            val rows: Iterator<*> = sheet.rowIterator()
            val resultList = arrayListOf<ResourceString>()
            while (rows.hasNext()) {
                val row = rows.next() as Row
                parseCell(row, excelData)
                        .takeIf { it.name.isNotEmpty() && it.data.isNotEmpty() }
                        ?.let { resultList.add(it) }
            }

            ioStream.close()
            return resultList
        }
    }

    @Throws(IOException::class)
    fun writeXLSFile(excelData: ExcelWriteFile) {

        val wb = HSSFWorkbook()
        val fileOut = FileOutputStream(excelData.excelFilePath)

        try {
            val sheet: HSSFSheet = wb.createSheet(excelData.sheetName)
            for (row in excelData.data.indices) {

                val dataRow = excelData.data[row]
                if (!dataRow.isSelected)
                    continue

                val hssfRow: HSSFRow = sheet.createRow(row)
                for (column in 0 until COLUMN_SUGGESTION_INDEX) {
                    val cell: HSSFCell = hssfRow.createCell(column)
                    if (column == COLUMN_KEY_INDEX) {
                        cell.setCellValue(dataRow.name)
                    } else if (column == COLUMN_VALUE_INDEX) {
                        cell.setCellValue(dataRow.data)
                    }
                }
            }
        } finally {
            wb.write(fileOut)
            fileOut.flush()
            fileOut.close()
        }
    }

    @Throws(IOException::class)
    fun writeXLSXFile(excelData: ExcelWriteFile) {

        val wb = XSSFWorkbook()
        val fileOut = FileOutputStream(excelData.excelFilePath)

        try {
            val sheet: XSSFSheet = wb.createSheet(excelData.sheetName)
            for (row in excelData.data.indices) {

                val dataRow = excelData.data[row]
                if (!dataRow.isSelected)
                    continue

                val xssfRow: XSSFRow = sheet.createRow(row)
                for (column in 0 until COLUMN_SUGGESTION_INDEX) {

                    val cell: XSSFCell = xssfRow.createCell(column)
                    if (column == COLUMN_KEY_INDEX) {
                        cell.setCellValue(dataRow.name)
                    } else if (column == COLUMN_VALUE_INDEX) {
                        cell.setCellValue(dataRow.data)
                    }
                }
            }
        } finally {
            wb.write(fileOut)
            fileOut.flush()
            fileOut.close()
        }
    }

    private fun parseCell(row: Row, excelData: ExcelReadFile): ResourceString {

        val cellKey = row.getCell(
                excelData.columnKeyIndex,
                Row.MissingCellPolicy.RETURN_NULL_AND_BLANK
        )
        val cellValue = row.getCell(
                excelData.columnValueIndex,
                Row.MissingCellPolicy.RETURN_NULL_AND_BLANK
        )
        val cellSuggestion = row.getCell(
                excelData.columnSuggestionIndex,
                Row.MissingCellPolicy.RETURN_NULL_AND_BLANK
        )

        with(ResourceString()) {
            this.suggestion = ""
            cellKey?.let { name = it.stringCellValue }
            cellValue?.let { data = it.stringCellValue }
            cellSuggestion?.let { suggestion = it.stringCellValue }
            return this
        }
    }

    enum class ExcelType(val type: Int) {
        XLS(0),
        XLSX(1)
    }
}