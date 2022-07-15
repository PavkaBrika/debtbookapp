package com.breckneck.deptbook.domain.model

data class DebtDomain
    (var id: Int,
     var sum: Double,
     var idHuman: Int,
     var info: String?,
     var date: String) {
}