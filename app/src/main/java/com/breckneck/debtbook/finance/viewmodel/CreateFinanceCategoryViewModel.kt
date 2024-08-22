package com.breckneck.debtbook.finance.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.breckneck.deptbook.domain.model.FinanceCategory
import com.breckneck.deptbook.domain.usecase.FinanceCategory.SetFinanceCategory
import com.breckneck.deptbook.domain.util.FinanceCategoryState
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class CreateFinanceCategoryViewModel(
    private val setFinanceCategory: SetFinanceCategory
): ViewModel() {

    val TAG = "CreateFinanceCatFragVM"

    private val disposeBag = CompositeDisposable()

    private val _checkedImage = MutableLiveData<Int>()
    val checkedImage: LiveData<Int>
        get() = _checkedImage
    private val _checkedImagePosition = MutableLiveData<Int>()
    val checkedImagePosition: LiveData<Int>
        get() = _checkedImagePosition
    private val _checkedColor = MutableLiveData<String>()
    val checkedColor: LiveData<String>
        get() = _checkedColor
    private val _checkedColorPosition = MutableLiveData<Int>()
    val checkedColorPosition: LiveData<Int>
        get() = _checkedColorPosition
    private val _financeCategoryState = MutableLiveData<FinanceCategoryState>()
    val financeCategoryState: LiveData<FinanceCategoryState>
        get() = _financeCategoryState

    init {
        Log.e(TAG, "Initialized")
    }

    override fun onCleared() {
        super.onCleared()
        disposeBag.clear()
        Log.e(TAG, "Cleared")
    }

    fun setCheckedImage(image: Int) {
        _checkedImage.value = image
    }

    fun setCheckedImagePosition(position: Int) {
        _checkedImagePosition.value = position
    }

    fun setCheckedColor(color: String) {
        _checkedColor.value = color
    }

    fun setCheckedColorPosition(position: Int) {
        _checkedColorPosition.value = position
    }

    fun setFinanceCategory(financeCategory: FinanceCategory) {
        val result = Completable.create {
            setFinanceCategory.execute(financeCategory = financeCategory)
            it.onComplete()
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.e(TAG, "New category added")
            }, {
                Log.e(TAG, it.message.toString())
            })
        disposeBag.add(result)
    }

    fun setFinanceCategoryState(financeCategoryState: FinanceCategoryState) {
        _financeCategoryState.value = financeCategoryState
    }
}