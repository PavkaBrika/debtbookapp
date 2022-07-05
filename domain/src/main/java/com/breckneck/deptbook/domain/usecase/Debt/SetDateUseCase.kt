package com.breckneck.deptbook.domain.usecase.Debt

import java.text.SimpleDateFormat
import java.util.*

class SetDateUseCase {

    fun execute(year: Int, month: Int, day: Int): String {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, day)
        val sdf = SimpleDateFormat("d MMM yyyy")
        return sdf.format(calendar.time)
    }
}