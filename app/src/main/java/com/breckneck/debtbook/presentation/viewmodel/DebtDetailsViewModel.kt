package com.breckneck.debtbook.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.breckneck.deptbook.domain.model.DebtDomain
import com.breckneck.deptbook.domain.usecase.Debt.DeleteDebtUseCase
import com.breckneck.deptbook.domain.usecase.Debt.DeleteDebtsByHumanIdUseCase
import com.breckneck.deptbook.domain.usecase.Debt.GetAllDebtsUseCase
import com.breckneck.deptbook.domain.usecase.Debt.SortDebts
import com.breckneck.deptbook.domain.usecase.Human.AddSumUseCase
import com.breckneck.deptbook.domain.usecase.Human.DeleteHumanUseCase
import com.breckneck.deptbook.domain.usecase.Human.GetHumanSumDebtUseCase
import com.breckneck.deptbook.domain.usecase.Human.GetLastHumanIdUseCase
import com.breckneck.deptbook.domain.usecase.Settings.GetDebtOrder
import com.breckneck.deptbook.domain.usecase.Settings.SetDebtOrder
import com.breckneck.deptbook.domain.util.DebtOrderAttribute
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class DebtDetailsViewModel(
    private val getAllDebtsUseCase: GetAllDebtsUseCase,
    private val getLastHumanIdUseCase: GetLastHumanIdUseCase,
    private val getHumanSumDebtUseCase: GetHumanSumDebtUseCase,
    private val deleteHumanUseCase: DeleteHumanUseCase,
    private val deleteDebtsByHumanIdUseCase: DeleteDebtsByHumanIdUseCase,
    private val deleteDebtUseCase: DeleteDebtUseCase,
    private val addSumUseCase: AddSumUseCase,
    private val getDebtOrder: GetDebtOrder,
    private val setDebtOrder: SetDebtOrder
): ViewModel() {

    private val TAG = "DebtDetailsViewModel"

    private val _debtList = MutableLiveData<List<DebtDomain>>()
    val debtList: LiveData<List<DebtDomain>>
        get() = _debtList
    private val _humanId = MutableLiveData<Int>()
    val humanId: LiveData<Int>
        get() = _humanId
    private val _overallSum = MutableLiveData<Double>()
    val overallSum: LiveData<Double>
        get() = _overallSum
    private val _isOrderDialogShown by lazy { MutableLiveData<Boolean>() }
    val isOrderDialogShown: LiveData<Boolean>
        get() = _isOrderDialogShown
    private val _isHumanDeleteDialogShown by lazy { MutableLiveData<Boolean>() }
    val isHumanDeleteDialogShown: LiveData<Boolean>
        get() = _isHumanDeleteDialogShown
    private val _isShareDialogShown by lazy { MutableLiveData<Boolean>() }
    val isShareDialogShown: LiveData<Boolean>
        get() = _isShareDialogShown
    private val _isDebtSettingsDialogShown by lazy { MutableLiveData<Boolean>() }
    val isDebtSettingsDialogShown: LiveData<Boolean>
        get() = _isDebtSettingsDialogShown
    private val _settingDebt by lazy { MutableLiveData<DebtDomain>() }
    val settingDebt: LiveData<DebtDomain>
        get() = _settingDebt
    private val _debtOrder = MutableLiveData<Pair<DebtOrderAttribute, Boolean>>()
    val debtOrder: LiveData<Pair<DebtOrderAttribute, Boolean>>
        get() = _debtOrder
    private val disposeBag = CompositeDisposable()
    private val sortDebtsUseCase by lazy { SortDebts() }

    init {
        Log.e(TAG, "Debt Fragment View Model Started")
    }

    override fun onCleared() {
        super.onCleared()
        disposeBag.clear()
        Log.e(TAG, "Debt Fragment View Model cleared")
    }

    fun getDebtOrder() {
        _debtOrder.value = getDebtOrder.execute()
    }

    fun saveDebtOrder(order: Pair<DebtOrderAttribute, Boolean>) {
        setDebtOrder.execute(order = order)
    }

    fun getAllDebts() {
        val getDebtsSingle = Single.create {
            Log.e(TAG, "Open Debt Details of Human id = $humanId")
            it.onSuccess(getAllDebtsUseCase.execute(id = humanId.value!!))
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _debtList.value = it
                sortDebts()
                Log.e(TAG, "Debts load success")
            }, {
                it.printStackTrace()
            })
        disposeBag.add(getDebtsSingle)
    }

    fun sortDebts() {
        if (_debtList.value != null)
            _debtList.value = sortDebtsUseCase.execute(debtList = _debtList.value!!, order = _debtOrder.value!!)
    }

    fun getAllInfoAboutNewHuman() {
        val getLastHumanIdSingle = Single.create {
            it.onSuccess(getLastHumanIdUseCase.exectute())
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _humanId.value = it
                getDebtOrder()
                getAllDebts()
                getOverallSum()
            }, {
                it.printStackTrace()
            })
        disposeBag.add(getLastHumanIdSingle)
    }

    fun getOverallSum() {
        val getOverallSumSingle = Single.create {
            it.onSuccess(getHumanSumDebtUseCase.execute(humanId = humanId.value!!))
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _overallSum.value = it
            }, {
                it.printStackTrace()
            })
        disposeBag.add(getOverallSumSingle)
    }

    fun deleteHuman() {
        val deleteHumanCompletable = Completable.create {
            deleteHumanUseCase.execute(id = _humanId.value!!)
            deleteDebtsByHumanIdUseCase.execute(id = _humanId.value!!)
            it.onComplete()
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.e(TAG, "Human with id = ${_humanId.value} deleted")
            },{
                it.printStackTrace()
            })
        disposeBag.add(deleteHumanCompletable)
    }

    fun deleteDebt(debtDomain: DebtDomain) {
        val deleteDebtCompletable = Completable.create {
            deleteDebtUseCase.execute(debtDomain)
            addSumUseCase.execute(humanId = _humanId.value!!, sum = (debtDomain.sum * (-1.0)))
            getOverallSum()
            Log.e(TAG, "Debt delete success")
            it.onComplete()
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                getAllDebts()
                Log.e(TAG, "Debts load success")
            },{
                it.printStackTrace()
            })
        disposeBag.add(deleteDebtCompletable)
    }

    fun onSetHumanId(id: Int) {
        _humanId.value = id
    }

    fun onHumanDeleteDialogOpen() {
        _isHumanDeleteDialogShown.value = true
    }

    fun onHumanDeleteDialogClose() {
        _isHumanDeleteDialogShown.value = false
    }

    fun onDebtSettingsDialogOpen() {
        _isDebtSettingsDialogShown.value = true
    }

    fun onDebtSettingsDialogClose() {
        _isDebtSettingsDialogShown.value = false
    }

    fun onSetSettingDebt(debt: DebtDomain) {
        _settingDebt.value = debt
    }

    fun onShareDialogOpen() {
        _isShareDialogShown.value = true
    }

    fun onShareDialogClose() {
        _isShareDialogShown.value = false
    }

    fun onOrderDialogOpen() {
        _isOrderDialogShown.value = true
    }

    fun onOrderDialogClose() {
        _isOrderDialogShown.value = false
    }

    fun onSetDebtOrder(order: Pair<DebtOrderAttribute, Boolean>) {
        _debtOrder.value = order
    }
}