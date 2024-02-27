package com.breckneck.debtbook.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.breckneck.debtbook.adapter.SettingsAdapter
import com.breckneck.deptbook.domain.usecase.Settings.GetAddSumInShareText
import com.breckneck.deptbook.domain.usecase.Settings.GetAppTheme
import com.breckneck.deptbook.domain.usecase.Settings.GetDefaultCurrency
import com.breckneck.deptbook.domain.usecase.Settings.GetFirstMainCurrency
import com.breckneck.deptbook.domain.usecase.Settings.GetIsAuthorized
import com.breckneck.deptbook.domain.usecase.Settings.GetSecondMainCurrency
import com.breckneck.deptbook.domain.usecase.Settings.GetUserData
import com.breckneck.deptbook.domain.usecase.Settings.SetAddSumInShareText
import com.breckneck.deptbook.domain.usecase.Settings.SetAppTheme
import com.breckneck.deptbook.domain.usecase.Settings.SetDefaultCurrency
import com.breckneck.deptbook.domain.usecase.Settings.SetFirstMainCurrency
import com.breckneck.deptbook.domain.usecase.Settings.SetSecondMainCurrency
import com.google.android.gms.common.GoogleApiAvailability
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
    private val setAppTheme: SetAppTheme,
    private val getIsAuthorized: GetIsAuthorized,
    private val getUserData: GetUserData
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
    private val _isSettingsDialogOpened = MutableLiveData<Boolean>()
    val isSettingsDialogOpened: LiveData<Boolean>
        get() = _isSettingsDialogOpened
    private val _settingsDialogTitle = MutableLiveData<String>()
    val settingsDialogTitle: LiveData<String>
        get() = _settingsDialogTitle
    private val _settingsList = MutableLiveData<List<String>>()
    val settingsList: LiveData<List<String>>
        get() = _settingsList
    private val _selectedSetting = MutableLiveData<Int>()
    val selectedSetting: LiveData<Int>
        get() = _selectedSetting
    private val _onSettingsClickListener = MutableLiveData<SettingsAdapter.OnClickListener>()
    val onSettingsClickListener: LiveData<SettingsAdapter.OnClickListener>
        get() = _onSettingsClickListener
    private val _isSynchronizationAvailable = MutableLiveData<Boolean>(null)
    val isSynchronizationAvailable: LiveData<Boolean>
        get() = _isSynchronizationAvailable
    private val _isAuthorized = MutableLiveData<Boolean>(false)
    val isAuthorized: LiveData<Boolean>
        get() = _isAuthorized
    private val _userName = MutableLiveData<String>()
    val userName: LiveData<String>
        get() = _userName
    private val _emailAddress = MutableLiveData<String>()
    val emailAddress: LiveData<String>
        get() = _emailAddress

    init {
        getFirstMainCurrency()
        getSecondMainCurrency()
        getDefaultCurrency()
        getAppTheme()
        getSumInShareText()
        getIsAuthorized()
    }

    private fun getIsAuthorized() {
        _isAuthorized.value = getIsAuthorized.execute()
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

    fun onSettingsDialogOpen(
        settingsTitle: String,
        settingsList: List<String>,
        selectedSetting: Int,
        onSettingsClickListener: SettingsAdapter.OnClickListener
    ) {
        _isSettingsDialogOpened.value = true
        _settingsDialogTitle.value = settingsTitle
        _settingsList.value = settingsList
        _selectedSetting.value = selectedSetting
        _onSettingsClickListener.value = onSettingsClickListener
    }

    fun onDialogClose() {
        _isSettingsDialogOpened.value = false
    }

    fun setIsSynchronizationAvailable(available: Boolean) {
        _isSynchronizationAvailable.value = available
    }

    fun getUserData() {
        val userData = getUserData.execute()
        _userName.value = userData.name
        _emailAddress.value = userData.email
    }

}