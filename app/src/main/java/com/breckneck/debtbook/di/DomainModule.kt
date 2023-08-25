package com.breckneck.debtbook.di

import com.breckneck.deptbook.domain.usecase.Debt.*
import com.breckneck.deptbook.domain.usecase.Human.*
import com.breckneck.deptbook.domain.usecase.Settings.*
import org.koin.dsl.module


val domainModule = module {

    //HUMAN

    factory<GetAllHumansUseCase> {
        GetAllHumansUseCase(humanRepository = get())
    }

    factory<GetPositiveHumansUseCase> {
        GetPositiveHumansUseCase(humanRepository = get())
    }

    factory<GetNegativeHumansUseCase> {
        GetNegativeHumansUseCase(humanRepository = get())
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

    factory<GetAllDebtsSumUseCase> {
        GetAllDebtsSumUseCase(humanRepository = get())
    }

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

    factory<GetDebtShareString> {
        GetDebtShareString()
    }

    factory<EditDebtUseCase> {
        EditDebtUseCase(debtRepository = get())
    }

    factory<UpdateCurrentSumUseCase> {
        UpdateCurrentSumUseCase()
    }

    //SETTINGS

    factory<SetFirstMainCurrency> {
        SetFirstMainCurrency(settingsRepository = get())
    }

    factory<GetFirstMainCurrency> {
        GetFirstMainCurrency(settingsRepository = get())
    }

    factory<SetSecondMainCurrency> {
        SetSecondMainCurrency(settingsRepository = get())
    }

    factory<GetSecondMainCurrency> {
        GetSecondMainCurrency(settingsRepository = get())
    }

    factory<SetDefaultCurrency> {
        SetDefaultCurrency(settingsRepository = get())
    }

    factory<GetDefaultCurrency> {
        GetDefaultCurrency(settingsRepository = get())
    }

    factory<SetAddSumInShareText> {
        SetAddSumInShareText(settingsRepository = get())
    }

    factory<GetAddSumInShareText> {
        GetAddSumInShareText(settingsRepository = get())
    }
}