package com.breckneck.debtbook.di

import com.breckneck.deptbook.domain.repository.DebtRepository
import com.breckneck.deptbook.domain.repository.HumanRepository
import com.breckneck.deptbook.domain.usecase.Debt.*
import com.breckneck.deptbook.domain.usecase.Human.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent

@Module
@InstallIn(FragmentComponent::class)
class DomainModule {

    //HUMAN

    @Provides
    fun provideSetHumanUseCase(humanRepository: HumanRepository): SetHumanUseCase {
        return SetHumanUseCase(humanRepository = humanRepository)
    }

    @Provides
    fun provideGetLastHumanIdUseCase(humanRepository: HumanRepository): GetLastHumanIdUseCase {
        return GetLastHumanIdUseCase(humanRepository = humanRepository)
    }

    @Provides
    fun provideAddSumUseCase(humanRepository: HumanRepository): AddSumUseCase {
        return AddSumUseCase(humanRepository = humanRepository)
    }

    @Provides
    fun provideGetHumanSumDebt(humanRepository: HumanRepository): GetHumanSumDebtUseCase {
        return GetHumanSumDebtUseCase(humanRepository = humanRepository)
    }

    @Provides
    fun provideDeleteHuman(humanRepository: HumanRepository): DeleteHumanUseCase {
        return DeleteHumanUseCase(humanRepository = humanRepository)
    }

    //DEBT

    @Provides
    fun provideSetDebtUseCase(debtRepository: DebtRepository): SetDebtUseCase {
        return SetDebtUseCase(debtRepository = debtRepository)
    }

    @Provides
    fun provideEditDebtUseCase(debtRepository: DebtRepository): EditDebtUseCase {
        return EditDebtUseCase(debtRepository = debtRepository)
    }

    @Provides
    fun provideGetAllDebts(debtRepository: DebtRepository): GetAllDebtsUseCase {
        return GetAllDebtsUseCase(debtRepository = debtRepository)
    }

    @Provides
    fun provideDeleteDebt(debtRepository: DebtRepository): DeleteDebtUseCase {
        return DeleteDebtUseCase(debtRepository = debtRepository)
    }

    @Provides
    fun provideDeleteDebtsByHumanId(debtRepository: DebtRepository): DeleteDebtsByHumanIdUseCase {
        return DeleteDebtsByHumanIdUseCase(debtRepository = debtRepository)
    }
}