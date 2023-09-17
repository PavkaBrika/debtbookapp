package com.breckneck.deptbook.domain.usecase.Debt

import com.breckneck.deptbook.domain.model.DebtDomain
import com.google.gson.Gson
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.Cell
import java.io.File
import java.io.FileOutputStream
import java.text.DecimalFormat

class ExportDebtDataInExcelUseCase {

    fun execute(debtList: List<DebtDomain>, sheetName: String, rootFolder: File, sheetTitles: Array<String>): File {
        val decimalFormat = DecimalFormat("###,###,###.##")

        val classTitles = arrayOf("date", "sum", "info")
        val gson = Gson()
        val jsonArray = gson.toJsonTree(debtList).asJsonArray

        val wb = HSSFWorkbook()
        val sheet = wb.createSheet(sheetName)
        var cell: Cell
        var rowIndex = 0
        val row = sheet.createRow(rowIndex)
        ++rowIndex
        //set titles
        for ((i, title) in sheetTitles.withIndex()) {
            cell = row.createCell(i)
            cell.setCellValue(title)
        }

        for (j in 0..122) {
            sheet.setColumnWidth(j, (30*200))
        }

        for (i in 0 until jsonArray.size()) {
            val jsonObject = jsonArray.get(i).asJsonObject
            if (jsonObject != null) {
                val row1 = sheet.createRow(i + rowIndex)
                for ((i, value) in classTitles.withIndex()) {
                    cell = row1.createCell(i)
                    try {
                        if (i == 1) {
                            var sumString = decimalFormat.format(jsonObject.get(value).asDouble)
                            if (!sumString.contains("-"))
                                sumString = "+$sumString"
                            cell.setCellValue(sumString)
                        }
                        else
                            cell.setCellValue(jsonObject.get(value).asString)
                    } catch (e: java.lang.NullPointerException) {
                        e.printStackTrace()
                        cell.setCellValue("")
                    }
                }
            }
        }

        val filePath = File(rootFolder, "${sheetName}.xls")
        try {
            if (!filePath.exists())
                filePath.mkdirs()
            if (!filePath.exists()) {
                filePath.createNewFile()
            } else {
                filePath.delete()
                filePath.createNewFile()
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

        val file = filePath

        val fileOutputStream = FileOutputStream(file.path)
        wb.write(fileOutputStream)
        fileOutputStream.close()
        println("SUCCESS EXCEL ${file.absolutePath}")
        return file
    }
}