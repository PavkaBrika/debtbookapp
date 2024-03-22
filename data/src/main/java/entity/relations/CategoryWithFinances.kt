package entity.relations

import androidx.room.Embedded
import androidx.room.Relation
import entity.FinanceCategoryData
import entity.FinanceData

//data class CategoryWithFinances(
//    @Embedded
//    val financeCategory: FinanceCategoryData,
//    @Relation(
//        parentColumn = "id",
//        entityColumn = "financeCategoryId"
//    )
//    val finances: List<FinanceData>
//)