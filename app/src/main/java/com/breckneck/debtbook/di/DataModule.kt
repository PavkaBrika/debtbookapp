package com.breckneck.debtbook.di

import android.content.Context
import com.breckneck.deptbook.data.storage.DebtStorage
import com.breckneck.deptbook.data.storage.HumanStorage
import com.breckneck.deptbook.data.storage.database.DataBaseDebtStorageImpl
import com.breckneck.deptbook.data.storage.database.DataBaseHumanStorageImpl
import com.breckneck.deptbook.data.storage.repository.DebtRepositoryImpl
import com.breckneck.deptbook.data.storage.repository.HumanRepositoryImpl
import com.breckneck.deptbook.domain.repository.DebtRepository
import com.breckneck.deptbook.domain.repository.HumanRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataModule {

    //HUMAN

    @Provides
    @Singleton
    fun provideDataBaseHumanStorage(@ApplicationContext context: Context): HumanStorage {
        return DataBaseHumanStorageImpl(context = context)
    }

    @Provides
    @Singleton
    fun provideHumanRepository(humanStorage: HumanStorage): HumanRepository {
        return HumanRepositoryImpl(humanStorage = humanStorage)
    }

    //DEBT

    @Provides
    @Singleton
    fun provideDataBaseDebtStorage(@ApplicationContext context: Context): DebtStorage {
        return DataBaseDebtStorageImpl(context = context)
    }

    @Provides
    @Singleton
    fun provideDebtRepository(debtStorage: DebtStorage): DebtRepository {
        return DebtRepositoryImpl(debtStorage = debtStorage)
    }
}