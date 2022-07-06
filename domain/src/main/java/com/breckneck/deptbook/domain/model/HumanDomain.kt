package com.breckneck.deptbook.domain.model

data class HumanDomain(
    var id: Int,
    var name: String,
    var sumDebt: Double,
    var currency: String) {
}