package com.breckneck.debtbook.di

import com.breckneck.deptbook.domain.usecase.Debt.*
import com.breckneck.deptbook.domain.usecase.Human.*
import org.koin.dsl.module


val domainModule = module {

    //HUMAN

    factory<GetAllDebtsSumUseCase> {
        GetAllDebtsSumUseCase(humanRepository = get())
    }

    factory<GetAllHumansUseCase> {
        GetAllHumansUseCase(humanRepository = get())
    }

    factory<GetLastHumanIdUseCase> {
        GetLastHumanIdUseCase(humanRepository = get())
    }

    factory<AddSumUseCase> {
        AddSumUseCase(humanRepository = get())
    }

    factory<GetHumanSumDebtUseCase> {
        GetHumanSumDebtUseCase(humanRepository = get())
    }

    factory<DeleteHumanUseCase> {
        DeleteHumanUseCase(humanRepository = get())
    }

    factory<SetHumanUseCase> {
        SetHumanUseCase(humanRepository = get())
    }

    //DEBT

    factory<GetAllDebtsUseCase> {
        GetAllDebtsUseCase(debtRepository = get())
    }

    factory<DeleteDebtUseCase> {
        DeleteDebtUseCase(debtRepository = get())
    }

    factory<DeleteDebtsByHumanIdUseCase> {
        DeleteDebtsByHumanIdUseCase(debtRepository = get())
    }

    factory<SetDebtUseCase> {
        SetDebtUseCase(debtRepository = get())
    }

    factory<GetCurrentDateUseCase> {
        GetCurrentDateUseCase()
    }

    factory<SetDateUseCase> {
        SetDateUseCase()
    }

    factory<CheckEditTextIsEmpty> {
        CheckEditTextIsEmpty()
    }

    factory<EditDebtUseCase> {
        EditDebtUseCase(debtRepository = get())
    }

    factory<UpdateCurrentSumUseCase> {
        UpdateCurrentSumUseCase()
    }

}