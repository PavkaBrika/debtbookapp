package database

import androidx.room.TypeConverter
import util.FinanceCategoryStateData
import java.util.Date

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time?.toLong()
    }

    @TypeConverter
    fun fromIntegerToFinanceCategoryState(value: Int): FinanceCategoryStateData {
        return when (value) {
            1 -> FinanceCategoryStateData.EXPENSE
            else -> FinanceCategoryStateData.INCOME
        }
    }

    @TypeConverter
    fun fromFinanceCategoryStateToInteger(financeCategoryStateData: FinanceCategoryStateData): Int {
        return when (financeCategoryStateData) {
            FinanceCategoryStateData.EXPENSE -> 1
            FinanceCategoryStateData.INCOME -> 2
        }
    }
}