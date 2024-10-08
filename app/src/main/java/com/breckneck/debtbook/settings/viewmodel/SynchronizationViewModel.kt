package com.breckneck.debtbook.settings.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.breckneck.debtbook.settings.util.DriveServiceHelper
import com.breckneck.deptbook.domain.model.AppDataLists
import com.breckneck.deptbook.domain.usecase.Debt.GetAllDebts
import com.breckneck.deptbook.domain.usecase.Debt.ReplaceAllDebts
import com.breckneck.deptbook.domain.usecase.Debt.SetDateUseCase
import com.breckneck.deptbook.domain.usecase.Finance.GetAllFinances
import com.breckneck.deptbook.domain.usecase.Finance.ReplaceAllFinances
import com.breckneck.deptbook.domain.usecase.FinanceCategory.GetAllFinanceCategories
import com.breckneck.deptbook.domain.usecase.FinanceCategory.ReplaceAllFinanceCategories
import com.breckneck.deptbook.domain.usecase.Goal.GetAllGoals
import com.breckneck.deptbook.domain.usecase.Goal.ReplaceAllGoals
import com.breckneck.deptbook.domain.usecase.GoalDeposit.GetAllGoalDeposits
import com.breckneck.deptbook.domain.usecase.GoalDeposit.ReplaceAllGoalsDeposits
import com.breckneck.deptbook.domain.usecase.Human.GetAllHumansUseCase
import com.breckneck.deptbook.domain.usecase.Human.ReplaceAllHumans
import com.breckneck.deptbook.domain.usecase.Settings.GetIsAuthorized
import com.breckneck.deptbook.domain.usecase.Settings.GetLastSyncDate
import com.breckneck.deptbook.domain.usecase.Settings.SetIsAuthorized
import com.breckneck.deptbook.domain.usecase.Settings.SetLastSyncDate
import com.breckneck.deptbook.domain.usecase.Settings.SetUserData
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.Calendar

class SynchronizationViewModel(
    private val getIsAuthorized: GetIsAuthorized,
    private val setIsAuthorized: SetIsAuthorized,
    private val getAllDebts: GetAllDebts,
    private val getAllHumansUseCase: GetAllHumansUseCase,
    private val replaceAllDebts: ReplaceAllDebts,
    private val replaceAllHumans: ReplaceAllHumans,
    private val setUserData: SetUserData,
    private val setDateUseCase: SetDateUseCase,
    private val setLastSyncDate: SetLastSyncDate,
    private val getLastSyncDate: GetLastSyncDate,
    private val getAllFinances: GetAllFinances,
    private val getAllFinanceCategories: GetAllFinanceCategories,
    private val replaceAllFinances: ReplaceAllFinances,
    private val replaceAllFinanceCategories: ReplaceAllFinanceCategories,
    private val getAllGoals: GetAllGoals,
    private val getAllGoalDeposits: GetAllGoalDeposits,
    private val replaceAllGoals: ReplaceAllGoals,
    private val replaceAllGoalDeposits: ReplaceAllGoalsDeposits
) : ViewModel() {

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
    private val _fileId = MutableLiveData<String>(null)
    val fileId: LiveData<String>
        get() = _fileId
    private var _driveServiceHelper = MutableLiveData<DriveServiceHelper>()
    val driveServiceHelper: LiveData<DriveServiceHelper>
        get() = _driveServiceHelper
    private val _isSynchronizing = MutableLiveData<Boolean>(false)
    val isSynchronizing: LiveData<Boolean>
        get() = _isSynchronizing
    private val _isRestoring = MutableLiveData<Boolean>(false)
    val isRestoring: LiveData<Boolean>
        get() = _isRestoring
    private val _isRestoreDialogOpened = MutableLiveData<Boolean>(false)
    val isRestoreDialogOpened: LiveData<Boolean>
        get() = _isRestoreDialogOpened
    private val _isLogOutDialogOpened = MutableLiveData<Boolean>(false)
    val isLogOutDialogOpened: LiveData<Boolean>
        get() = _isLogOutDialogOpened
    private val _isListModified = MutableLiveData<Boolean>(false)
    val isListModified: LiveData<Boolean>
        get() = _isListModified
    private val _lastSyncDate = MutableLiveData<String>()
    val lastSyncDate: LiveData<String>
        get() = _lastSyncDate

    val disposeBag = CompositeDisposable()

    init {
        getIsAuthorized()
        getAppDataForSync()
        getLastSyncDate()
    }

    override fun onCleared() {
        super.onCleared()
        disposeBag.dispose()
    }

    fun setLastSyncDate() {
        val calendar = Calendar.getInstance()
        setLastSyncDate.execute(calendar.timeInMillis)
        val date = setDateUseCase.execute(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        _lastSyncDate.value = date
    }

    private fun getLastSyncDate() {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = getLastSyncDate.execute()
        if (calendar.timeInMillis == 0L) {
            _lastSyncDate.value = ""
        } else {
            val date = setDateUseCase.execute(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            _lastSyncDate.value = date
        }
    }

    private fun getIsAuthorized() {
        _isAuthorized.value = getIsAuthorized.execute()
    }

    fun setIsAuthorized(isAuthorized: Boolean) {
        _isAuthorized.value = isAuthorized
        setIsAuthorized.execute(isAuthorized)
    }

    fun setUser(name: String?, email: String?) {
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
        setUserData.execute(userName.value!!, emailAddress.value!!)
    }

    fun getAppDataForSync() {
        val disposable = Single.create {
            it.onSuccess(
                AppDataLists(
                    humanList = getAllHumansUseCase.execute(),
                    debtList = getAllDebts.execute(),
                    financeList = getAllFinances.execute(),
                    financeCategoryList = getAllFinanceCategories.execute(),
                    goalList = getAllGoals.execute(),
                    goalDepositList = getAllGoalDeposits.execute()
                )
            )
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

    fun replaceAllData(appDataLists: AppDataLists) {
        val disposable = Completable.create {
            replaceAllHumans.execute(appDataLists.humanList)
            replaceAllDebts.execute(appDataLists.debtList)
            replaceAllFinanceCategories.execute(appDataLists.financeCategoryList)
            replaceAllFinances.execute(appDataLists.financeList)
            replaceAllGoals.execute(appDataLists.goalList)
            replaceAllGoalDeposits.execute(appDataLists.goalDepositList)
            it.onComplete()
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _isRestoring.value = false
                _isListModified.value = true
            }, {
                Log.e(TAG, it.stackTrace.toString())
            })
        disposeBag.add(disposable)
    }

    fun setFileId(fileId: String) {
        _fileId.value = fileId
    }

    fun setDriveServiceHelper(helper: DriveServiceHelper) {
        _driveServiceHelper.value = helper
    }

    fun setIsSynchronizing(isSynchronizing: Boolean) {
        _isSynchronizing.value = isSynchronizing
    }

    fun setIsRestoring(isRestoring: Boolean) {
        _isRestoring.value = isRestoring
    }

    fun setIsRestoreDialogOpened(opened: Boolean) {
        _isRestoreDialogOpened.value = opened
    }

    fun setIsLogOutDialogOpened(opened: Boolean) {
        _isLogOutDialogOpened.value = opened
    }
}