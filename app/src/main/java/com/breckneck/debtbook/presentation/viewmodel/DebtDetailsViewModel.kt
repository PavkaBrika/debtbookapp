package com.breckneck.debtbook.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.breckneck.deptbook.domain.model.DebtDomain

class DebtDetailsViewModel: ViewModel() {


    val debtList = MutableLiveData<List<DebtDomain>>()
    val overallSum = MutableLiveData<Double>()

    init {
        Log.e("TAG", "Debt Fragment View Model Started")
    }

    override fun onCleared() {
        super.onCleared()
        Log.e("TAG", "Debt Fragment View Model cleared")
    }


}