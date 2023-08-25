package com.breckneck.debtbook.di

import com.breckneck.deptbook.data.storage.DebtStorage
import com.breckneck.deptbook.data.storage.HumanStorage
import com.breckneck.deptbook.data.storage.SettingsStorage
import database.DataBaseDebtStorageImpl
import database.DataBaseHumanStorageImpl
import repository.DebtRepositoryImpl
import repository.HumanRepositoryImpl
import com.breckneck.deptbook.domain.repository.DebtRepository
import com.breckneck.deptbook.domain.repository.HumanRepository
import com.breckneck.deptbook.domain.repository.SettingsRepository
import org.koin.dsl.module
import repository.SettingsRepositoryImpl
import sharedprefs.SharedPrefsSettingsStorageImpl

val dataModule = module {

    //HUMAN

    single<HumanStorage> {
        DataBaseHumanStorageImpl(context = get())
    }

    single<HumanRepository> {
        HumanRepositoryImpl(humanStorage = get())
    }

    //DEBT

    single<DebtStorage> {
        DataBaseDebtStorageImpl(context = get())
    }

    single <DebtRepository> {
        DebtRepositoryImpl(debtStorage = get())
    }

    //SETTINGS

    single<SettingsStorage> {
        SharedPrefsSettingsStorageImpl(context = get())
    }

    single<SettingsRepository> {
        SettingsRepositoryImpl(settingsStorage = get())
    }

}