package com.breckneck.debtbook.app

import android.app.Application
import com.breckneck.debtbook.di.appModule
import com.breckneck.debtbook.di.dataModule
import com.breckneck.debtbook.di.domainModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class App: Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@App)
            modules(listOf(appModule, domainModule, dataModule))
        }
    }
}