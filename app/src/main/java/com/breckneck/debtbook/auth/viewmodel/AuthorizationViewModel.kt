package com.breckneck.debtbook.auth.viewmodel

import android.os.Build
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.breckneck.debtbook.auth.util.CryptoManager
import com.breckneck.deptbook.domain.usecase.Settings.GetIsFingerprintAuthEnabled
import com.breckneck.deptbook.domain.util.PINCodeAction
import com.breckneck.deptbook.domain.util.PINCodeEnterState
import com.breckneck.deptbook.domain.usecase.Settings.GetPINCode
import com.breckneck.deptbook.domain.usecase.Settings.GetPINCodeEnabled
import com.breckneck.deptbook.domain.usecase.Settings.SetPINCode
import com.breckneck.deptbook.domain.usecase.Settings.SetPINCodeEnabled
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.File

class AuthorizationViewModel(
    private val getPINCodeEnabled: GetPINCodeEnabled,
    private val setPINCodeEnabled: SetPINCodeEnabled,
    private val setPINCode: SetPINCode,
    private val getPINCode: GetPINCode,
    private val getIsFingerprintAuthEnabled: GetIsFingerprintAuthEnabled
): ViewModel() {

    val TAG = "AuthorizationVM"

    val cryptoManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        CryptoManager()
    } else {
        null
    }

    private val _enteredPINCode = MutableStateFlow("") // variable for current entered pin by user
    val enteredPINCode: StateFlow<String>
        get() = _enteredPINCode
    private val _pastPINCode = mutableStateOf("") // variable that used for ENABLE action and comparing with entered pin code in CONFIRMATION state
    val pastPINCode: State<String>
        get() = _pastPINCode
    private val _currentPINCode = mutableStateOf(getPINCode.execute()) // variable for existing PIN code in memory
    val currentPINCode: State<String>
        get() = _currentPINCode
    private val _isPINCodeEnabled = MutableLiveData(false)
    val isPINCodeEnabled: LiveData<Boolean>
        get() = _isPINCodeEnabled
    private val _pinCodeEnterState = MutableStateFlow(PINCodeEnterState.FIRST)
    val pinCodeEnterState: StateFlow<PINCodeEnterState>
        get() = _pinCodeEnterState
    private val _pinCodeAction = mutableStateOf(PINCodeAction.CHECK)
    val pinCodeAction: State<PINCodeAction>
        get() = _pinCodeAction
    private val _isFingerprintAuthEnabled = mutableStateOf(false)
    val isFingerprintAuthEnabled: State<Boolean>
        get() = _isFingerprintAuthEnabled

    init {
        Log.e(TAG, "Created")
        getPINCodeEnabled()
        getPINCode()
        getIsFingerprintAuthEnabled()
    }

    fun getPINCode() {
        _currentPINCode.value = getPINCode.execute()
    }

    fun setDecryptedPINCode(code: String) {
        _currentPINCode.value = code
    }

    fun getPINCodeEnabled() {
        _isPINCodeEnabled.value = getPINCodeEnabled.execute()
    }

    fun changeEnteredPINCode(PINCode: String) {
        _enteredPINCode.value = PINCode
    }

    fun setPINCode() {
        setPINCode.execute(PINCode = _enteredPINCode.value)
    }

    fun enablePINCode() {
        setPINCodeEnabled.execute(true)
    }

    fun setPastPINCode() {
        _pastPINCode.value = enteredPINCode.value
        _enteredPINCode.value = ""
    }

    fun resetPINCodes() {
        _enteredPINCode.value = ""
        _pastPINCode.value = ""
    }

    fun turnOffPINCode() {
        setPINCode.execute("")
        setPINCodeEnabled.execute(false)
    }

    fun setPINCodeAction(action: PINCodeAction) {
        _pinCodeAction.value = action
    }

    fun setPINCodeEnterState(state: PINCodeEnterState) {
        _pinCodeEnterState.value = state
    }

    fun getIsFingerprintAuthEnabled() {
        _isFingerprintAuthEnabled.value = getIsFingerprintAuthEnabled.execute()
    }
}