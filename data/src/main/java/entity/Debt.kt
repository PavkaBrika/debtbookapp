package entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Debt(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var sum: Double,
    var idHuman: Int,
    var info: String?,
    var date: String) {
}
