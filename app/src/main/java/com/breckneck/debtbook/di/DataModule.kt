package com.breckneck.debtbook.di

import android.content.Context
import com.breckneck.deptbook.data.storage.AdStorage
import com.breckneck.deptbook.data.storage.DebtStorage
import com.breckneck.deptbook.data.storage.FinanceCategoryStorage
import com.breckneck.deptbook.data.storage.FinanceStorage
import com.breckneck.deptbook.data.storage.GoalDepositStorage
import com.breckneck.deptbook.data.storage.GoalStorage
import com.breckneck.deptbook.data.storage.HumanStorage
import com.breckneck.deptbook.data.storage.SettingsStorage
import com.breckneck.deptbook.domain.repository.AdRepository
import com.breckneck.deptbook.domain.repository.AppDataRepository
import com.breckneck.deptbook.domain.repository.DebtRepository
import com.breckneck.deptbook.domain.repository.FinanceCategoryRepository
import com.breckneck.deptbook.domain.repository.FinanceRepository
import com.breckneck.deptbook.domain.repository.GoalDepositRepository
import com.breckneck.deptbook.domain.repository.GoalRepository
import com.breckneck.deptbook.domain.repository.HumanRepository
import com.breckneck.deptbook.domain.repository.SettingsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import database.DataBaseDebtStorageImpl
import database.DataBaseFinanceCategoryStorageImpl
import database.DataBaseFinanceStorageImpl
import database.DataBaseGoalDepositStorageImpl
import database.DataBaseGoalStorageImpl
import database.DataBaseHumanStorageImpl
import repository.AdRepositoryImpl
import repository.AppDataRepositoryImpl
import repository.DebtRepositoryImpl
import repository.FinanceCategoryRepositoryImpl
import repository.FinanceRepositoryImpl
import repository.GoalDepositRepositoryImpl
import repository.GoalRepositoryImpl
import repository.HumanRepositoryImpl
import repository.SettingsRepositoryImpl
import sharedprefs.SharedPrefsAdStorageImpl
import sharedprefs.SharedPrefsSettingsStorageImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    // HUMAN

    @Provides
    @Singleton
    fun provideHumanStorage(@ApplicationContext context: Context): HumanStorage =
        DataBaseHumanStorageImpl(context = context)

    @Provides
    @Singleton
    fun provideHumanRepository(humanStorage: HumanStorage): HumanRepository =
        HumanRepositoryImpl(humanStorage = humanStorage)

    // DEBT

    @Provides
    @Singleton
    fun provideDebtStorage(@ApplicationContext context: Context): DebtStorage =
        DataBaseDebtStorageImpl(context = context)

    @Provides
    @Singleton
    fun provideDebtRepository(debtStorage: DebtStorage): DebtRepository =
        DebtRepositoryImpl(debtStorage = debtStorage)

    // SETTINGS

    @Provides
    @Singleton
    fun provideSettingsStorage(@ApplicationContext context: Context): SettingsStorage =
        SharedPrefsSettingsStorageImpl(context = context)

    @Provides
    @Singleton
    fun provideSettingsRepository(settingsStorage: SettingsStorage): SettingsRepository =
        SettingsRepositoryImpl(settingsStorage = settingsStorage)

    // FINANCE

    @Provides
    @Singleton
    fun provideFinanceStorage(@ApplicationContext context: Context): FinanceStorage =
        DataBaseFinanceStorageImpl(context = context)

    @Provides
    @Singleton
    fun provideFinanceRepository(financeStorage: FinanceStorage): FinanceRepository =
        FinanceRepositoryImpl(financeStorage = financeStorage)

    // FINANCE CATEGORY

    @Provides
    @Singleton
    fun provideFinanceCategoryStorage(@ApplicationContext context: Context): FinanceCategoryStorage =
        DataBaseFinanceCategoryStorageImpl(context = context)

    @Provides
    @Singleton
    fun provideFinanceCategoryRepository(financeCategoryStorage: FinanceCategoryStorage): FinanceCategoryRepository =
        FinanceCategoryRepositoryImpl(financeCategoryStorage = financeCategoryStorage)

    // ADS

    @Provides
    @Singleton
    fun provideAdStorage(@ApplicationContext context: Context): AdStorage =
        SharedPrefsAdStorageImpl(context = context)

    @Provides
    @Singleton
    fun provideAdRepository(adStorage: AdStorage): AdRepository =
        AdRepositoryImpl(adStorage = adStorage)

    // GOALS

    @Provides
    @Singleton
    fun provideGoalStorage(@ApplicationContext context: Context): GoalStorage =
        DataBaseGoalStorageImpl(context = context)

    @Provides
    @Singleton
    fun provideGoalRepository(goalStorage: GoalStorage): GoalRepository =
        GoalRepositoryImpl(goalStorage = goalStorage)

    // GOAL DEPOSITS

    @Provides
    @Singleton
    fun provideGoalDepositStorage(@ApplicationContext context: Context): GoalDepositStorage =
        DataBaseGoalDepositStorageImpl(context = context)

    @Provides
    @Singleton
    fun provideGoalDepositRepository(goalDepositStorage: GoalDepositStorage): GoalDepositRepository =
        GoalDepositRepositoryImpl(goalDepositStorage = goalDepositStorage)

    // APP DATA (atomic cross-table restore)

    @Provides
    @Singleton
    fun provideAppDataRepository(@ApplicationContext context: Context): AppDataRepository =
        AppDataRepositoryImpl(context = context)
}
