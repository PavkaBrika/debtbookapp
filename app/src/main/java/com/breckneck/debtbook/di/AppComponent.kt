package com.breckneck.debtbook.di

import com.breckneck.debtbook.presentation.fragment.MainFragment
import com.breckneck.debtbook.presentation.fragment.NewDebtFragment
import dagger.Component

@Component(modules = [AppModule::class, DomainModule::class, DataModule::class])
interface AppComponent {

    fun inject(mainFragment: MainFragment)

//    fun inject(newDebtFragment: NewDebtFragment)
}