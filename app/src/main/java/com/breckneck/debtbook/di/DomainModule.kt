package com.breckneck.debtbook.di

import com.breckneck.deptbook.domain.usecase.Ad.SaveClicksUseCase
import com.breckneck.deptbook.domain.usecase.Ad.GetClicksUseCase
import com.breckneck.deptbook.domain.usecase.Debt.*
import com.breckneck.deptbook.domain.usecase.Finance.DeleteAllFinancesByCategoryId
import com.breckneck.deptbook.domain.usecase.Finance.DeleteFinance
import com.breckneck.deptbook.domain.usecase.Finance.GetAllFinances
import com.breckneck.deptbook.domain.usecase.Finance.GetFinanceByCategoryId
import com.breckneck.deptbook.domain.usecase.Finance.ReplaceAllFinances
import com.breckneck.deptbook.domain.usecase.Finance.SetFinance
import com.breckneck.deptbook.domain.usecase.Finance.UpdateFinance
import com.breckneck.deptbook.domain.usecase.FinanceCategory.DeleteFinanceCategory
import com.breckneck.deptbook.domain.usecase.FinanceCategory.GetAllCategoriesWithFinances
import com.breckneck.deptbook.domain.usecase.FinanceCategory.GetAllFinanceCategories
import com.breckneck.deptbook.domain.usecase.FinanceCategory.GetFinanceCategoriesByState
import com.breckneck.deptbook.domain.usecase.FinanceCategory.ReplaceAllFinanceCategories
import com.breckneck.deptbook.domain.usecase.FinanceCategory.SetFinanceCategory
import com.breckneck.deptbook.domain.usecase.Goal.DeleteGoal
import com.breckneck.deptbook.domain.usecase.Goal.GetAllGoals
import com.breckneck.deptbook.domain.usecase.Goal.ReplaceAllGoals
import com.breckneck.deptbook.domain.usecase.Goal.SetGoal
import com.breckneck.deptbook.domain.usecase.Goal.UpdateGoal
import com.breckneck.deptbook.domain.usecase.GoalDeposit.DeleteGoalDepositsByGoalId
import com.breckneck.deptbook.domain.usecase.GoalDeposit.GetAllGoalDeposits
import com.breckneck.deptbook.domain.usecase.GoalDeposit.GetGoalDepositsByGoalId
import com.breckneck.deptbook.domain.usecase.GoalDeposit.ReplaceAllGoalsDeposits
import com.breckneck.deptbook.domain.usecase.GoalDeposit.SetGoalDeposit
import com.breckneck.deptbook.domain.usecase.Human.*
import com.breckneck.deptbook.domain.usecase.Settings.*
import org.koin.dsl.module


