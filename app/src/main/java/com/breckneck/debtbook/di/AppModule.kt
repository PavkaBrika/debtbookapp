package com.breckneck.debtbook.di

import com.breckneck.debtbook.presentation.viewmodel.MainFragmentViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    viewModel<MainFragmentViewModel> {
        MainFragmentViewModel(
            getAllDebtsSumUseCase = get(),
            getAllHumansUseCase = get()
        )
    }
}