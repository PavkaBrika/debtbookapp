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
    var name: String,
    var sum: Double,
    var isRevenue: Boolean,
    var date: Date,
    var info: String?,
    var financeCategoryId: Int
)
