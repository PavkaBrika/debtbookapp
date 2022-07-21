package com.breckneck.debtbook.presentation.viewmodel.newdebtfragment

import android.util.Log
import androidx.lifecycle.ViewModel

class NewDebtFragmentVM : ViewModel() {

    init {
        Log.e("TAG", "NewDebtFragment VM created")
    }

    override fun onCleared() {
        Log.e("TAG", "NewDebtFragment VM cleared")
        super.onCleared()
    }
}