package com.breckneck.debtbook.presentation.viewmodel

import android.util.Log
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

    val debtList = MutableLiveData<List<DebtDomain>>()
    val humanId = MutableLiveData<Int>()
    val overallSum = MutableLiveData<Double>()
    val isOrderDialogShown = MutableLiveData<Boolean>()
    val debtOrder = MutableLiveData<Pair<DebtOrderAttribute, Boolean>>()
    private val disposeBag = CompositeDisposable()
    private val sortDebtsUseCase by lazy { SortDebts() }

    init {
        Log.e("TAG", "Debt Fragment View Model Started")
    }

    override fun onCleared() {
        super.onCleared()
        disposeBag.clear()
        Log.e("TAG", "Debt Fragment View Model cleared")
    }

    fun getDebtOrder() {
        debtOrder.value = getDebtOrder.execute()
    }

    fun saveDebtOrder(order: Pair<DebtOrderAttribute, Boolean>) {
        setDebtOrder.execute(order = order)
    }

    fun getAllDebts() {
        val getDebtsSingle = Single.create {
            Log.e("TAG", "Open Debt Details of Human id = $humanId")
            it.onSuccess(getAllDebtsUseCase.execute(id = humanId.value!!))
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                debtList.value = it
                sortDebts()
                Log.e("TAG", "Debts load success")
            }, {
                it.printStackTrace()
            })
        disposeBag.add(getDebtsSingle)
    }

    fun sortDebts() {
        if (debtList.value != null)
            debtList.value = sortDebtsUseCase.execute(debtList = debtList.value!!, order = debtOrder.value!!)
    }

    fun getAllInfoAboutNewHuman() {
        val getLastHumanIdSingle = Single.create {
            it.onSuccess(getLastHumanIdUseCase.exectute())
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                humanId.value = it
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
                overallSum.value = it
            }, {
                it.printStackTrace()
            })
        disposeBag.add(getOverallSumSingle)
    }

    fun deleteHuman() {
        val deleteHumanCompletable = Completable.create {
            deleteHumanUseCase.execute(id = humanId.value!!)
            deleteDebtsByHumanIdUseCase.execute(id = humanId.value!!)
            it.onComplete()
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.e("TAG", "Human with id = ${humanId.value} deleted")
            },{
                it.printStackTrace()
            })
        disposeBag.add(deleteHumanCompletable)
    }

    fun deleteDebt(debtDomain: DebtDomain) {
        val deleteDebtCompletable = Completable.create {
            deleteDebtUseCase.execute(debtDomain)
            addSumUseCase.execute(humanId = humanId.value!!, sum = (debtDomain.sum * (-1.0)))
            getOverallSum()
            Log.e("TAG", "Debt delete success")
            it.onComplete()
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                getAllDebts()
                Log.e("TAG", "Debts load success")
            },{
                it.printStackTrace()
            })
        disposeBag.add(deleteDebtCompletable)
    }
}