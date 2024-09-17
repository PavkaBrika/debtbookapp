package com.breckneck.debtbook.di

import com.breckneck.debtbook.auth.viewmodel.AuthorizationViewModel
import com.breckneck.debtbook.finance.viewmodel.CreateFinanceCategoryViewModel
import com.breckneck.debtbook.finance.viewmodel.CreateFinanceViewModel
import com.breckneck.debtbook.goal.viewmodel.CreateGoalsFragmentViewModel
import com.breckneck.debtbook.settings.viewmodel.SynchronizationViewModel
import com.breckneck.debtbook.debt.viewmodel.DebtDetailsViewModel
import com.breckneck.debtbook.finance.viewmodel.FinanceDetailsViewModel
import com.breckneck.debtbook.finance.viewmodel.FinanceViewModel
import com.breckneck.debtbook.goal.viewmodel.GoalDetailsFragmentViewModel
import com.breckneck.debtbook.goal.viewmodel.GoalsFragmentViewModel
import com.breckneck.debtbook.core.viewmodel.MainActivityViewModel
import com.breckneck.debtbook.debt.viewmodel.MainFragmentViewModel
import com.breckneck.debtbook.debt.viewmodel.NewDebtViewModel
import com.breckneck.debtbook.settings.viewmodel.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    viewModel<MainFragmentViewModel> {
        MainFragmentViewModel(
            getAllDebtsSumUseCase = get(),
            getAllHumansUseCase = get(),
            getPositiveHumansUseCase = get(),
            getNegativeHumansUseCase = get(),
            getFirstMainCurrency = get(),
            getSecondMainCurrency = get(),
            getHumanOrder = get(),
            setHumanOrder = get(),
            updateHuman = get()
        )
    }

    viewModel<MainActivityViewModel> {
        MainActivityViewModel(
            getDebtQuantity = get(),
            getDebtQuantityForAppRateDialogShow = get(),
            setDebtQuantityForAppRateDialogShow = get(),
            getClicksUseCase = get(),
            saveClicks = get(),
            getAppTheme = get(),
            setPINCodeEnabled = get(),
            getPINCodeEnabled = get(),
            setIsFingerprintAuthEnabled = get()
        )
    }

    viewModel<DebtDetailsViewModel> {
        DebtDetailsViewModel(
            getAllDebtsByIdUseCase = get(),
            getLastHumanIdUseCase = get(),
            getHumanSumDebtUseCase = get(),
            deleteHumanUseCase = get(),
            deleteDebtsByHumanIdUseCase = get(),
            deleteDebtUseCase = get(),
            addSumUseCase = get(),
            getDebtOrder = get(),
            setDebtOrder = get(),
            filterDebts = get()
        )
    }

    viewModel<SettingsViewModel> {
        SettingsViewModel(
            setFirstMainCurrency = get(),
            getFirstMainCurrency = get(),
            setSecondMainCurrency = get(),
            getSecondMainCurrency = get(),
            setDefaultCurrency = get(),
            getDefaultCurrency = get(),
            setAddSumInShareText = get(),
            getAddSumInShareText = get(),
            getAppTheme = get(),
            setAppTheme = get(),
            getIsAuthorized = get(),
            getUserData = get(),
            setIsFingerprintAuthEnabled = get(),
            getIsFingerprintAuthEnabled = get()
        )
    }

    viewModel<NewDebtViewModel> {
        NewDebtViewModel(
            getDefaultCurrency = get(),
            getCurrentDateUseCase = get(),
            setDateUseCase = get()
        )
    }

    viewModel<SynchronizationViewModel> {
        SynchronizationViewModel(
            getIsAuthorized = get(),
            setIsAuthorized = get(),
            getAllDebts = get(),
            getAllHumansUseCase = get(),
            replaceAllDebts = get(),
            replaceAllHumans = get(),
            setUserData = get(),
            setDateUseCase = get(),
            setLastSyncDate = get(),
            getLastSyncDate = get(),
            getAllFinances = get(),
            getAllFinanceCategories = get(),
            replaceAllFinances = get(),
            replaceAllFinanceCategories = get(),
            getAllGoals = get(),
            getAllGoalDeposits = get(),
            replaceAllGoals = get(),
            replaceAllGoalDeposits = get()
        )
    }

    viewModel<FinanceViewModel> {
        FinanceViewModel(
            getFinanceCurrency = get(),
            setFinanceCurrency = get(),
            getAllCategoriesWithFinances = get()
        )
    }

    viewModel<CreateFinanceViewModel> {
        CreateFinanceViewModel(
            setFinance = get(),
            getAllFinanceCategories = get(),
            getFinanceCurrency = get(),
            updateFinance = get(),
            deleteFinanceCategoryUseCase = get(),
            getFinanceCategoriesByState = get(),
            deleteAllFinancesByCategoryId = get()
        )
    }

    viewModel<CreateFinanceCategoryViewModel> {
        CreateFinanceCategoryViewModel(setFinanceCategory = get())
    }

    viewModel<FinanceDetailsViewModel> {
        FinanceDetailsViewModel(
            getFinanceByCategoryId = get(),
            deleteFinance = get()
        )
    }

    viewModel<GoalsFragmentViewModel> {
        GoalsFragmentViewModel(
            getAllGoals = get(),
            updateGoal = get(),
            deleteGoal = get(),
            setGoalDeposit = get(),
            deleteGoalDepositsByGoalId = get()
        )
    }

    viewModel<CreateGoalsFragmentViewModel> {
        CreateGoalsFragmentViewModel(
            setGoal = get(),
            getDefaultCurrency = get(),
            updateGoal = get()
        )
    }

    viewModel<GoalDetailsFragmentViewModel> {
        GoalDetailsFragmentViewModel(
            updateGoal = get(),
            setGoalDeposit = get(),
            getGoalDepositsByGoalId = get(),
            deleteGoalDepositsByGoalId = get(),
            deleteGoal = get()
        )
    }

    viewModel<AuthorizationViewModel> {
        AuthorizationViewModel(
            getPINCodeEnabled = get(),
            setPINCodeEnabled = get(),
            setPINCode = get(),
            getPINCode = get(),
            getIsFingerprintAuthEnabled = get()
        )
    }
}