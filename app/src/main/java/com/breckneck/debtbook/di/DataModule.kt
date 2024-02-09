package com.breckneck.debtbook.di

import com.breckneck.deptbook.data.storage.AdStorage
import com.breckneck.deptbook.data.storage.DebtStorage
import com.breckneck.deptbook.data.storage.HumanStorage
import com.breckneck.deptbook.data.storage.SettingsStorage
import com.breckneck.deptbook.domain.repository.AdRepository
import database.DataBaseDebtStorageImpl
import database.DataBaseHumanStorageImpl
import repository.DebtRepositoryImpl
import repository.HumanRepositoryImpl
import com.breckneck.deptbook.domain.repository.DebtRepository
import com.breckneck.deptbook.domain.repository.HumanRepository
import com.breckneck.deptbook.domain.repository.SettingsRepository
import org.koin.dsl.module
import repository.AdRepositoryImpl
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

    //ADS

    factory<AdStorage> {
        SharedPrefsAdStorageImpl(context = get())
    }

    factory<AdRepository> {
        AdRepositoryImpl(adStorage = get())
    }

}