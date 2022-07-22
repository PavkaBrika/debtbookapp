package com.breckneck.debtbook.di

import com.breckneck.deptbook.data.storage.DebtStorage
import com.breckneck.deptbook.data.storage.HumanStorage
import com.breckneck.deptbook.data.storage.database.DataBaseDebtStorageImpl
import com.breckneck.deptbook.data.storage.database.DataBaseHumanStorageImpl
import com.breckneck.deptbook.data.storage.repository.DebtRepositoryImpl
import com.breckneck.deptbook.data.storage.repository.HumanRepositoryImpl
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