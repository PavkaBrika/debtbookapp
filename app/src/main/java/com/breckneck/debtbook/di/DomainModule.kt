package com.breckneck.debtbook.di

import com.breckneck.deptbook.domain.usecase.Ad.SaveClicksUseCase
import com.breckneck.deptbook.domain.usecase.Ad.GetClicksUseCase
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

    factory<UpdateHuman> {
        UpdateHuman(humanRepository = get())
    }

    //DEBT

    factory<GetAllDebtsSumUseCase> {
        GetAllDebtsSumUseCase(humanRepository = get())
    }

    factory<GetAllDebts> {
        GetAllDebts(debtRepository = get())
    }

    factory<GetAllDebtsByIdUseCase> {
        GetAllDebtsByIdUseCase(debtRepository = get())
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

    factory<GetDebtShareString> {
        GetDebtShareString()
    }

    factory<EditDebtUseCase> {
        EditDebtUseCase(debtRepository = get())
    }

    factory<UpdateCurrentSumUseCase> {
        UpdateCurrentSumUseCase()
    }

    factory<GetDebtQuantity> {
        GetDebtQuantity(debtRepository = get())
    }

    factory<FilterDebts> {
        FilterDebts()
    }

    //SETTINGS

    single<SetFirstMainCurrency> {
        SetFirstMainCurrency(settingsRepository = get())
    }

    single<GetFirstMainCurrency> {
        GetFirstMainCurrency(settingsRepository = get())
    }

    single<SetSecondMainCurrency> {
        SetSecondMainCurrency(settingsRepository = get())
    }

    single<GetSecondMainCurrency> {
        GetSecondMainCurrency(settingsRepository = get())
    }

    single<SetDefaultCurrency> {
        SetDefaultCurrency(settingsRepository = get())
    }

    single<GetDefaultCurrency> {
        GetDefaultCurrency(settingsRepository = get())
    }

    single<SetAddSumInShareText> {
        SetAddSumInShareText(settingsRepository = get())
    }

    single<GetAddSumInShareText> {
        GetAddSumInShareText(settingsRepository = get())
    }

    single<SetAppTheme> {
        SetAppTheme(settingsRepository = get())
    }

    single<GetAppTheme> {
        GetAppTheme(settingsRepository = get())
    }

    single<SetDebtQuantityForAppRateDialogShow> {
        SetDebtQuantityForAppRateDialogShow(settingsRepository = get())
    }

    single<GetDebtQuantityForAppRateDialogShow> {
        GetDebtQuantityForAppRateDialogShow(settingsRepository = get())
    }

    single<SetDebtOrder> {
        SetDebtOrder(settingsRepository = get())
    }

    single<GetDebtOrder> {
        GetDebtOrder(settingsRepository = get())
    }

    single<SetHumanOrder> {
        SetHumanOrder(settingsRepository = get())
    }

    single<GetHumanOrder> {
        GetHumanOrder(settingsRepository = get())
    }

    single<SetIsAuthorized> {
        SetIsAuthorized(settingsRepository = get())
    }

    single<GetIsAuthorized> {
        GetIsAuthorized(settingsRepository = get())
    }

    //ADS

    factory<SaveClicksUseCase> {
        SaveClicksUseCase(adRepository = get())
    }

    factory<GetClicksUseCase> {
        GetClicksUseCase(adRepository = get())
    }
}