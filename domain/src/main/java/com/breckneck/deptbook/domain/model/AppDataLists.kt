package com.breckneck.deptbook.domain.model

data class AppDataLists(
    val humanList: List<HumanDomain>,
    val debtList: List<DebtDomain>,
    val financeList: List<Finance>,
    val financeCategoryList: List<FinanceCategory>,
    val goalList: List<Goal>,
    val goalDepositList: List<GoalDeposit>
)