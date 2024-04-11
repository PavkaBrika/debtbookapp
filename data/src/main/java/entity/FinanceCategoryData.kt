package entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import util.FinanceCategoryStateData

@Entity
data class FinanceCategoryData(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var name: String,
    var state: FinanceCategoryStateData,
    var color: String,
    var image: Int
)
