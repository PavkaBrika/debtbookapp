package com.breckneck.debtbook.di

import com.breckneck.debtbook.presentation.viewmodel.SynchronizationFragmentViewModel
import com.breckneck.debtbook.presentation.viewmodel.DebtDetailsViewModel
import com.breckneck.debtbook.presentation.viewmodel.MainActivityViewModel
import com.breckneck.debtbook.presentation.viewmodel.MainFragmentViewModel
import com.breckneck.debtbook.presentation.viewmodel.NewDebtFragmentViewModel
import com.breckneck.debtbook.presentation.viewmodel.SettingsFragmentViewModel
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
            getAppTheme = get())
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

    viewModel<SettingsFragmentViewModel> {
        SettingsFragmentViewModel(
            setFirstMainCurrency = get(),
            getFirstMainCurrency = get(),
            setSecondMainCurrency = get(),
            getSecondMainCurrency = get(),
            setDefaultCurrency = get(),
            getDefaultCurrency = get(),
            setAddSumInShareText = get(),
            getAddSumInShareText = get(),
            getAppTheme = get(),
            setAppTheme = get()
        )
    }

    viewModel<NewDebtFragmentViewModel> {
        NewDebtFragmentViewModel(
            getDefaultCurrency = get(),
            getCurrentDateUseCase = get(),
            setDateUseCase = get()
        )
    }

    viewModel<SynchronizationFragmentViewModel> {
        SynchronizationFragmentViewModel(
            getIsAuthorized = get(),
            setIsAuthorized = get(),
            getAllDebts = get(),
            getAllHumansUseCase = get()
        )
    }
}