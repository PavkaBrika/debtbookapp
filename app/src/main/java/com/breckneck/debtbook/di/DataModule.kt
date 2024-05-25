package com.breckneck.debtbook.di

import com.breckneck.deptbook.data.storage.AdStorage
import com.breckneck.deptbook.data.storage.DebtStorage
import com.breckneck.deptbook.data.storage.FinanceCategoryStorage
import com.breckneck.deptbook.data.storage.FinanceStorage
import com.breckneck.deptbook.data.storage.GoalStorage
import com.breckneck.deptbook.data.storage.HumanStorage
import com.breckneck.deptbook.data.storage.SettingsStorage
import com.breckneck.deptbook.domain.repository.AdRepository
import database.DataBaseDebtStorageImpl
import database.DataBaseHumanStorageImpl
import repository.DebtRepositoryImpl
import repository.HumanRepositoryImpl
import com.breckneck.deptbook.domain.repository.DebtRepository
import com.breckneck.deptbook.domain.repository.FinanceCategoryRepository
import com.breckneck.deptbook.domain.repository.FinanceRepository
import com.breckneck.deptbook.domain.repository.GoalRepository
import com.breckneck.deptbook.domain.repository.HumanRepository
import com.breckneck.deptbook.domain.repository.SettingsRepository
import database.DataBaseFinanceCategoryStorageImpl
import database.DataBaseFinanceStorageImpl
import database.DataBaseGoalStorageImpl
import org.koin.dsl.module
import repository.AdRepositoryImpl
import repository.FinanceCategoryRepositoryImpl
import repository.FinanceRepositoryImpl
import repository.GoalRepositoryImpl
import repository.SettingsRepositoryImpl
import sharedprefs.SharedPrefsAdStorageImpl
import sharedprefs.SharedPrefsSettingsStorageImpl

val dataModule = module {

    //HUMAN

    factory<HumanStorage> {
        DataBaseHumanStorageImpl(context = get())
    }

    factory<HumanRepository> {
        HumanRepositoryImpl(humanStorage = get())
    }

    //DEBT

    factory<DebtStorage> {
        DataBaseDebtStorageImpl(context = get())
    }

    factory<DebtRepository> {
        DebtRepositoryImpl(debtStorage = get())
    }

    //SETTINGS

    single<SettingsStorage> {
        SharedPrefsSettingsStorageImpl(context = get())
    }

    single<SettingsRepository> {
        SettingsRepositoryImpl(settingsStorage = get())
    }

    //FINANCE

    factory<FinanceStorage> {
        DataBaseFinanceStorageImpl(context = get())
    }

    factory<FinanceRepository> {
        FinanceRepositoryImpl(financeStorage = get())
    }

    //FINANCE CATEGORY

    factory<FinanceCategoryStorage> {
        DataBaseFinanceCategoryStorageImpl(context = get())
    }

    factory<FinanceCategoryRepository> {
        FinanceCategoryRepositoryImpl(financeCategoryStorage = get())
    }

    //ADS

    factory<AdStorage> {
        SharedPrefsAdStorageImpl(context = get())
    }

    factory<AdRepository> {
        AdRepositoryImpl(adStorage = get())
    }

    //GOALS

    factory<GoalStorage> {
        DataBaseGoalStorageImpl(context = get())
    }

    factory<GoalRepository> {
        GoalRepositoryImpl(goalStorage = get())
    }
}