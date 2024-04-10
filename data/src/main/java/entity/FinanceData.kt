package entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    foreignKeys = [ForeignKey(
        entity = FinanceCategoryData::class,
        parentColumns = ["id"],
        childColumns = ["financeCategoryId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class FinanceData(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var sum: Double,
    var isExpenses: Boolean,
    var date: Date,
    var info: String?,
    var financeCategoryId: Int
)
