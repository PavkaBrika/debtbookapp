package com.breckneck.debtbook.finance.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.breckneck.deptbook.domain.model.FinanceCategory
import com.breckneck.deptbook.domain.usecase.FinanceCategory.SetFinanceCategory
import com.breckneck.deptbook.domain.util.FinanceCategoryState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CreateFinanceCategoryViewModel(
    private val setFinanceCategory: SetFinanceCategory
): ViewModel() {

    val TAG = "CreateFinanceCatFragVM"

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
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    setFinanceCategory.execute(financeCategory = financeCategory)
                }
                Log.e(TAG, "New category added")
            } catch (e: Exception) {
                Log.e(TAG, e.message.toString())
            }
        }
    }

    fun setFinanceCategoryState(financeCategoryState: FinanceCategoryState) {
        _financeCategoryState.value = financeCategoryState
    }
}
