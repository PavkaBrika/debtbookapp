package com.breckneck.deptbook.data.storage.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*
@Entity
data class Debt(
    @PrimaryKey
    var id: Int,
    var sum: Double,
    var currency: String,
    var idHuman: Int,
    var info: String?,
    var date: String) {
}
