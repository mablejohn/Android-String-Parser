@file:Suppress("unused")

package org.rsp.parser.helper

import org.apache.poi.hssf.usermodel.HSSFCell
import org.apache.poi.hssf.usermodel.HSSFRow
import org.apache.poi.hssf.usermodel.HSSFSheet
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.CellType
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
        val wb = HSSFWorkbook(inputStream)
        val sheet: HSSFSheet = wb.getSheetAt(excelData.sheetIndex)
        val rows: Iterator<*> = sheet.rowIterator()

        val resultList = arrayListOf<ResourceString>()
        while (rows.hasNext()) {
            val row = rows.next() as HSSFRow
            parseCell(row.cellIterator(), excelData)
                    .takeIf { it.name.isNotEmpty() && it.data.isNotEmpty() }
                    ?.let {
                        resultList.add(it)
                    }
        }
        return resultList
    }

    @Throws(IOException::class)
    fun readXLSXFile(excelData: ExcelReadFile): List<ResourceString> {

        val inputStream: InputStream = FileInputStream(excelData.excelFilePath)
        val wb = XSSFWorkbook(inputStream)
        val sheet: XSSFSheet = wb.getSheetAt(excelData.sheetIndex)
        val rows: Iterator<*> = sheet.rowIterator()

        val resultList = arrayListOf<ResourceString>()
        while (rows.hasNext()) {
            val row = rows.next() as XSSFRow
            parseCell(row.cellIterator(), excelData)
                    .takeIf { it.name.isNotEmpty() && it.data.isNotEmpty() }
                    ?.let {
                        resultList.add(it)
                    }
        }
        return resultList
    }

    @Throws(IOException::class)
    fun writeXLSFile(excelData: ExcelWriteFile) {

        val wb = HSSFWorkbook()
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
        val fileOut = FileOutputStream(excelData.excelFilePath)
        //write this workbook to an Output stream.
        wb.write(fileOut)
        fileOut.flush()
        fileOut.close()
    }

    @Throws(IOException::class)
    fun writeXLSXFile(excelData: ExcelWriteFile) {

        val wb = XSSFWorkbook()
        val sheet: XSSFSheet = wb.createSheet(excelData.sheetName)

        for (row in 0..4) {
            val xssfRow: XSSFRow = sheet.createRow(row)
            for (column in 0..4) {
                val cell: XSSFCell = xssfRow.createCell(column)
                val dataRow = excelData.data[row]

                if (column == COLUMN_KEY_INDEX) {
                    cell.setCellValue(dataRow.name)
                } else if (column == COLUMN_VALUE_INDEX) {
                    cell.setCellValue(dataRow.data)
                }
            }
        }

        val fileOut = FileOutputStream(excelData.excelFilePath)
        //write this workbook to an output stream.
        wb.write(fileOut)
        fileOut.flush()
        fileOut.close()
    }

    private fun parseCell(cells: Iterator<*>, excelData: ExcelReadFile): ResourceString {

        val rs = ResourceString()

        while (cells.hasNext()) {
            val cell = when (val rawCell = cells.next()) {
                is XSSFCell -> rawCell
                is HSSFCell -> rawCell
                else -> throw  UnsupportedClassVersionError("Invalid type detected..!")
            }
            when {
                (cell.cellType == CellType.STRING || cell.cellType == CellType.NUMERIC)
                        && cell.columnIndex == excelData.columnKeyIndex ->
                    rs.name = cell.stringCellValue

                (cell.cellType == CellType.STRING || cell.cellType == CellType.NUMERIC)
                        && (cell.columnIndex == excelData.columnValueIndex) ->
                    rs.data = cell.stringCellValue

                (cell.cellType == CellType.STRING || cell.cellType == CellType.NUMERIC)
                        && (cell.columnIndex == excelData.columnSuggestionIndex) -> {
                    rs.suggestion = cell.stringCellValue
                }
                else -> {
                }
            }
        }
        return rs
    }
}