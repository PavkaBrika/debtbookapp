package com.breckneck.debtbook.di

import com.breckneck.deptbook.data.storage.DebtStorage
import com.breckneck.deptbook.data.storage.HumanStorage
import database.DataBaseDebtStorageImpl
import database.DataBaseHumanStorageImpl
import repository.DebtRepositoryImpl
import repository.HumanRepositoryImpl
import com.breckneck.deptbook.domain.repository.DebtRepository
import com.breckneck.deptbook.domain.repository.HumanRepository
import org.koin.dsl.module

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

}