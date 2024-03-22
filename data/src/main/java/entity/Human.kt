package entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Human(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var name: String,
    var sumDebt: Double,
    var currency: String) {
}