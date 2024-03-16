package entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class FinanceData(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var name: String,
    var sum: Double,
    var info: String
)
