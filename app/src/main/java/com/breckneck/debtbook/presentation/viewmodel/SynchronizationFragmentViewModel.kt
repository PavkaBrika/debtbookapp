package com.breckneck.debtbook.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.breckneck.deptbook.domain.model.AppDataLists
import com.breckneck.deptbook.domain.model.HumanDomain
import com.breckneck.deptbook.domain.usecase.Debt.GetAllDebts
import com.breckneck.deptbook.domain.usecase.Debt.GetAllDebtsByIdUseCase
import com.breckneck.deptbook.domain.usecase.Human.GetAllHumansUseCase
import com.breckneck.deptbook.domain.usecase.Settings.GetIsAuthorized
import com.breckneck.deptbook.domain.usecase.Settings.SetIsAuthorized
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class SynchronizationFragmentViewModel(
    private val getIsAuthorized: GetIsAuthorized,
    private val setIsAuthorized: SetIsAuthorized,
    private val getAllDebts: GetAllDebts,
    private val getAllHumansUseCase: GetAllHumansUseCase
): ViewModel() {

    private val TAG = "SyncFragmentVM"

    private val _isAuthorized = MutableLiveData<Boolean>()
    val isAuthorized: LiveData<Boolean>
        get() = _isAuthorized
    private val _userName = MutableLiveData<String>()
    val userName: LiveData<String>
        get() = _userName
    private val _emailAddress = MutableLiveData<String>()
    val emailAddress: LiveData<String>
        get() = _emailAddress
    private val _appDataInfoForSync = MutableLiveData<AppDataLists>()
    val appDataInfoForSync: LiveData<AppDataLists>
        get() = _appDataInfoForSync

    val disposeBag = CompositeDisposable()

    init {
        getIsAuthorized()
        getAppDataForSync()
    }

    override fun onCleared() {
        super.onCleared()
        disposeBag.dispose()
    }

    private fun getIsAuthorized() {
        _isAuthorized.value = getIsAuthorized.execute()
    }

    fun setIsAuthorized(isAuthorized: Boolean) {
        _isAuthorized.value = isAuthorized
        setIsAuthorized.execute(isAuthorized)
    }

    fun setUser(name: String?, email: String?) {
        //TODO SAVE IN SHARED PREFS
        if (name == null) {
            _userName.value = ""
        } else {
            _userName.value = name!!
        }

        if (email == null) {
            _emailAddress.value = ""
        } else {
            _emailAddress.value = email!!
        }
    }

    fun getAppDataForSync() {
        val disposable = Single.create {
            val humanList = getAllHumansUseCase.execute()
            val debtList = getAllDebts.execute()
            it.onSuccess(AppDataLists(humanList, debtList))
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _appDataInfoForSync.value = it
            }, {
                Log.e(TAG, it.stackTrace.toString())
            })
        disposeBag.add(disposable)
    }
}