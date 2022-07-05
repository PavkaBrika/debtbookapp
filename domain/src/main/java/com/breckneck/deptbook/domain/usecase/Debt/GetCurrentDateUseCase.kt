package com.breckneck.deptbook.domain.usecase.Debt

import java.text.SimpleDateFormat
import java.util.*

class GetCurrentDateUseCase {
    fun execute() : String {
        val sdf = SimpleDateFormat("d MMM yyyy")
        return sdf.format(Calendar.getInstance().time)
    }
}