val domainModule = module {

    //HUMAN

    factory<GetAllHumansUseCase> {
        GetAllHumansUseCase(humanRepository = get())
    }

    factory<GetLastHumanIdUseCase> {
        GetLastHumanIdUseCase(humanRepository = get())
    }

    factory<AddSumUseCase> {
        AddSumUseCase(humanRepository = get())
    }

    factory<GetHumanSumDebtUseCase> {
        GetHumanSumDebtUseCase(humanRepository = get())
    }

    factory<DeleteHumanUseCase> {
        DeleteHumanUseCase(humanRepository = get())
    }

    factory<SetHumanUseCase> {
        SetHumanUseCase(humanRepository = get())
    }

    factory<UpdateHuman> {
        UpdateHuman(humanRepository = get())
    }

    factory<ReplaceAllHumans> {
        ReplaceAllHumans(humanRepository = get())
    }

    //DEBT

    factory<GetAllDebtsSumUseCase> {
        GetAllDebtsSumUseCase(humanRepository = get())
    }

    factory<GetAllDebts> {
        GetAllDebts(debtRepository = get())
    }

    factory<GetAllDebtsByIdUseCase> {
        GetAllDebtsByIdUseCase(debtRepository = get())
    }

    factory<DeleteDebtUseCase> {
        DeleteDebtUseCase(debtRepository = get())
    }

    factory<DeleteDebtsByHumanIdUseCase> {
        DeleteDebtsByHumanIdUseCase(debtRepository = get())
    }

    factory<SetDebtUseCase> {
        SetDebtUseCase(debtRepository = get())
    }

    factory<GetCurrentDateUseCase> {
        GetCurrentDateUseCase()
    }

    factory<SetDateUseCase> {
        SetDateUseCase()
    }

    factory<GetDebtShareString> {
        GetDebtShareString()
    }

    factory<EditDebtUseCase> {
        EditDebtUseCase(debtRepository = get())
    }

    factory<UpdateCurrentSumUseCase> {
        UpdateCurrentSumUseCase()
    }

    factory<GetDebtQuantity> {
        GetDebtQuantity(debtRepository = get())
    }

    factory<FilterDebts> {
        FilterDebts()
    }

    factory<ReplaceAllDebts> {
        ReplaceAllDebts(debtRepository = get())
    }

    //SETTINGS

    single<SetFirstMainCurrency> {
        SetFirstMainCurrency(settingsRepository = get())
    }

    single<GetFirstMainCurrency> {
        GetFirstMainCurrency(settingsRepository = get())
    }

    single<SetSecondMainCurrency> {
        SetSecondMainCurrency(settingsRepository = get())
    }

    single<GetSecondMainCurrency> {
        GetSecondMainCurrency(settingsRepository = get())
    }

    single<SetDefaultCurrency> {
        SetDefaultCurrency(settingsRepository = get())
    }

    single<GetDefaultCurrency> {
        GetDefaultCurrency(settingsRepository = get())
    }

    single<SetAddSumInShareText> {
        SetAddSumInShareText(settingsRepository = get())
    }

    single<GetAddSumInShareText> {
        GetAddSumInShareText(settingsRepository = get())
    }

    single<SetAppTheme> {
        SetAppTheme(settingsRepository = get())
    }

    single<GetAppTheme> {
        GetAppTheme(settingsRepository = get())
    }

    single<SetDebtQuantityForAppRateDialogShow> {
        SetDebtQuantityForAppRateDialogShow(settingsRepository = get())
    }

    single<GetDebtQuantityForAppRateDialogShow> {
        GetDebtQuantityForAppRateDialogShow(settingsRepository = get())
    }

    single<SetDebtOrder> {
        SetDebtOrder(settingsRepository = get())
    }

    single<GetDebtOrder> {
        GetDebtOrder(settingsRepository = get())
    }

    single<SetHumanOrder> {
        SetHumanOrder(settingsRepository = get())
    }

    single<GetHumanOrder> {
        GetHumanOrder(settingsRepository = get())
    }

    single<SetIsAuthorized> {
        SetIsAuthorized(settingsRepository = get())
    }

    single<GetIsAuthorized> {
        GetIsAuthorized(settingsRepository = get())
    }

    single<SetUserData> {
        SetUserData(settingsRepository = get())
    }

    single<GetUserData> {
        GetUserData(settingsRepository = get())
    }

    single<SetLastSyncDate> {
        SetLastSyncDate(settingsRepository = get())
    }

    single<GetLastSyncDate> {
        GetLastSyncDate(settingsRepository = get())
    }

    single<SetFinanceCurrency> {
        SetFinanceCurrency(settingsRepository = get())
    }

    single<GetFinanceCurrency> {
        GetFinanceCurrency(settingsRepository = get())
    }

    single<SetPINCodeEnabled> {
        SetPINCodeEnabled(settingsRepository = get())
    }

    single<GetPINCodeEnabled> {
        GetPINCodeEnabled(settingsRepository = get())
    }

    single<SetPINCode> {
        SetPINCode(settingsRepository = get())
    }

    single<GetPINCode> {
        GetPINCode(settingsRepository = get())
    }

    single<GetIsFingerprintAuthEnabled> {
        GetIsFingerprintAuthEnabled(settingsRepository = get())
    }

    single<SetIsFingerprintAuthEnabled> {
        SetIsFingerprintAuthEnabled(settingsRepository = get())
    }

    //FINANCE
    single<SetFinance> {
        SetFinance(financeRepository = get())
    }

    single<GetAllFinances> {
        GetAllFinances(financeRepository = get())
    }

    single<GetFinanceByCategoryId> {
        GetFinanceByCategoryId(financeRepository = get())
    }

    single<DeleteFinance> {
        DeleteFinance(financeRepository = get())
    }

    single<UpdateFinance> {
        UpdateFinance(financeRepository = get())
    }

    single<DeleteAllFinancesByCategoryId> {
        DeleteAllFinancesByCategoryId(financeRepository = get())
    }

    single<ReplaceAllFinances> {
        ReplaceAllFinances(financeRepository = get())
    }

    //FINANCE CATEGORIES

    single<GetAllFinanceCategories> {
        GetAllFinanceCategories(financeCategoryRepository = get())
    }

    single<SetFinanceCategory> {
        SetFinanceCategory(financeCategoryRepository = get())
    }

    single<GetAllCategoriesWithFinances> {
        GetAllCategoriesWithFinances(financeCategoryRepository = get())
    }

    single<DeleteFinanceCategory> {
        DeleteFinanceCategory(financeCategoryRepository = get())
    }

    single<GetFinanceCategoriesByState> {
        GetFinanceCategoriesByState(financeCategoryRepository = get())
    }

    single<ReplaceAllFinanceCategories> {
        ReplaceAllFinanceCategories(financeCategoryRepository = get())
    }

    //ADS

    factory<SaveClicksUseCase> {
        SaveClicksUseCase(adRepository = get())
    }

    factory<GetClicksUseCase> {
        GetClicksUseCase(adRepository = get())
    }

    //GOALS

    factory<GetAllGoals> {
        GetAllGoals(goalRepository = get())
    }

    factory<SetGoal> {
        SetGoal(goalRepository = get())
    }

    factory<UpdateGoal> {
        UpdateGoal(goalRepository = get())
    }

    factory<DeleteGoal> {
        DeleteGoal(goalRepository = get())
    }

    factory<ReplaceAllGoals> {
        ReplaceAllGoals(goalRepository = get())
    }

    //GOAL DEPOSITS

    factory<GetAllGoalDeposits> {
        GetAllGoalDeposits(goalDepositRepository = get())
    }

    factory<GetGoalDepositsByGoalId> {
        GetGoalDepositsByGoalId(goalDepositRepository = get())
    }

    factory<SetGoalDeposit> {
        SetGoalDeposit(goalDepositRepository = get())
    }

    factory<DeleteGoalDepositsByGoalId> {
        DeleteGoalDepositsByGoalId(goalDepositRepository = get())
    }

    factory<ReplaceAllGoalsDeposits> {
        ReplaceAllGoalsDeposits(goalDepositRepository = get())
    }
}