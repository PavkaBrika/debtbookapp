package com.breckneck.debtbook.di

import android.content.Context
import com.breckneck.debtbook.presentation.viewmodel.mainfragment.MainFragmentViewModelFactory
import com.breckneck.deptbook.domain.usecase.Human.*
import dagger.Module
import dagger.Provides

@Module
class AppModule(val context: Context) {

    @Provides
    fun provideContext(): Context {
        return context
    }

    @Provides
    fun provideMainFragmentViewModelFactory(
        getAllHumansUseCase: GetAllHumansUseCase,
        getAllDebtsSumUseCase: GetAllDebtsSumUseCase
    ): MainFragmentViewModelFactory {
        return MainFragmentViewModelFactory(
            getAllHumansUseCase = getAllHumansUseCase,
            getAllDebtsSumUseCase = getAllDebtsSumUseCase
        )
    }

//    @Provides
//    fun provideSetHuman(setHumanUseCase: SetHumanUseCase): SetHumanUseCase {
//        return setHumanUseCase
//    }
//
//    @Provides
//    fun provideGetLastHumanId(getLastHumanIdUseCase: GetLastHumanIdUseCase): GetLastHumanIdUseCase {
//        return getLastHumanIdUseCase
//    }
//
//    @Provides
//    fun provideAddSum(addSumUseCase: AddSumUseCase): AddSumUseCase {
//        return addSumUseCase
//    }
}