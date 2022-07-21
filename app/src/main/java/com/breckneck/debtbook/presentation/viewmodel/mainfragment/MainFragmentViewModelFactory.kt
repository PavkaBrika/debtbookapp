package com.breckneck.debtbook.presentation.viewmodel.mainfragment

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.breckneck.deptbook.data.storage.database.DataBaseHumanStorageImpl
import com.breckneck.deptbook.data.storage.repository.HumanRepositoryImpl
import com.breckneck.deptbook.domain.usecase.Human.GetAllDebtsSumUseCase
import com.breckneck.deptbook.domain.usecase.Human.GetAllHumansUseCase

class MainFragmentViewModelFactory(context: Context) : ViewModelProvider.Factory {

    private val dataBaseHumanStorage = DataBaseHumanStorageImpl(context = context)
    private val humanRepository = HumanRepositoryImpl(dataBaseHumanStorage)
    private val getAllDebtsSumUseCase = GetAllDebtsSumUseCase(humanRepository = humanRepository)
    private val getAllHumansUseCase = GetAllHumansUseCase(humanRepository = humanRepository)

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainFragmentViewModel(getAllHumansUseCase = getAllHumansUseCase,
            getAllDebtsSumUseCase = getAllDebtsSumUseCase) as T
    }
}