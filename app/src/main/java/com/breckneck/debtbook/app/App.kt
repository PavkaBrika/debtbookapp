package com.breckneck.debtbook.app

import android.app.Application
import android.util.Log
import com.breckneck.debtbook.di.appModule
import com.breckneck.debtbook.di.dataModule
import com.breckneck.debtbook.di.domainModule
import com.yandex.mobile.ads.common.InitializationListener
import com.yandex.mobile.ads.common.MobileAds
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class App: Application() {

    override fun onCreate() {
        super.onCreate()

        MobileAds.initialize(this, object: InitializationListener {
            override fun onInitializationCompleted() {
                Log.e("TAG", "Yandex initialized")
            }
        })

        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@App)
            modules(listOf(appModule, domainModule, dataModule))
        }
    }
}