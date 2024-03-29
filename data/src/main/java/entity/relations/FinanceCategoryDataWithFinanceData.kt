package entity.relations

import androidx.room.Embedded
import androidx.room.Relation
import entity.FinanceCategoryData
import entity.FinanceData

data class FinanceCategoryDataWithFinanceData(
    @Embedded
    val financeCategoryData: FinanceCategoryData,
    @Relation(
        parentColumn = "id",
        entityColumn = "financeCategoryId"
    )
    val financeDataList: List<FinanceData>
)