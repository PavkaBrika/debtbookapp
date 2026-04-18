package com.breckneck.debtbook.common

import com.breckneck.deptbook.domain.model.Finance
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

val String.Companion.empty
        get() = ""

fun Date.toDMYFormat(pattern: String = "d MMM yyyy"): String {
    val sdf = SimpleDateFormat(pattern, Locale.getDefault())
    return sdf.format(this)
}

fun Double.format(pattern: String = "###,###,###.##"): String {
    val symbols = DecimalFormatSymbols().apply { groupingSeparator = ' ' }
    val df = DecimalFormat(pattern , symbols)
    return df.format(this)
}
