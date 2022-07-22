package com.breckneck.debtbook.app

import android.app.Application
import com.breckneck.debtbook.di.AppComponent
import com.breckneck.debtbook.di.AppModule
import com.breckneck.debtbook.di.DaggerAppComponent

class App : Application() {

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerAppComponent
            .builder()
            .appModule(AppModule(context = this))
            .build()
    }
}