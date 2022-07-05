package com.breckneck.deptbook.domain.model

import java.util.*

data class DebtDomain(var id: Int, var sum: Double, var currency: String, var idHuman: Int, var info: String?, var date: String) {
}