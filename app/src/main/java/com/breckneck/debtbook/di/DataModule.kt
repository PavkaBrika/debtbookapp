package com.breckneck.debtbook.di

import android.content.Context
import com.breckneck.deptbook.data.storage.HumanStorage
import com.breckneck.deptbook.data.storage.database.DataBaseHumanStorageImpl
import com.breckneck.deptbook.data.storage.repository.HumanRepositoryImpl
import com.breckneck.deptbook.domain.repository.HumanRepository
import com.breckneck.deptbook.domain.usecase.Human.GetAllDebtsSumUseCase
import com.breckneck.deptbook.domain.usecase.Human.GetAllHumansUseCase
import dagger.Module
import dagger.Provides

@Module
class DataModule {

    @Provides
    fun provideHumanStorage(context: Context): HumanStorage {
        return DataBaseHumanStorageImpl(context = context)
    }

    @Provides
    fun provideHumanRepository(humanStorage: HumanStorage): HumanRepository {
        return HumanRepositoryImpl(humanStorage = humanStorage)
    }
}