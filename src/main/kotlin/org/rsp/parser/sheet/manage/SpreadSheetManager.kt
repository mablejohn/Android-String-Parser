@file:Suppress("unused")

package org.rsp.parser.sheet.manage

import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.model.*
import java.io.IOException
import java.util.*
import java.util.Collections.singletonList

class SpreadSheetManager(private val service: Sheets) {

    @Throws(IOException::class)
    fun appendValues(spreadsheetId: String?, range: String?,
                     valueInputOption: String?, values: List<List<Any?>?>?): AppendValuesResponse? {

        val body: ValueRange? = ValueRange().setValues(values)
        val result: AppendValuesResponse? = service.spreadsheets()
                .values().append(spreadsheetId, range, body)
                .setValueInputOption(valueInputOption)
                .execute()

        System.out.printf("%d cells appended.", result?.updates?.updatedCells)
        return result
    }

    @Throws(IOException::class)
    fun batchGetValues(spreadsheetId: String?, ranges: List<String?>?): BatchGetValuesResponse? {

        val result: BatchGetValuesResponse? = service.spreadsheets().values().batchGet(spreadsheetId)
                .setRanges(ranges).execute()

        System.out.printf("%d ranges retrieved.", result?.valueRanges?.size)
        return result
    }

    @Throws(IOException::class)
    fun batchUpdate(spreadsheetId: String?, title: String?,
                    find: String?, replacement: String?): BatchUpdateSpreadsheetResponse? {

        val requests: ArrayList<Request?> = arrayListOf()
        requests.add(Request().setUpdateSpreadsheetProperties(UpdateSpreadsheetPropertiesRequest()
                .setProperties(SpreadsheetProperties().setTitle(title))
                .setFields("title")))

        requests.add(Request().setFindReplace(FindReplaceRequest()
                .setFind(find)
                .setReplacement(replacement)
                .setAllSheets(true)))

        val body: BatchUpdateSpreadsheetRequest? = BatchUpdateSpreadsheetRequest().setRequests(requests)
        val response: BatchUpdateSpreadsheetResponse? = service.spreadsheets().batchUpdate(spreadsheetId, body).execute()
        val findReplaceResponse: FindReplaceResponse? = response?.replies?.get(1)?.findReplace

        System.out.printf("%d replacements made.", findReplaceResponse?.occurrencesChanged)
        return response
    }

    @Throws(IOException::class)
    fun batchUpdateValues(spreadsheetId: String?, range: String?,
                          valueInputOption: String?,
                          values: List<List<Any?>?>?): BatchUpdateValuesResponse? {

        val data: ArrayList<ValueRange?> = arrayListOf()
        data.add(ValueRange()
                .setRange(range)
                .setValues(values))

        val body: BatchUpdateValuesRequest? = BatchUpdateValuesRequest()
                .setValueInputOption(valueInputOption)
                .setData(data)

        val result: BatchUpdateValuesResponse? = service.spreadsheets()
                .values().batchUpdate(spreadsheetId, body).execute()

        System.out.printf("%d cells updated.", result?.totalUpdatedCells)
        return result
    }

    @Throws(IOException::class)
    fun conditionalFormat(spreadsheetId: String?): BatchUpdateSpreadsheetResponse {

        val ranges: List<GridRange?> = singletonList(GridRange()
                .setSheetId(0)
                .setStartRowIndex(1)
                .setEndRowIndex(11)
                .setStartColumnIndex(0)
                .setEndColumnIndex(4)
        )

        val requests: List<Request?> = listOf(
                Request().setAddConditionalFormatRule(AddConditionalFormatRuleRequest()
                        .setRule(ConditionalFormatRule()
                                .setRanges(ranges)
                                .setBooleanRule(BooleanRule()
                                        .setCondition(BooleanCondition()
                                                .setType("CUSTOM_FORMULA")
                                                .setValues(singletonList(
                                                        ConditionValue().setUserEnteredValue(
                                                                "=GT(\$D2,median(\$D$2:\$D$11))")
                                                ))
                                        )
                                        .setFormat(CellFormat().setTextFormat(
                                                TextFormat().setForegroundColor(
                                                        Color().setRed(0.8f))
                                        ))
                                )
                        )
                        .setIndex(0)
                ),
                Request().setAddConditionalFormatRule(AddConditionalFormatRuleRequest()
                        .setRule(ConditionalFormatRule()
                                .setRanges(ranges)
                                .setBooleanRule(BooleanRule()
                                        .setCondition(BooleanCondition()
                                                .setType("CUSTOM_FORMULA")
                                                .setValues(singletonList(
                                                        ConditionValue().setUserEnteredValue(
                                                                "=LT(\$D2,median(\$D$2:\$D$11))")
                                                ))
                                        )
                                        .setFormat(CellFormat().setBackgroundColor(
                                                Color().setRed(1f).setGreen(0.4f).setBlue(0.4f)
                                        ))
                                )
                        )
                        .setIndex(0)
                )
        )
        val body: BatchUpdateSpreadsheetRequest? = BatchUpdateSpreadsheetRequest()
                .setRequests(requests)
        val result: BatchUpdateSpreadsheetResponse = service.spreadsheets()
                .batchUpdate(spreadsheetId, body)
                .execute()

        System.out.printf("%d cells updated.", result.replies.size)
        return result
    }

