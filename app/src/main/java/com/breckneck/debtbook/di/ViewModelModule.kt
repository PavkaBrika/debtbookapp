package com.breckneck.debtbook.di

import com.breckneck.deptbook.domain.repository.HumanRepository
import com.breckneck.deptbook.domain.usecase.Human.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent


@Module
@InstallIn(ViewModelComponent::class)
class ViewModelModule {

    @Provides
    fun provideGetAllDebtsSumUseCase(humanRepository: HumanRepository): GetAllDebtsSumUseCase {
        return GetAllDebtsSumUseCase(humanRepository = humanRepository)
    }

    @Provides
    fun provideGetAllHumansUseCase(humanRepository: HumanRepository): GetAllHumansUseCase {
        return GetAllHumansUseCase(humanRepository = humanRepository)
    }


}