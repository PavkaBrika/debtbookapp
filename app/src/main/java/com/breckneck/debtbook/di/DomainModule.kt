package com.breckneck.debtbook.di

import com.breckneck.deptbook.domain.repository.HumanRepository
import com.breckneck.deptbook.domain.usecase.Human.*
import dagger.Module
import dagger.Provides

@Module
class DomainModule {

    @Provides
    fun provideGetAllDebtsSumUseCase(humanRepository: HumanRepository): GetAllDebtsSumUseCase {
        return GetAllDebtsSumUseCase(humanRepository = humanRepository)
    }

    @Provides
    fun provideGetAllHumansUseCase(humanRepository: HumanRepository): GetAllHumansUseCase {
        return GetAllHumansUseCase(humanRepository = humanRepository)
    }

//    @Provides
//    fun provideSetHumanUseCase(humanRepository: HumanRepository): SetHumanUseCase {
//        return SetHumanUseCase(humanRepository = humanRepository)
//    }
//
//    @Provides
//    fun provideGetLastHumanIdUseCase(humanRepository: HumanRepository): GetLastHumanIdUseCase {
//        return GetLastHumanIdUseCase(humanRepository = humanRepository)
//    }
//
//    @Provides
//    fun provideAddSumUseCase(humanRepository: HumanRepository): AddSumUseCase {
//        return AddSumUseCase(humanRepository = humanRepository)
//    }
}