package entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity
data class GoalData(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    var name: String,
    var sum: Double,
    var savedSum: Double,
    var creationDate: Date,
    var goalDate: Date
)
