package com.breckneck.debtbook.common

import com.breckneck.deptbook.domain.model.Finance
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

val String.Companion.empty
        get() = ""

fun Date.toDMYFormat(): String {
    val sdf = SimpleDateFormat("d MMM yyyy", Locale.getDefault())
    return sdf.format(this)
}

fun Double.format(): String {
    val symbols = DecimalFormatSymbols().apply { groupingSeparator = ' ' }
    val df = DecimalFormat("###,###,###.##", symbols)
    return df.format(this)
}
