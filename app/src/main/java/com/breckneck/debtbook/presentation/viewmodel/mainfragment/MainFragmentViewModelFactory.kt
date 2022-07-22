package com.breckneck.debtbook.presentation.viewmodel.mainfragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.breckneck.deptbook.domain.usecase.Human.GetAllDebtsSumUseCase
import com.breckneck.deptbook.domain.usecase.Human.GetAllHumansUseCase

class MainFragmentViewModelFactory(
    val getAllHumansUseCase: GetAllHumansUseCase,
    val getAllDebtsSumUseCase: GetAllDebtsSumUseCase
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainFragmentViewModel(
            getAllHumansUseCase = getAllHumansUseCase,
            getAllDebtsSumUseCase = getAllDebtsSumUseCase
        ) as T
    }
}