package com.breckneck.debtbook.di

import com.breckneck.deptbook.domain.repository.AdRepository
import com.breckneck.deptbook.domain.repository.AppDataRepository
import com.breckneck.deptbook.domain.repository.DebtRepository
import com.breckneck.deptbook.domain.repository.FinanceCategoryRepository
import com.breckneck.deptbook.domain.repository.FinanceRepository
import com.breckneck.deptbook.domain.repository.GoalDepositRepository
import com.breckneck.deptbook.domain.repository.GoalRepository
import com.breckneck.deptbook.domain.repository.HumanRepository
import com.breckneck.deptbook.domain.repository.SettingsRepository
import com.breckneck.deptbook.domain.usecase.AppData.ReplaceAllAppData
import com.breckneck.deptbook.domain.usecase.Ad.GetClicksUseCase
import com.breckneck.deptbook.domain.usecase.Ad.SaveClicksUseCase
import com.breckneck.deptbook.domain.usecase.Debt.DeleteDebtUseCase
import com.breckneck.deptbook.domain.usecase.Debt.DeleteDebtsByHumanIdUseCase
import com.breckneck.deptbook.domain.usecase.Debt.EditDebtUseCase
import com.breckneck.deptbook.domain.usecase.Debt.GetAllDebts
import com.breckneck.deptbook.domain.usecase.Debt.GetAllDebtsByIdUseCase
import com.breckneck.deptbook.domain.usecase.Human.GetAllDebtsSumUseCase
import com.breckneck.deptbook.domain.usecase.Debt.GetCurrentDateUseCase
import com.breckneck.deptbook.domain.usecase.Debt.GetDebtQuantity
import com.breckneck.deptbook.domain.usecase.Debt.GetDebtShareString
import com.breckneck.deptbook.domain.usecase.Debt.ReplaceAllDebts
import com.breckneck.deptbook.domain.usecase.Debt.SetDateUseCase
import com.breckneck.deptbook.domain.usecase.Debt.SetDebtUseCase
import com.breckneck.deptbook.domain.usecase.Debt.UpdateCurrentSumUseCase
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
import com.breckneck.deptbook.domain.usecase.Human.AddSumUseCase
import com.breckneck.deptbook.domain.usecase.Human.DeleteHumanUseCase
import com.breckneck.deptbook.domain.usecase.Human.GetAllHumansUseCase
import com.breckneck.deptbook.domain.usecase.Human.GetHumanSumDebtUseCase
import com.breckneck.deptbook.domain.usecase.Human.GetLastHumanIdUseCase
import com.breckneck.deptbook.domain.usecase.Human.ReplaceAllHumans
import com.breckneck.deptbook.domain.usecase.Human.SetHumanUseCase
import com.breckneck.deptbook.domain.usecase.Human.UpdateHuman
import com.breckneck.deptbook.domain.usecase.Settings.GetAddSumInShareText
import com.breckneck.deptbook.domain.usecase.Settings.GetAppTheme
import com.breckneck.deptbook.domain.usecase.Settings.GetDefaultCurrency
import com.breckneck.deptbook.domain.usecase.Settings.GetDebtOrder
import com.breckneck.deptbook.domain.usecase.Settings.GetDebtQuantityForAppRateDialogShow
import com.breckneck.deptbook.domain.usecase.Settings.GetFinanceCurrency
import com.breckneck.deptbook.domain.usecase.Settings.GetFirstMainCurrency
import com.breckneck.deptbook.domain.usecase.Settings.GetHumanOrder
import com.breckneck.deptbook.domain.usecase.Settings.GetIsFingerprintAuthEnabled
import com.breckneck.deptbook.domain.usecase.Settings.GetIsAuthorized
import com.breckneck.deptbook.domain.usecase.Settings.GetLastSyncDate
import com.breckneck.deptbook.domain.usecase.Settings.GetPINCode
import com.breckneck.deptbook.domain.usecase.Settings.GetPINCodeEnabled
import com.breckneck.deptbook.domain.usecase.Settings.GetSecondMainCurrency
import com.breckneck.deptbook.domain.usecase.Settings.GetUserData
import com.breckneck.deptbook.domain.usecase.Settings.SetAddSumInShareText
import com.breckneck.deptbook.domain.usecase.Settings.SetAppTheme
import com.breckneck.deptbook.domain.usecase.Settings.SetDefaultCurrency
import com.breckneck.deptbook.domain.usecase.Settings.SetDebtOrder
import com.breckneck.deptbook.domain.usecase.Settings.SetDebtQuantityForAppRateDialogShow
import com.breckneck.deptbook.domain.usecase.Settings.SetFinanceCurrency
import com.breckneck.deptbook.domain.usecase.Settings.SetFirstMainCurrency
import com.breckneck.deptbook.domain.usecase.Settings.SetHumanOrder
import com.breckneck.deptbook.domain.usecase.Settings.SetIsFingerprintAuthEnabled
import com.breckneck.deptbook.domain.usecase.Settings.SetIsAuthorized
import com.breckneck.deptbook.domain.usecase.Settings.SetLastSyncDate
import com.breckneck.deptbook.domain.usecase.Settings.SetPINCode
import com.breckneck.deptbook.domain.usecase.Settings.SetPINCodeEnabled
import com.breckneck.deptbook.domain.usecase.Settings.SetSecondMainCurrency
import com.breckneck.deptbook.domain.usecase.Settings.SetUserData
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DomainModule {

    // ── HUMAN ────────────────────────────────────────────────────────────────

    @Provides
    fun provideGetAllHumansUseCase(humanRepository: HumanRepository) =
        GetAllHumansUseCase(humanRepository = humanRepository)

    @Provides
    fun provideGetLastHumanIdUseCase(humanRepository: HumanRepository) =
        GetLastHumanIdUseCase(humanRepository = humanRepository)

    @Provides
    fun provideAddSumUseCase(humanRepository: HumanRepository) =
        AddSumUseCase(humanRepository = humanRepository)

    @Provides
    fun provideGetHumanSumDebtUseCase(humanRepository: HumanRepository) =
        GetHumanSumDebtUseCase(humanRepository = humanRepository)

    @Provides
    fun provideDeleteHumanUseCase(humanRepository: HumanRepository) =
        DeleteHumanUseCase(humanRepository = humanRepository)

    @Provides
    fun provideSetHumanUseCase(humanRepository: HumanRepository) =
        SetHumanUseCase(humanRepository = humanRepository)

    @Provides
    fun provideUpdateHuman(humanRepository: HumanRepository) =
        UpdateHuman(humanRepository = humanRepository)

    @Provides
    fun provideReplaceAllHumans(humanRepository: HumanRepository) =
        ReplaceAllHumans(humanRepository = humanRepository)

    @Provides
    fun provideGetAllDebtsSumUseCase(humanRepository: HumanRepository) =
        GetAllDebtsSumUseCase(humanRepository = humanRepository)

    // ── DEBT ─────────────────────────────────────────────────────────────────

    @Provides
    fun provideGetAllDebts(debtRepository: DebtRepository) =
        GetAllDebts(debtRepository = debtRepository)

    @Provides
    fun provideGetAllDebtsByIdUseCase(debtRepository: DebtRepository) =
        GetAllDebtsByIdUseCase(debtRepository = debtRepository)

    @Provides
    fun provideDeleteDebtUseCase(debtRepository: DebtRepository) =
        DeleteDebtUseCase(debtRepository = debtRepository)

    @Provides
    fun provideDeleteDebtsByHumanIdUseCase(debtRepository: DebtRepository) =
        DeleteDebtsByHumanIdUseCase(debtRepository = debtRepository)

    @Provides
    fun provideSetDebtUseCase(debtRepository: DebtRepository) =
        SetDebtUseCase(debtRepository = debtRepository)

    @Provides
    fun provideGetCurrentDateUseCase() = GetCurrentDateUseCase()

    @Provides
    fun provideSetDateUseCase() = SetDateUseCase()

    @Provides
    fun provideGetDebtShareString() = GetDebtShareString()

    @Provides
    fun provideEditDebtUseCase(debtRepository: DebtRepository) =
        EditDebtUseCase(debtRepository = debtRepository)

    @Provides
    fun provideUpdateCurrentSumUseCase() = UpdateCurrentSumUseCase()

    @Provides
    fun provideGetDebtQuantity(debtRepository: DebtRepository) =
        GetDebtQuantity(debtRepository = debtRepository)

    @Provides
    fun provideReplaceAllDebts(debtRepository: DebtRepository) =
        ReplaceAllDebts(debtRepository = debtRepository)

    // ── SETTINGS (singleton — read from SharedPreferences once per session) ──

    @Provides
    @Singleton
    fun provideSetFirstMainCurrency(settingsRepository: SettingsRepository) =
        SetFirstMainCurrency(settingsRepository = settingsRepository)

    @Provides
    @Singleton
    fun provideGetFirstMainCurrency(settingsRepository: SettingsRepository) =
        GetFirstMainCurrency(settingsRepository = settingsRepository)

    @Provides
    @Singleton
    fun provideSetSecondMainCurrency(settingsRepository: SettingsRepository) =
        SetSecondMainCurrency(settingsRepository = settingsRepository)

    @Provides
    @Singleton
    fun provideGetSecondMainCurrency(settingsRepository: SettingsRepository) =
        GetSecondMainCurrency(settingsRepository = settingsRepository)

    @Provides
    @Singleton
    fun provideSetDefaultCurrency(settingsRepository: SettingsRepository) =
        SetDefaultCurrency(settingsRepository = settingsRepository)

    @Provides
    @Singleton
    fun provideGetDefaultCurrency(settingsRepository: SettingsRepository) =
        GetDefaultCurrency(settingsRepository = settingsRepository)

    @Provides
    @Singleton
    fun provideSetAddSumInShareText(settingsRepository: SettingsRepository) =
        SetAddSumInShareText(settingsRepository = settingsRepository)

    @Provides
    @Singleton
    fun provideGetAddSumInShareText(settingsRepository: SettingsRepository) =
        GetAddSumInShareText(settingsRepository = settingsRepository)

    @Provides
    @Singleton
    fun provideSetAppTheme(settingsRepository: SettingsRepository) =
        SetAppTheme(settingsRepository = settingsRepository)

    @Provides
    @Singleton
    fun provideGetAppTheme(settingsRepository: SettingsRepository) =
        GetAppTheme(settingsRepository = settingsRepository)

    @Provides
    @Singleton
    fun provideSetDebtQuantityForAppRateDialogShow(settingsRepository: SettingsRepository) =
        SetDebtQuantityForAppRateDialogShow(settingsRepository = settingsRepository)

    @Provides
    @Singleton
    fun provideGetDebtQuantityForAppRateDialogShow(settingsRepository: SettingsRepository) =
        GetDebtQuantityForAppRateDialogShow(settingsRepository = settingsRepository)

    @Provides
    @Singleton
    fun provideSetDebtOrder(settingsRepository: SettingsRepository) =
        SetDebtOrder(settingsRepository = settingsRepository)

    @Provides
    @Singleton
    fun provideGetDebtOrder(settingsRepository: SettingsRepository) =
        GetDebtOrder(settingsRepository = settingsRepository)

    @Provides
    @Singleton
    fun provideSetHumanOrder(settingsRepository: SettingsRepository) =
        SetHumanOrder(settingsRepository = settingsRepository)

    @Provides
    @Singleton
    fun provideGetHumanOrder(settingsRepository: SettingsRepository) =
        GetHumanOrder(settingsRepository = settingsRepository)

    @Provides
    @Singleton
    fun provideSetIsAuthorized(settingsRepository: SettingsRepository) =
        SetIsAuthorized(settingsRepository = settingsRepository)

    @Provides
    @Singleton
    fun provideGetIsAuthorized(settingsRepository: SettingsRepository) =
        GetIsAuthorized(settingsRepository = settingsRepository)

    @Provides
    @Singleton
    fun provideSetUserData(settingsRepository: SettingsRepository) =
        SetUserData(settingsRepository = settingsRepository)

    @Provides
    @Singleton
    fun provideGetUserData(settingsRepository: SettingsRepository) =
        GetUserData(settingsRepository = settingsRepository)

    @Provides
    @Singleton
    fun provideSetLastSyncDate(settingsRepository: SettingsRepository) =
        SetLastSyncDate(settingsRepository = settingsRepository)

    @Provides
    @Singleton
    fun provideGetLastSyncDate(settingsRepository: SettingsRepository) =
        GetLastSyncDate(settingsRepository = settingsRepository)

    @Provides
    @Singleton
    fun provideSetFinanceCurrency(settingsRepository: SettingsRepository) =
        SetFinanceCurrency(settingsRepository = settingsRepository)

    @Provides
    @Singleton
    fun provideGetFinanceCurrency(settingsRepository: SettingsRepository) =
        GetFinanceCurrency(settingsRepository = settingsRepository)

    @Provides
    @Singleton
    fun provideSetPINCodeEnabled(settingsRepository: SettingsRepository) =
        SetPINCodeEnabled(settingsRepository = settingsRepository)

    @Provides
    @Singleton
    fun provideGetPINCodeEnabled(settingsRepository: SettingsRepository) =
        GetPINCodeEnabled(settingsRepository = settingsRepository)

    @Provides
    @Singleton
    fun provideSetPINCode(settingsRepository: SettingsRepository) =
        SetPINCode(settingsRepository = settingsRepository)

    @Provides
    @Singleton
    fun provideGetPINCode(settingsRepository: SettingsRepository) =
        GetPINCode(settingsRepository = settingsRepository)

    @Provides
    @Singleton
    fun provideGetIsFingerprintAuthEnabled(settingsRepository: SettingsRepository) =
        GetIsFingerprintAuthEnabled(settingsRepository = settingsRepository)

    @Provides
    @Singleton
    fun provideSetIsFingerprintAuthEnabled(settingsRepository: SettingsRepository) =
        SetIsFingerprintAuthEnabled(settingsRepository = settingsRepository)

    // ── FINANCE (singleton — shared across ViewModels that read finance data) ─

    @Provides
    @Singleton
    fun provideSetFinance(financeRepository: FinanceRepository) =
        SetFinance(financeRepository = financeRepository)

    @Provides
    @Singleton
    fun provideGetAllFinances(financeRepository: FinanceRepository) =
        GetAllFinances(financeRepository = financeRepository)

    @Provides
    @Singleton
    fun provideGetFinanceByCategoryId(financeRepository: FinanceRepository) =
        GetFinanceByCategoryId(financeRepository = financeRepository)

    @Provides
    @Singleton
    fun provideDeleteFinance(financeRepository: FinanceRepository) =
        DeleteFinance(financeRepository = financeRepository)

    @Provides
    @Singleton
    fun provideUpdateFinance(financeRepository: FinanceRepository) =
        UpdateFinance(financeRepository = financeRepository)

    @Provides
    @Singleton
    fun provideDeleteAllFinancesByCategoryId(financeRepository: FinanceRepository) =
        DeleteAllFinancesByCategoryId(financeRepository = financeRepository)

    @Provides
    @Singleton
    fun provideReplaceAllFinances(financeRepository: FinanceRepository) =
        ReplaceAllFinances(financeRepository = financeRepository)

    // ── FINANCE CATEGORIES ────────────────────────────────────────────────────

    @Provides
    @Singleton
    fun provideGetAllFinanceCategories(financeCategoryRepository: FinanceCategoryRepository) =
        GetAllFinanceCategories(financeCategoryRepository = financeCategoryRepository)

    @Provides
    @Singleton
    fun provideSetFinanceCategory(financeCategoryRepository: FinanceCategoryRepository) =
        SetFinanceCategory(financeCategoryRepository = financeCategoryRepository)

    @Provides
    @Singleton
    fun provideGetAllCategoriesWithFinances(financeCategoryRepository: FinanceCategoryRepository) =
        GetAllCategoriesWithFinances(financeCategoryRepository = financeCategoryRepository)

    @Provides
    @Singleton
    fun provideDeleteFinanceCategory(financeCategoryRepository: FinanceCategoryRepository) =
        DeleteFinanceCategory(financeCategoryRepository = financeCategoryRepository)

    @Provides
    @Singleton
    fun provideGetFinanceCategoriesByState(financeCategoryRepository: FinanceCategoryRepository) =
        GetFinanceCategoriesByState(financeCategoryRepository = financeCategoryRepository)

    @Provides
    @Singleton
    fun provideReplaceAllFinanceCategories(financeCategoryRepository: FinanceCategoryRepository) =
        ReplaceAllFinanceCategories(financeCategoryRepository = financeCategoryRepository)

    // ── ADS ──────────────────────────────────────────────────────────────────

    @Provides
    fun provideSaveClicksUseCase(adRepository: AdRepository) =
        SaveClicksUseCase(adRepository = adRepository)

    @Provides
    fun provideGetClicksUseCase(adRepository: AdRepository) =
        GetClicksUseCase(adRepository = adRepository)

    // ── GOALS ────────────────────────────────────────────────────────────────

    @Provides
    fun provideGetAllGoals(goalRepository: GoalRepository) =
        GetAllGoals(goalRepository = goalRepository)

    @Provides
    fun provideSetGoal(goalRepository: GoalRepository) =
        SetGoal(goalRepository = goalRepository)

    @Provides
    fun provideUpdateGoal(goalRepository: GoalRepository) =
        UpdateGoal(goalRepository = goalRepository)

    @Provides
    fun provideDeleteGoal(goalRepository: GoalRepository) =
        DeleteGoal(goalRepository = goalRepository)

    @Provides
    fun provideReplaceAllGoals(goalRepository: GoalRepository) =
        ReplaceAllGoals(goalRepository = goalRepository)

    // ── GOAL DEPOSITS ─────────────────────────────────────────────────────────

    @Provides
    fun provideGetAllGoalDeposits(goalDepositRepository: GoalDepositRepository) =
        GetAllGoalDeposits(goalDepositRepository = goalDepositRepository)

    @Provides
    fun provideGetGoalDepositsByGoalId(goalDepositRepository: GoalDepositRepository) =
        GetGoalDepositsByGoalId(goalDepositRepository = goalDepositRepository)

    @Provides
    fun provideSetGoalDeposit(goalDepositRepository: GoalDepositRepository) =
        SetGoalDeposit(goalDepositRepository = goalDepositRepository)

    @Provides
    fun provideDeleteGoalDepositsByGoalId(goalDepositRepository: GoalDepositRepository) =
        DeleteGoalDepositsByGoalId(goalDepositRepository = goalDepositRepository)

    @Provides
    fun provideReplaceAllGoalsDeposits(goalDepositRepository: GoalDepositRepository) =
        ReplaceAllGoalsDeposits(goalDepositRepository = goalDepositRepository)

    // ── APP DATA ──────────────────────────────────────────────────────────────

    @Provides
    fun provideReplaceAllAppData(appDataRepository: AppDataRepository) =
        ReplaceAllAppData(appDataRepository = appDataRepository)
}