    @Throws(IOException::class)
    fun create(title: String?): String? {

        var spreadsheet: Spreadsheet = Spreadsheet()
                .setProperties(SpreadsheetProperties()
                        .setTitle(title))

        spreadsheet = service.spreadsheets()
                .create(spreadsheet)
                .setFields("spreadsheetId")
                .execute()

        println("Spreadsheet ID: " + spreadsheet.spreadsheetId)
        return spreadsheet.spreadsheetId
    }

    @Throws(IOException::class)
    fun getValues(spreadsheetId: String?, range: String?): ValueRange? {

        val result: ValueRange? = service.spreadsheets().values().get(spreadsheetId, range).execute()
        val numRows = if (result!!.getValues() != null) result.getValues().size else 0
        System.out.printf("%d rows retrieved.", numRows)
        return result
    }

    @Throws(IOException::class)
    fun pivotTables(spreadsheetId: String?): BatchUpdateSpreadsheetResponse? {

        val sheetsRequests: ArrayList<Request?> = arrayListOf()
        sheetsRequests.add(Request().setAddSheet(AddSheetRequest()))
        sheetsRequests.add(Request().setAddSheet(AddSheetRequest()))
        val createSheetsBody: BatchUpdateSpreadsheetRequest? = BatchUpdateSpreadsheetRequest()
                .setRequests(sheetsRequests)
        val createSheetsResponse: BatchUpdateSpreadsheetResponse? = service.spreadsheets()
                .batchUpdate(spreadsheetId, createSheetsBody).execute()

        val sourceSheetId: Int = createSheetsResponse?.replies?.get(0)?.addSheet?.properties?.sheetId ?: 0
        val targetSheetId: Int = createSheetsResponse?.replies?.get(1)?.addSheet?.properties?.sheetId ?: 0

        val pivotTable: PivotTable? = PivotTable()
                .setSource(GridRange()
                        .setSheetId(sourceSheetId)
                        .setStartRowIndex(0)
                        .setStartColumnIndex(0)
                        .setEndRowIndex(20)
                        .setEndColumnIndex(7)
                )
                .setRows(singletonList(PivotGroup()
                        .setSourceColumnOffset(1)
                        .setShowTotals(true)
                        .setSortOrder("ASCENDING")
                ))
                .setColumns(singletonList(PivotGroup()
                        .setSourceColumnOffset(4)
                        .setShowTotals(true)
                        .setSortOrder("ASCENDING")
                ))
                .setValues(singletonList(PivotValue()
                        .setSummarizeFunction("COUNTA")
                        .setSourceColumnOffset(4)
                ))
        val requests: ArrayList<Request?> = arrayListOf()
        val updateCellsRequest: Request? = Request().setUpdateCells(UpdateCellsRequest()
                .setFields("*")
                .setRows(singletonList(RowData().setValues(singletonList(CellData().setPivotTable(pivotTable)))))
                .setStart(GridCoordinate()
                        .setSheetId(targetSheetId)
                        .setRowIndex(0)
                        .setColumnIndex(0)
                ))
        requests.add(updateCellsRequest)
        val updateCellsBody: BatchUpdateSpreadsheetRequest? = BatchUpdateSpreadsheetRequest()
                .setRequests(requests)

        return service.spreadsheets()
                .batchUpdate(spreadsheetId, updateCellsBody).execute()
    }

    @Throws(IOException::class)
    fun updateValues(spreadsheetId: String?, range: String?,
                     valueInputOption: String?, values: List<List<Any?>?>?): UpdateValuesResponse? {

        val body: ValueRange? = ValueRange()
                .setValues(values)
        val result: UpdateValuesResponse? = service.spreadsheets()
                .values().update(spreadsheetId, range, body)
                .setValueInputOption(valueInputOption)
                .execute()

        System.out.printf("%d cells updated.", result?.updatedCells)
        return result
    }
}