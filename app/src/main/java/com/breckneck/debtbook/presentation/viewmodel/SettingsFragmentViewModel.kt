package com.breckneck.debtbook.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.breckneck.deptbook.domain.usecase.Settings.GetAddSumInShareText
import com.breckneck.deptbook.domain.usecase.Settings.GetAppTheme
import com.breckneck.deptbook.domain.usecase.Settings.GetDefaultCurrency
import com.breckneck.deptbook.domain.usecase.Settings.GetFirstMainCurrency
import com.breckneck.deptbook.domain.usecase.Settings.GetSecondMainCurrency
import com.breckneck.deptbook.domain.usecase.Settings.SetAddSumInShareText
import com.breckneck.deptbook.domain.usecase.Settings.SetAppTheme
import com.breckneck.deptbook.domain.usecase.Settings.SetDefaultCurrency
import com.breckneck.deptbook.domain.usecase.Settings.SetFirstMainCurrency
import com.breckneck.deptbook.domain.usecase.Settings.SetSecondMainCurrency
import org.koin.android.ext.android.inject

class SettingsFragmentViewModel(
    private val setFirstMainCurrency: SetFirstMainCurrency,
    private val getFirstMainCurrency: GetFirstMainCurrency,
    private val setSecondMainCurrency: SetSecondMainCurrency,
    private val getSecondMainCurrency: GetSecondMainCurrency,
    private val setDefaultCurrency: SetDefaultCurrency,
    private val getDefaultCurrency: GetDefaultCurrency,
    private val setAddSumInShareText: SetAddSumInShareText,
    private val getAddSumInShareText: GetAddSumInShareText,
    private val getAppTheme: GetAppTheme,
    private val setAppTheme: SetAppTheme
) : ViewModel() {

    private val TAG = "SettingsFragmentViewModel"

    private val _firstMainCurrency = MutableLiveData<String>()
    val firstMainCurrency: LiveData<String>
        get() = _firstMainCurrency
    private val _secondMainCurrency = MutableLiveData<String>()
    val secondMainCurrency: LiveData<String>
        get() = _secondMainCurrency
    private val _defaultCurrency = MutableLiveData<String>()
    val defaultCurrency: LiveData<String>
        get() = _defaultCurrency
    private val _addSumInShareText = MutableLiveData<Boolean>()
    val addSumInShareText: LiveData<Boolean>
        get() = _addSumInShareText
    private val _appTheme = MutableLiveData<String>()
    val appTheme: LiveData<String>
        get() = _appTheme


    init {
        getFirstMainCurrency()
        getSecondMainCurrency()
        getDefaultCurrency()
        getAppTheme()
        getSumInShareText()
    }

    private fun getFirstMainCurrency() {
        _firstMainCurrency.value = getFirstMainCurrency.execute()
    }

    fun setFirstMainCurrency(currency: String) {
        setFirstMainCurrency.execute(currency = currency)
        _firstMainCurrency.value = currency
    }

    private fun getSecondMainCurrency() {
        _secondMainCurrency.value = getSecondMainCurrency.execute()
    }

    fun setSecondMainCurrency(currency: String) {
        setSecondMainCurrency.execute(currency = currency)
        _secondMainCurrency.value = currency
    }

    private fun getDefaultCurrency() {
        _defaultCurrency.value = getDefaultCurrency.execute()
    }

    fun setDefaultCurrency(currency: String) {
        setDefaultCurrency.execute(currency = currency)
        _defaultCurrency.value = currency
    }

    private fun getAppTheme() {
        _appTheme.value = getAppTheme.execute()
    }

    fun setAppTheme(theme: String) {
        setAppTheme.execute(theme = theme)
        _appTheme.value = theme
    }

    private fun getSumInShareText() {
        _addSumInShareText.value = getAddSumInShareText.execute()
    }

    fun setSumInShareText(value: Boolean) {
        setAddSumInShareText.execute(value)
        _addSumInShareText.value = value
    }

}