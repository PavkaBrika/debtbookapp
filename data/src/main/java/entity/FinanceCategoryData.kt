package entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class FinanceCategoryData(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var name: String,
    var color: String,
    var image: Int,
)
